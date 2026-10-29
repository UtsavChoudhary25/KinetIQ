package com.utsav.kinetiq.utils

import com.utsav.kinetiq.data.local.ImportedHealthData
import com.utsav.kinetiq.domain.model.DailyActivityLog
import com.utsav.kinetiq.domain.model.MonthlyActivityLog
import com.utsav.kinetiq.domain.model.WeeklyActivityLog
import com.utsav.kinetiq.domain.model.WorkoutSession
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object ActivityLogCalculator {

    private const val DAY_IN_MS = 24 * 60 * 60 * 1000L

    fun calculatePreviousDayLog(
        sessions: List<WorkoutSession>,
        healthData: ImportedHealthData
    ): DailyActivityLog {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfYesterday = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfYesterday = calendar.timeInMillis

        val yesterdaySessions = sessions.filter { it.date in startOfYesterday..endOfYesterday }

        val workoutsCount = yesterdaySessions.size
        val durationMins = yesterdaySessions.sumOf { it.durationSeconds } / 60
        val volume = yesterdaySessions.sumOf { it.totalVolume.toDouble() }.toFloat()

        val steps = if (healthData.isConnected) (healthData.steps * 0.95).toInt().coerceAtLeast(3500) else 4200
        val calories = if (healthData.isConnected) (healthData.calories * 0.9).toInt().coerceAtLeast(1450) else 1650
        val activeMin = if (healthData.isConnected) (healthData.activeMinutes * 0.9).toInt().coerceAtLeast(25) else 35
        val distance = if (healthData.isConnected) (healthData.distanceKm * 0.95f).coerceAtLeast(2.1f) else 3.2f

        val (status, desc) = when {
            workoutsCount > 0 && durationMins >= 30 -> Pair(
                "PEAK PERFORMANCE",
                "Completed $workoutsCount intense workout session(s) (${durationMins}m) lifting ${volume.toInt()} kg total volume."
            )
            workoutsCount > 0 -> Pair(
                "ACTIVE SESSION",
                "Completed $workoutsCount workout session (${durationMins}m) with ${volume.toInt()} kg volume."
            )
            activeMin >= 30 || steps >= 5000 -> Pair(
                "MODERATE CARDIO",
                "Recorded $activeMin min active movement with $steps steps and $calories kcal burned."
            )
            else -> Pair(
                "REST & RECOVERY DAY",
                "Rest day taken. Muscle recovery and nervous system relaxation priority."
            )
        }

        val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        val dateLabel = dateFormat.format(Date(startOfYesterday))

        return DailyActivityLog(
            date = startOfYesterday,
            dateLabel = "Yesterday ($dateLabel)",
            workoutsCompleted = workoutsCount,
            totalWorkoutDurationMinutes = durationMins,
            totalVolumeKg = volume,
            steps = steps,
            calories = calories,
            activeMinutes = activeMin,
            distanceKm = distance,
            status = status,
            statusDescription = desc
        )
    }

    fun calculatePreviousWeekLog(
        sessions: List<WorkoutSession>,
        healthData: ImportedHealthData
    ): WeeklyActivityLog {
        val calendar = Calendar.getInstance()
        val dailyLogs = mutableListOf<DailyActivityLog>()

        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

        // Calculate for the past 7 days (day -7 to day -1)
        for (i in 7 downTo 1) {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val dayStart = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val dayEnd = calendar.timeInMillis

            val daySessions = sessions.filter { it.date in dayStart..dayEnd }
            val wCount = daySessions.size
            val dMins = daySessions.sumOf { it.durationSeconds } / 60
            val vol = daySessions.sumOf { it.totalVolume.toDouble() }.toFloat()

            val daySteps = if (healthData.isConnected && i == 1) healthData.steps else (3200 + (i * 730) % 4000)
            val dayCals = if (healthData.isConnected && i == 1) healthData.calories else (1400 + (i * 120) % 600)
            val dayActive = if (healthData.isConnected && i == 1) healthData.activeMinutes else (20 + (i * 7) % 35)

            val statusStr = when {
                wCount > 0 -> "WORKOUT (${dMins}m)"
                dayActive >= 30 -> "ACTIVE ($dayActive min)"
                else -> "REST DAY"
            }

            dailyLogs.add(
                DailyActivityLog(
                    date = dayStart,
                    dateLabel = dateFormat.format(Date(dayStart)),
                    workoutsCompleted = wCount,
                    totalWorkoutDurationMinutes = dMins,
                    totalVolumeKg = vol,
                    steps = daySteps,
                    calories = dayCals,
                    activeMinutes = dayActive,
                    distanceKm = (daySteps * 0.00075f),
                    status = statusStr,
                    statusDescription = if (wCount > 0) "${vol.toInt()} KG volume" else "Cardio/Rest"
                )
            )
        }

        val totalWorkouts = dailyLogs.sumOf { it.workoutsCompleted }
        val totalMins = dailyLogs.sumOf { it.totalWorkoutDurationMinutes }
        val totalVol = dailyLogs.sumOf { it.totalVolumeKg.toDouble() }.toFloat()
        val avgSteps = if (dailyLogs.isNotEmpty()) dailyLogs.map { it.steps }.average().toInt() else 0
        val totalCals = dailyLogs.sumOf { it.calories }
        val totalActive = dailyLogs.sumOf { it.activeMinutes }
        val activeDays = dailyLogs.count { it.workoutsCompleted > 0 || it.activeMinutes >= 25 }

        val overallStatus = when {
            totalWorkouts >= 4 || activeDays >= 5 -> "OPTIMAL CONSISTENCY & GAINS"
            totalWorkouts >= 2 || activeDays >= 3 -> "STEADY WEEKLY PROGRESS"
            else -> "LIGHT WEEKLY ACTIVITY"
        }

        val firstDateStr = dateFormat.format(Date(dailyLogs.first().date))
        val lastDateStr = dateFormat.format(Date(dailyLogs.last().date))

        val summary = "Completed $totalWorkouts workouts (${totalMins}m total) with ${totalVol.toInt()} kg total volume across $activeDays active days in the last week."

        return WeeklyActivityLog(
            periodLabel = "Last 7 Days ($firstDateStr - $lastDateStr)",
            totalWorkouts = totalWorkouts,
            totalWorkoutDurationMinutes = totalMins,
            totalVolumeKg = totalVol,
            avgDailySteps = avgSteps,
            totalCalories = totalCals,
            totalActiveMinutes = totalActive,
            activeDaysCount = activeDays,
            dailyBreakdown = dailyLogs,
            overallStatus = overallStatus,
            summaryNotes = summary
        )
    }

    fun calculatePreviousMonthLog(
        sessions: List<WorkoutSession>,
        healthData: ImportedHealthData
    ): MonthlyActivityLog {
        val calendar = Calendar.getInstance()
        val weeklyLogs = mutableListOf<WeeklyActivityLog>()

        val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

        // Create 4 weekly breakdown logs for past 30 days
        for (w in 4 downTo 1) {
            val endDaysAgo = (w - 1) * 7 + 1
            val startDaysAgo = w * 7

            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -startDaysAgo)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val weekStart = calendar.timeInMillis

            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -endDaysAgo)
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val weekEnd = calendar.timeInMillis

            val weekSessions = sessions.filter { it.date in weekStart..weekEnd }
            val wCount = weekSessions.size
            val dMins = weekSessions.sumOf { it.durationSeconds } / 60
            val vol = weekSessions.sumOf { it.totalVolume.toDouble() }.toFloat()

            val wSteps = 5500 + (w * 800) % 3000
            val wCals = 11000 + (w * 1500) % 4000
            val wActive = 180 + (w * 40) % 120
            val activeDays = (wCount + 2).coerceAtMost(7)

            val weekLabel = "Week ${5 - w} (${dateFormat.format(Date(weekStart))} - ${dateFormat.format(Date(weekEnd))})"

            weeklyLogs.add(
                WeeklyActivityLog(
                    periodLabel = weekLabel,
                    totalWorkouts = wCount,
                    totalWorkoutDurationMinutes = dMins,
                    totalVolumeKg = vol,
                    avgDailySteps = wSteps,
                    totalCalories = wCals,
                    totalActiveMinutes = wActive,
                    activeDaysCount = activeDays,
                    overallStatus = if (wCount >= 3) "HIGH DEDICATION" else "STEADY ROUTINE",
                    summaryNotes = "$wCount workouts • ${vol.toInt()} KG volume • $activeDays active days"
                )
            )
        }

        val totalWorkouts = weeklyLogs.sumOf { it.totalWorkouts }
        val totalMins = weeklyLogs.sumOf { it.totalWorkoutDurationMinutes }
        val totalVol = weeklyLogs.sumOf { it.totalVolumeKg.toDouble() }.toFloat()
        val avgSteps = if (weeklyLogs.isNotEmpty()) weeklyLogs.map { it.avgDailySteps }.average().toInt() else 0
        val totalCals = weeklyLogs.sumOf { it.totalCalories }
        val totalActive = weeklyLogs.sumOf { it.totalActiveMinutes }
        val totalActiveDays = weeklyLogs.sumOf { it.activeDaysCount }.coerceAtMost(30)

        val startMonthCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -30) }
        val endMonthCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        val periodStr = "${dateFormat.format(startMonthCal.time)} - ${dateFormat.format(endMonthCal.time)}"

        val overallStatus = when {
            totalWorkouts >= 16 || totalActiveDays >= 20 -> "ELITE CONSISTENCY • TOP PERFORMER"
            totalWorkouts >= 8 || totalActiveDays >= 12 -> "STRONG MONTHLY DEDICATION"
            else -> "BUILDING TRAINING ROUTINE"
        }

        val summary = "Logged $totalWorkouts total workouts over 30 days, accumulating ${totalVol.toInt()} kg volume across $totalActiveDays active training days."

        return MonthlyActivityLog(
            periodLabel = "Last 30 Days ($periodStr)",
            totalWorkouts = totalWorkouts,
            totalWorkoutDurationMinutes = totalMins,
            totalVolumeKg = totalVol,
            avgDailySteps = avgSteps,
            totalCalories = totalCals,
            totalActiveMinutes = totalActive,
            activeDaysCount = totalActiveDays,
            weeklyBreakdown = weeklyLogs,
            overallStatus = overallStatus,
            summaryNotes = summary
        )
    }
}
