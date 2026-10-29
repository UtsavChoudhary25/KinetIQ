package com.utsav.kinetiq.data.health

import android.content.Context
import android.os.Build
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.utsav.kinetiq.data.local.HealthPrefs
import com.utsav.kinetiq.data.local.ImportedHealthData
import java.time.Instant
import java.util.Calendar

class HealthConnectManager(private val context: Context) {

    private val healthPrefs = HealthPrefs(context)

    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class)
    )

    fun isHealthConnectAvailable(): Boolean {
        return HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
    }

    private fun getHealthConnectClient(): HealthConnectClient? {
        return if (isHealthConnectAvailable()) {
            HealthConnectClient.getOrCreate(context)
        } else {
            null
        }
    }

    suspend fun hasAllPermissions(): Boolean {
        val client = getHealthConnectClient() ?: return false
        val granted = client.permissionController.getGrantedPermissions()
        return granted.containsAll(permissions)
    }

    suspend fun fetchTodayHealthData(): ImportedHealthData {
        val client = getHealthConnectClient()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDayMs = calendar.timeInMillis
        val nowMs = System.currentTimeMillis()

        var steps = 0
        var calories = 0
        var distanceKm = 0f
        var activeMinutes = 0
        var heartRate = 0
        var workoutsCount = 0

        if (client != null && hasAllPermissions() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val timeRangeFilter = TimeRangeFilter.between(
                    Instant.ofEpochMilli(startOfDayMs),
                    Instant.ofEpochMilli(nowMs)
                )

                // 1. Steps Record (Aggregate + Fallback to Summing Raw Samples)
                val stepsResponse = client.aggregate(
                    AggregateRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = timeRangeFilter
                    )
                )
                steps = stepsResponse[StepsRecord.COUNT_TOTAL]?.toInt() ?: 0

                if (steps == 0) {
                    val rawSteps = client.readRecords(
                        ReadRecordsRequest(
                            recordType = StepsRecord::class,
                            timeRangeFilter = timeRangeFilter
                        )
                    )
                    steps = rawSteps.records.sumOf { it.count.toInt() }
                }

                // 2. Calories Record (Fallback to Raw Samples)
                try {
                    val rawTotalCal = client.readRecords(
                        ReadRecordsRequest(
                            recordType = TotalCaloriesBurnedRecord::class,
                            timeRangeFilter = timeRangeFilter
                        )
                    )
                    calories = rawTotalCal.records.sumOf { it.energy.inKilocalories.toInt() }
                } catch (e: Exception) {
                    calories = 0
                }

                if (calories == 0) {
                    try {
                        val rawActiveCal = client.readRecords(
                            ReadRecordsRequest(
                                recordType = ActiveCaloriesBurnedRecord::class,
                                timeRangeFilter = timeRangeFilter
                            )
                        )
                        calories = rawActiveCal.records.sumOf { it.energy.inKilocalories.toInt() }
                    } catch (e: Exception) {
                        // ignore
                    }
                }

                // 3. Distance Record (Aggregate + Fallback to Summing Raw Samples)
                val distanceResponse = client.aggregate(
                    AggregateRequest(
                        metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
                        timeRangeFilter = timeRangeFilter
                    )
                )
                val totalDistance = distanceResponse[DistanceRecord.DISTANCE_TOTAL]
                distanceKm = ((totalDistance?.inMeters ?: 0.0) / 1000.0).toFloat()

                if (distanceKm == 0f) {
                    val rawDist = client.readRecords(
                        ReadRecordsRequest(
                            recordType = DistanceRecord::class,
                            timeRangeFilter = timeRangeFilter
                        )
                    )
                    val meters = rawDist.records.sumOf { it.distance.inMeters }
                    distanceKm = (meters / 1000.0).toFloat()
                }

                // 4. Heart Rate Record
                val heartRateRecords = client.readRecords(
                    ReadRecordsRequest(
                        recordType = HeartRateRecord::class,
                        timeRangeFilter = timeRangeFilter
                    )
                )
                if (heartRateRecords.records.isNotEmpty()) {
                    val samples = heartRateRecords.records.flatMap { it.samples }
                    if (samples.isNotEmpty()) {
                        heartRate = samples.map { it.beatsPerMinute }.average().toInt()
                    }
                }

                // 5. Exercise Sessions Record
                val exerciseRecords = client.readRecords(
                    ReadRecordsRequest(
                        recordType = ExerciseSessionRecord::class,
                        timeRangeFilter = timeRangeFilter
                    )
                )
                workoutsCount = exerciseRecords.records.size
                activeMinutes = exerciseRecords.records.sumOf { record ->
                    val durationSeconds = record.endTime.epochSecond - record.startTime.epochSecond
                    (durationSeconds / 60).toInt()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val importedData = ImportedHealthData(
            isConnected = true,
            lastSyncTimestamp = System.currentTimeMillis(),
            steps = steps,
            calories = calories,
            distanceKm = distanceKm,
            activeMinutes = activeMinutes,
            heartRate = heartRate,
            workoutsCount = workoutsCount
        )

        healthPrefs.saveHealthData(
            steps = steps,
            calories = calories,
            distanceKm = distanceKm,
            activeMinutes = activeMinutes,
            heartRate = heartRate,
            workoutsCount = workoutsCount
        )

        return importedData
    }

    suspend fun disconnectGoogleFit() {
        healthPrefs.setConnected(false)
    }
}
