package com.utsav.kinetiq.ui.workout.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime
import kotlin.math.cos
import kotlin.math.sin

enum class ExerciseType {
    PUSH_UP,
    SQUAT,
    BICEP_CURL,
    SHOULDER_PRESS,
    LUNGE,
    BENCH_PRESS,
    PULL_UP_ROW,
    PLANK_CRUNCH,
    JUMPING_JACKS,
    DEADLIFT,
    TRICEP_DIPS_EXTENSION,
    LATERAL_RAISE,
    MOUNTAIN_CLIMBERS,
    CALF_RAISE,
    RUSSIAN_TWIST,
    BURPEES,
    KETTLEBELL_SWING,
    FACE_PULL,
    BOX_JUMP,
    GENERIC
}

fun detectExerciseType(name: String, muscleGroup: String): ExerciseType {
    val lowerName = name.lowercase()
    val lowerMuscle = muscleGroup.lowercase()

    return when {
        lowerName.contains("deadlift") || lowerName.contains("rdl") -> ExerciseType.DEADLIFT
        lowerName.contains("push") && lowerName.contains("up") -> ExerciseType.PUSH_UP
        lowerName.contains("squat") || lowerName.contains("leg press") -> ExerciseType.SQUAT
        lowerName.contains("curl") && (lowerName.contains("bicep") || lowerName.contains("hammer") || lowerName.contains("barbell")) -> ExerciseType.BICEP_CURL
        lowerName.contains("lateral raise") || lowerName.contains("side raise") -> ExerciseType.LATERAL_RAISE
        lowerName.contains("press") && (lowerName.contains("shoulder") || lowerName.contains("overhead") || lowerName.contains("military")) -> ExerciseType.SHOULDER_PRESS
        lowerName.contains("tricep") || lowerName.contains("skull") || lowerName.contains("pushdown") || lowerName.contains("dip") -> ExerciseType.TRICEP_DIPS_EXTENSION
        lowerName.contains("lunge") || lowerName.contains("step up") -> ExerciseType.LUNGE
        lowerName.contains("bench") || lowerName.contains("chest press") || lowerName.contains("fly") -> ExerciseType.BENCH_PRESS
        lowerName.contains("pull") && lowerName.contains("face") -> ExerciseType.FACE_PULL
        lowerName.contains("pull") || lowerName.contains("row") || lowerName.contains("lat") || lowerMuscle.contains("back") -> ExerciseType.PULL_UP_ROW
        lowerName.contains("twist") || lowerName.contains("russian") -> ExerciseType.RUSSIAN_TWIST
        lowerName.contains("plank") || lowerName.contains("crunch") || lowerName.contains("rollout") || lowerName.contains("leg raise") || lowerMuscle.contains("core") || lowerMuscle.contains("abs") -> ExerciseType.PLANK_CRUNCH
        lowerName.contains("climber") || lowerName.contains("mountain") -> ExerciseType.MOUNTAIN_CLIMBERS
        lowerName.contains("calf") -> ExerciseType.CALF_RAISE
        lowerName.contains("swing") || lowerName.contains("kettlebell") -> ExerciseType.KETTLEBELL_SWING
        lowerName.contains("burpee") -> ExerciseType.BURPEES
        lowerName.contains("box jump") || lowerName.contains("plyo") -> ExerciseType.BOX_JUMP
        lowerName.contains("jack") || lowerName.contains("cardio") -> ExerciseType.JUMPING_JACKS
        lowerMuscle.contains("chest") -> ExerciseType.PUSH_UP
        lowerMuscle.contains("leg") || lowerMuscle.contains("quad") -> ExerciseType.SQUAT
        lowerMuscle.contains("hamstring") || lowerMuscle.contains("glute") -> ExerciseType.DEADLIFT
        lowerMuscle.contains("bicep") || lowerMuscle.contains("arm") -> ExerciseType.BICEP_CURL
        lowerMuscle.contains("tricep") -> ExerciseType.TRICEP_DIPS_EXTENSION
        lowerMuscle.contains("shoulder") -> ExerciseType.SHOULDER_PRESS
        else -> ExerciseType.GENERIC
    }
}

@Composable
fun ExerciseAnimationCard(
    exerciseName: String,
    muscleGroup: String,
    equipment: String = "",
    instructions: String = "",
    modifier: Modifier = Modifier
) {
    val exerciseType = remember(exerciseName, muscleGroup) {
        detectExerciseType(exerciseName, muscleGroup)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                colors = listOf(
                    KinetiqLime.copy(alpha = 0.35f),
                    Color.White.copy(alpha = 0.08f),
                    KinetiqLime.copy(alpha = 0.15f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            KinetiqLime.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exerciseName.uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = KinetiqLime.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = muscleGroup.uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = KinetiqLime,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp
                                )
                            )
                        }

                        if (equipment.isNotBlank()) {
                            Text(
                                text = "• $equipment",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = KinetiqGray,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Live Loop Badge
                Surface(
                    color = KinetiqLime,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(KinetiqBlack)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE MOTION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = KinetiqBlack,
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Animation Canvas Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                KinetiqBlack,
                                Color(0xFF141A14)
                            )
                        )
                    )
                    .border(1.dp, KinetiqGray.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                ExerciseAnimationCanvas(
                    type = exerciseType,
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (instructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = instructions,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = KinetiqGray,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}

@Composable
fun ExerciseAnimationCanvas(
    type: ExerciseType,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "exerciseAnimation")
    
    // Progress animation going smoothly from 0f to 1f and back (sine motion)
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "motionProgress"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val strokeColor = Color.White
    val accentColor = KinetiqLime
    val glowColor = KinetiqLime.copy(alpha = pulseAlpha)

    Canvas(modifier = modifier.padding(16.dp)) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f

        // Draw grid background lines for futuristic high-tech aesthetic
        val gridStep = 24.dp.toPx()
        var currentX = 0f
        while (currentX < width) {
            drawLine(
                color = Color.White.copy(alpha = 0.03f),
                start = Offset(currentX, 0f),
                end = Offset(currentX, height),
                strokeWidth = 1f
            )
            currentX += gridStep
        }

        when (type) {
            ExerciseType.DEADLIFT -> {
                val groundY = centerY + 40.dp.toPx()
                drawLine(
                    color = KinetiqGray.copy(alpha = 0.3f),
                    start = Offset(centerX - 60.dp.toPx(), groundY),
                    end = Offset(centerX + 60.dp.toPx(), groundY),
                    strokeWidth = 2.dp.toPx()
                )

                // Hinge & Stand
                val hingeFactor = (1f - progress)
                val hipY = centerY - 10.dp.toPx() + hingeFactor * 20.dp.toPx()
                val hipX = centerX - hingeFactor * 18.dp.toPx()
                val headY = hipY - 40.dp.toPx()
                val headX = centerX + hingeFactor * 15.dp.toPx()

                // Head & Torso
                drawCircle(color = strokeColor, radius = 9.dp.toPx(), center = Offset(headX, headY))
                drawLine(color = strokeColor, start = Offset(headX, headY + 9.dp.toPx()), end = Offset(hipX, hipY), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

                // Legs
                drawLine(color = strokeColor, start = Offset(hipX, hipY), end = Offset(centerX, groundY), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)

                // Barbell height
                val barY = groundY - 8.dp.toPx() - progress * 40.dp.toPx()
                val barX = centerX + 12.dp.toPx()
                drawLine(color = accentColor, start = Offset(barX, barY - 25.dp.toPx()), end = Offset(barX, barY + 25.dp.toPx()), strokeWidth = 4.dp.toPx())
                drawCircle(color = accentColor, radius = 8.dp.toPx(), center = Offset(barX, barY - 25.dp.toPx()))
                drawCircle(color = accentColor, radius = 8.dp.toPx(), center = Offset(barX, barY + 25.dp.toPx()))

                // Arms
                drawLine(color = strokeColor, start = Offset(headX - 5.dp.toPx(), headY + 18.dp.toPx()), end = Offset(barX, barY), strokeWidth = 4.dp.toPx())

                // Glutes & Hamstrings Target Glow
                drawCircle(color = glowColor, radius = 15.dp.toPx(), center = Offset(hipX, hipY + 10.dp.toPx()))
                drawCircle(color = accentColor, radius = 6.dp.toPx(), center = Offset(hipX, hipY + 10.dp.toPx()))
            }

            ExerciseType.TRICEP_DIPS_EXTENSION -> {
                val headY = centerY - 45.dp.toPx()
                val shoulderY = headY + 18.dp.toPx()
                val shoulderX = centerX

                // Head & Body
                drawCircle(color = strokeColor, radius = 10.dp.toPx(), center = Offset(centerX, headY))
                drawLine(color = strokeColor, start = Offset(centerX, headY + 10.dp.toPx()), end = Offset(centerX, centerY + 30.dp.toPx()), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

                // Tricep extension motion
                val extAngle = (1f - progress) * 110.0
                val rad = Math.toRadians(extAngle).toFloat()
                val elbowY = shoulderY - 15.dp.toPx()
                val handX = shoulderX + 25.dp.toPx() * sin(rad)
                val handY = elbowY - 25.dp.toPx() * cos(rad)

                drawLine(color = strokeColor, start = Offset(shoulderX, shoulderY), end = Offset(shoulderX + 10.dp.toPx(), elbowY), strokeWidth = 4.dp.toPx())
                drawLine(color = strokeColor, start = Offset(shoulderX + 10.dp.toPx(), elbowY), end = Offset(handX, handY), strokeWidth = 4.dp.toPx())
                drawCircle(color = accentColor, radius = 6.dp.toPx(), center = Offset(handX, handY))

                // Tricep Target Glow
                drawCircle(color = glowColor, radius = 12.dp.toPx(), center = Offset(shoulderX + 8.dp.toPx(), elbowY + 5.dp.toPx()))
                drawCircle(color = accentColor, radius = 5.dp.toPx(), center = Offset(shoulderX + 8.dp.toPx(), elbowY + 5.dp.toPx()))
            }

            ExerciseType.LATERAL_RAISE -> {
                val headY = centerY - 40.dp.toPx()
                val shoulderY = headY + 18.dp.toPx()

                // Head & Torso
                drawCircle(color = strokeColor, radius = 10.dp.toPx(), center = Offset(centerX, headY))
                drawLine(color = strokeColor, start = Offset(centerX, headY + 10.dp.toPx()), end = Offset(centerX, centerY + 35.dp.toPx()), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

                val raiseOffset = progress * 40.dp.toPx()

                // Left Arm
                drawLine(color = strokeColor, start = Offset(centerX, shoulderY), end = Offset(centerX - 12.dp.toPx() - raiseOffset, shoulderY + 25.dp.toPx() - raiseOffset), strokeWidth = 4.dp.toPx())
                drawCircle(color = accentColor, radius = 6.dp.toPx(), center = Offset(centerX - 12.dp.toPx() - raiseOffset, shoulderY + 25.dp.toPx() - raiseOffset))

                // Right Arm
                drawLine(color = strokeColor, start = Offset(centerX, shoulderY), end = Offset(centerX + 12.dp.toPx() + raiseOffset, shoulderY + 25.dp.toPx() - raiseOffset), strokeWidth = 4.dp.toPx())
                drawCircle(color = accentColor, radius = 6.dp.toPx(), center = Offset(centerX + 12.dp.toPx() + raiseOffset, shoulderY + 25.dp.toPx() - raiseOffset))

                // Lateral Deltoid Target Glow
                drawCircle(color = glowColor, radius = 14.dp.toPx(), center = Offset(centerX - 12.dp.toPx(), shoulderY))
                drawCircle(color = glowColor, radius = 14.dp.toPx(), center = Offset(centerX + 12.dp.toPx(), shoulderY))
            }

            ExerciseType.MOUNTAIN_CLIMBERS -> {
                val groundY = centerY + 30.dp.toPx()
                drawLine(color = KinetiqGray.copy(alpha = 0.3f), start = Offset(centerX - 70.dp.toPx(), groundY), end = Offset(centerX + 70.dp.toPx(), groundY), strokeWidth = 2.dp.toPx())

                val headX = centerX + 45.dp.toPx()
                val hipX = centerX - 15.dp.toPx()
                val bodyY = groundY - 25.dp.toPx()

                // Head & Torso
                drawCircle(color = strokeColor, radius = 9.dp.toPx(), center = Offset(headX, bodyY - 5.dp.toPx()))
                drawLine(color = strokeColor, start = Offset(headX - 9.dp.toPx(), bodyY), end = Offset(hipX, bodyY + 5.dp.toPx()), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

                // Arms
                drawLine(color = strokeColor, start = Offset(headX - 10.dp.toPx(), bodyY), end = Offset(headX - 10.dp.toPx(), groundY), strokeWidth = 4.dp.toPx())

                // Alternating Legs
                val legDrive = progress * 30.dp.toPx()
                // Left Leg (driven forward)
                val leftKneeX = hipX + legDrive
                drawLine(color = strokeColor, start = Offset(hipX, bodyY + 5.dp.toPx()), end = Offset(leftKneeX, groundY - 10.dp.toPx()), strokeWidth = 5.dp.toPx())

                // Right Leg (extended back)
                drawLine(color = strokeColor, start = Offset(hipX, bodyY + 5.dp.toPx()), end = Offset(centerX - 55.dp.toPx(), groundY), strokeWidth = 5.dp.toPx())

                // Abs Core Glow
                drawCircle(color = glowColor, radius = 14.dp.toPx(), center = Offset(hipX + 15.dp.toPx(), bodyY + 2.dp.toPx()))
                drawCircle(color = accentColor, radius = 6.dp.toPx(), center = Offset(hipX + 15.dp.toPx(), bodyY + 2.dp.toPx()))
            }

            ExerciseType.CALF_RAISE -> {
                val groundY = centerY + 45.dp.toPx()
                drawLine(color = KinetiqGray.copy(alpha = 0.3f), start = Offset(centerX - 50.dp.toPx(), groundY), end = Offset(centerX + 50.dp.toPx(), groundY), strokeWidth = 2.dp.toPx())

                val raiseHeight = progress * 20.dp.toPx()
                val headY = centerY - 55.dp.toPx() - raiseHeight

                // Head & Body
                drawCircle(color = strokeColor, radius = 10.dp.toPx(), center = Offset(centerX, headY))
                drawLine(color = strokeColor, start = Offset(centerX, headY + 10.dp.toPx()), end = Offset(centerX, groundY - 20.dp.toPx() - raiseHeight), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

                // Legs on toes
                val ankleY = groundY - 10.dp.toPx() - raiseHeight
                drawLine(color = strokeColor, start = Offset(centerX - 8.dp.toPx(), groundY - 20.dp.toPx() - raiseHeight), end = Offset(centerX - 8.dp.toPx(), ankleY), strokeWidth = 5.dp.toPx())
                drawLine(color = strokeColor, start = Offset(centerX + 8.dp.toPx(), groundY - 20.dp.toPx() - raiseHeight), end = Offset(centerX + 8.dp.toPx(), ankleY), strokeWidth = 5.dp.toPx())

                // Calf Target Glow
                val calfY = (ankleY + groundY - 20.dp.toPx() - raiseHeight) / 2f
                drawCircle(color = glowColor, radius = 14.dp.toPx(), center = Offset(centerX, calfY))
                drawCircle(color = accentColor, radius = 6.dp.toPx(), center = Offset(centerX, calfY))
            }

            ExerciseType.RUSSIAN_TWIST -> {
                val groundY = centerY + 30.dp.toPx()
                val hipX = centerX
                val hipY = groundY - 10.dp.toPx()

                // V-sit pose
                val headX = centerX - 20.dp.toPx()
                val headY = centerY - 35.dp.toPx()

                drawCircle(color = strokeColor, radius = 9.dp.toPx(), center = Offset(headX, headY))
                drawLine(color = strokeColor, start = Offset(headX, headY + 9.dp.toPx()), end = Offset(hipX, hipY), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

                // Raised Legs
                drawLine(color = strokeColor, start = Offset(hipX, hipY), end = Offset(centerX + 35.dp.toPx(), centerY - 15.dp.toPx()), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)

                // Twist weight motion
                val twistOffset = (sin(progress * Math.PI * 2) * 25.dp.toPx()).toFloat()
                drawCircle(color = accentColor, radius = 8.dp.toPx(), center = Offset(centerX + twistOffset, centerY - 5.dp.toPx()))

                // Obliques Glow
                drawCircle(color = glowColor, radius = 14.dp.toPx(), center = Offset(centerX - 8.dp.toPx(), centerY))
                drawCircle(color = accentColor, radius = 5.dp.toPx(), center = Offset(centerX - 8.dp.toPx(), centerY))
            }

            ExerciseType.BURPEES -> {
                val groundY = centerY + 35.dp.toPx()
                drawLine(color = KinetiqGray.copy(alpha = 0.3f), start = Offset(centerX - 60.dp.toPx(), groundY), end = Offset(centerX + 60.dp.toPx(), groundY), strokeWidth = 2.dp.toPx())

                val phase = (progress * 3f).toInt()
                val localProg = (progress * 3f) - phase

                when (phase) {
                    0 -> { // Drop to Pushup Plank
                        val bodyY = groundY - 15.dp.toPx()
                        drawCircle(color = strokeColor, radius = 8.dp.toPx(), center = Offset(centerX + 25.dp.toPx(), bodyY - 4.dp.toPx()))
                        drawLine(color = strokeColor, start = Offset(centerX - 35.dp.toPx(), bodyY), end = Offset(centerX + 25.dp.toPx(), bodyY), strokeWidth = 5.dp.toPx())
                    }
                    1 -> { // Squat Tuck
                        val hipY = groundY - 20.dp.toPx()
                        drawCircle(color = strokeColor, radius = 8.dp.toPx(), center = Offset(centerX, hipY - 25.dp.toPx()))
                        drawLine(color = strokeColor, start = Offset(centerX, hipY - 17.dp.toPx()), end = Offset(centerX, hipY), strokeWidth = 5.dp.toPx())
                    }
                    else -> { // Jump Overhead
                        val jumpY = localProg * 30.dp.toPx()
                        val headY = centerY - 45.dp.toPx() - jumpY
                        drawCircle(color = strokeColor, radius = 8.dp.toPx(), center = Offset(centerX, headY))
                        drawLine(color = strokeColor, start = Offset(centerX, headY + 8.dp.toPx()), end = Offset(centerX, centerY + 10.dp.toPx() - jumpY), strokeWidth = 5.dp.toPx())
                        // Hands up
                        drawLine(color = strokeColor, start = Offset(centerX, headY + 12.dp.toPx()), end = Offset(centerX - 15.dp.toPx(), headY - 15.dp.toPx()), strokeWidth = 4.dp.toPx())
                        drawLine(color = strokeColor, start = Offset(centerX, headY + 12.dp.toPx()), end = Offset(centerX + 15.dp.toPx(), headY - 15.dp.toPx()), strokeWidth = 4.dp.toPx())
                    }
                }

                drawCircle(color = glowColor, radius = 16.dp.toPx(), center = Offset(centerX, centerY))
            }

            ExerciseType.KETTLEBELL_SWING -> {
                val groundY = centerY + 40.dp.toPx()
                drawLine(color = KinetiqGray.copy(alpha = 0.3f), start = Offset(centerX - 50.dp.toPx(), groundY), end = Offset(centerX + 50.dp.toPx(), groundY), strokeWidth = 2.dp.toPx())

                val swingAngle = (progress - 0.5f) * 120.0
                val rad = Math.toRadians(swingAngle).toFloat()

                val hipY = centerY - 5.dp.toPx()
                val headY = hipY - 40.dp.toPx()

                // Torso hinge
                drawCircle(color = strokeColor, radius = 9.dp.toPx(), center = Offset(centerX, headY))
                drawLine(color = strokeColor, start = Offset(centerX, headY + 9.dp.toPx()), end = Offset(centerX, hipY), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

                // Arms & Kettlebell swing arc
                val kbLength = 35.dp.toPx()
                val kbX = centerX + kbLength * sin(rad)
                val kbY = hipY + 10.dp.toPx() - kbLength * cos(rad)

                drawLine(color = strokeColor, start = Offset(centerX, headY + 18.dp.toPx()), end = Offset(kbX, kbY), strokeWidth = 4.dp.toPx())
                drawCircle(color = accentColor, radius = 10.dp.toPx(), center = Offset(kbX, kbY))

                // Posterior Chain Glute Glow
                drawCircle(color = glowColor, radius = 14.dp.toPx(), center = Offset(centerX - 5.dp.toPx(), hipY))
                drawCircle(color = accentColor, radius = 5.dp.toPx(), center = Offset(centerX - 5.dp.toPx(), hipY))
            }

            ExerciseType.FACE_PULL -> {
                val headY = centerY - 35.dp.toPx()
                val shoulderY = headY + 18.dp.toPx()

                // Head & Torso
                drawCircle(color = strokeColor, radius = 10.dp.toPx(), center = Offset(centerX - 10.dp.toPx(), headY))
                drawLine(color = strokeColor, start = Offset(centerX - 10.dp.toPx(), headY + 10.dp.toPx()), end = Offset(centerX - 10.dp.toPx(), centerY + 30.dp.toPx()), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

                // Cable Pulley line
                val pulleyX = centerX + 50.dp.toPx()
                val pullProgress = progress * 30.dp.toPx()
                val handX = pulleyX - pullProgress

                drawLine(color = KinetiqGray, start = Offset(pulleyX, headY), end = Offset(handX, headY), strokeWidth = 3.dp.toPx())

                // Arms flared out
                drawLine(color = strokeColor, start = Offset(centerX - 10.dp.toPx(), shoulderY), end = Offset(handX, headY), strokeWidth = 4.dp.toPx())

                // Rear Delt Glow
                drawCircle(color = glowColor, radius = 14.dp.toPx(), center = Offset(centerX - 14.dp.toPx(), shoulderY))
                drawCircle(color = accentColor, radius = 6.dp.toPx(), center = Offset(centerX - 14.dp.toPx(), shoulderY))
            }

            ExerciseType.BOX_JUMP -> {
                val groundY = centerY + 40.dp.toPx()
                val boxX = centerX + 20.dp.toPx()
                val boxY = groundY - 30.dp.toPx()

                // Box
                drawPath(
                    path = Path().apply {
                        moveTo(boxX, groundY)
                        lineTo(boxX + 35.dp.toPx(), groundY)
                        lineTo(boxX + 35.dp.toPx(), boxY)
                        lineTo(boxX, boxY)
                        close()
                    },
                    color = KinetiqGray.copy(alpha = 0.4f),
                    style = Stroke(width = 2.dp.toPx())
                )

                // Jumper
                val jumperX = centerX - 25.dp.toPx() + progress * 45.dp.toPx()
                val jumperY = groundY - 45.dp.toPx() - (sin(progress * Math.PI) * 35.dp.toPx()).toFloat()

                drawCircle(color = strokeColor, radius = 9.dp.toPx(), center = Offset(jumperX, jumperY))
                drawLine(color = strokeColor, start = Offset(jumperX, jumperY + 9.dp.toPx()), end = Offset(jumperX, jumperY + 35.dp.toPx()), strokeWidth = 5.dp.toPx())

                // Explosive Leg Glow
                drawCircle(color = glowColor, radius = 14.dp.toPx(), center = Offset(jumperX, jumperY + 25.dp.toPx()))
            }

            ExerciseType.PUSH_UP -> {
                // Ground line
                val groundY = centerY + 35.dp.toPx()
                drawLine(
                    color = KinetiqGray.copy(alpha = 0.3f),
                    start = Offset(centerX - 80.dp.toPx(), groundY),
                    end = Offset(centerX + 80.dp.toPx(), groundY),
                    strokeWidth = 2.dp.toPx()
                )

                // Pushup Motion: Body moves up & down
                val heightOffset = (1f - progress) * 28.dp.toPx()
                val bodyY = groundY - 20.dp.toPx() - heightOffset

                val headX = centerX + 45.dp.toPx()
                val feetX = centerX - 55.dp.toPx()

                // Head
                drawCircle(color = strokeColor, radius = 9.dp.toPx(), center = Offset(headX + 12.dp.toPx(), bodyY - 4.dp.toPx()))

                // Body Plank Line
                drawLine(
                    color = strokeColor,
                    start = Offset(feetX, groundY - 6.dp.toPx()),
                    end = Offset(headX, bodyY),
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Arms bending at elbow
                val handX = headX - 5.dp.toPx()
                val handY = groundY - 2.dp.toPx()
                val shoulderX = headX - 8.dp.toPx()
                val shoulderY = bodyY

                val elbowX = (handX + shoulderX) / 2f + (1f - progress) * 15.dp.toPx()
                val elbowY = (handY + shoulderY) / 2f + (1f - progress) * 10.dp.toPx()

                val armPath = Path().apply {
                    moveTo(shoulderX, shoulderY)
                    lineTo(elbowX, elbowY)
                    lineTo(handX, handY)
                }
                drawPath(armPath, color = strokeColor, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))

                // Chest Target Muscle Highlight Glow
                drawCircle(color = glowColor, radius = 14.dp.toPx(), center = Offset(headX - 10.dp.toPx(), bodyY + 2.dp.toPx()))
                drawCircle(color = accentColor, radius = 6.dp.toPx(), center = Offset(headX - 10.dp.toPx(), bodyY + 2.dp.toPx()))
            }

            ExerciseType.SQUAT -> {
                // Ground line
                val groundY = centerY + 45.dp.toPx()
                drawLine(
                    color = KinetiqGray.copy(alpha = 0.3f),
                    start = Offset(centerX - 60.dp.toPx(), groundY),
                    end = Offset(centerX + 60.dp.toPx(), groundY),
                    strokeWidth = 2.dp.toPx()
                )

                // Squat Motion: Lowering hips
                val squatDepth = progress * 32.dp.toPx()
                val hipY = centerY - 10.dp.toPx() + squatDepth
                val headY = hipY - 45.dp.toPx()

                // Head
                drawCircle(color = strokeColor, radius = 10.dp.toPx(), center = Offset(centerX, headY))

                // Torso
                drawLine(
                    color = strokeColor,
                    start = Offset(centerX, headY + 10.dp.toPx()),
                    end = Offset(centerX - progress * 8.dp.toPx(), hipY),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Legs bending at knees
                val leftFootX = centerX - 18.dp.toPx()
                val rightFootX = centerX + 18.dp.toPx()
                val kneeY = (hipY + groundY) / 2f + progress * 8.dp.toPx()
                val kneeOutX = progress * 16.dp.toPx()

                // Left Leg
                val leftLegPath = Path().apply {
                    moveTo(centerX - progress * 4.dp.toPx(), hipY)
                    lineTo(leftFootX - kneeOutX, kneeY)
                    lineTo(leftFootX, groundY)
                }
                drawPath(leftLegPath, color = strokeColor, style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round))

                // Right Leg
                val rightLegPath = Path().apply {
                    moveTo(centerX + progress * 4.dp.toPx(), hipY)
                    lineTo(rightFootX + kneeOutX, kneeY)
                    lineTo(rightFootX, groundY)
                }
                drawPath(rightLegPath, color = strokeColor, style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round))

                // Arms out for balance
                drawLine(
                    color = strokeColor,
                    start = Offset(centerX, headY + 18.dp.toPx()),
                    end = Offset(centerX + 30.dp.toPx(), headY + 22.dp.toPx()),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Quadriceps Target Highlight
                drawCircle(color = glowColor, radius = 15.dp.toPx(), center = Offset(leftFootX - kneeOutX / 2f, kneeY - 8.dp.toPx()))
                drawCircle(color = accentColor, radius = 6.dp.toPx(), center = Offset(leftFootX - kneeOutX / 2f, kneeY - 8.dp.toPx()))
            }

            ExerciseType.BICEP_CURL -> {
                val headY = centerY - 45.dp.toPx()
                val shoulderX = centerX - 10.dp.toPx()
                val shoulderY = headY + 18.dp.toPx()
                val elbowX = shoulderX
                val elbowY = shoulderY + 30.dp.toPx()

                // Head & Torso
                drawCircle(color = strokeColor, radius = 10.dp.toPx(), center = Offset(centerX, headY))
                drawLine(
                    color = strokeColor,
                    start = Offset(centerX, headY + 10.dp.toPx()),
                    end = Offset(centerX, centerY + 25.dp.toPx()),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Upper arm (fixed)
                drawLine(
                    color = strokeColor,
                    start = Offset(shoulderX, shoulderY),
                    end = Offset(elbowX, elbowY),
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Forearm pivoting up
                val curlAngle = Math.toRadians((180.0 - progress * 140.0)).toFloat()
                val forearmLength = 28.dp.toPx()
                val handX = elbowX + forearmLength * sin(curlAngle)
                val handY = elbowY + forearmLength * cos(curlAngle)

                drawLine(
                    color = strokeColor,
                    start = Offset(elbowX, elbowY),
                    end = Offset(handX, handY),
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Dumbbell at hand
                drawCircle(color = accentColor, radius = 8.dp.toPx(), center = Offset(handX, handY))

                // Bicep Muscle Flex Glow
                val bicepX = (shoulderX + elbowX) / 2f + 4.dp.toPx()
                val bicepY = (shoulderY + elbowY) / 2f
                val flexRadius = (8.dp.toPx() + progress * 8.dp.toPx())
                drawCircle(color = glowColor, radius = flexRadius, center = Offset(bicepX, bicepY))
                drawCircle(color = accentColor, radius = 5.dp.toPx(), center = Offset(bicepX, bicepY))
            }

            ExerciseType.SHOULDER_PRESS -> {
                val headY = centerY - 25.dp.toPx()
                val shoulderY = headY + 20.dp.toPx()
                val leftShoulderX = centerX - 18.dp.toPx()
                val rightShoulderX = centerX + 18.dp.toPx()

                // Head & Torso
                drawCircle(color = strokeColor, radius = 10.dp.toPx(), center = Offset(centerX, headY))
                drawLine(
                    color = strokeColor,
                    start = Offset(centerX, headY + 10.dp.toPx()),
                    end = Offset(centerX, centerY + 35.dp.toPx()),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Press Height Motion
                val pressHeight = progress * 40.dp.toPx()

                // Left Arm
                val leftElbowY = shoulderY + 15.dp.toPx() - pressHeight * 0.5f
                val leftHandY = shoulderY - pressHeight
                drawLine(color = strokeColor, start = Offset(leftShoulderX, shoulderY), end = Offset(leftShoulderX - 12.dp.toPx(), leftElbowY), strokeWidth = 4.dp.toPx())
                drawLine(color = strokeColor, start = Offset(leftShoulderX - 12.dp.toPx(), leftElbowY), end = Offset(leftShoulderX - 10.dp.toPx(), leftHandY), strokeWidth = 4.dp.toPx())
                drawCircle(color = accentColor, radius = 7.dp.toPx(), center = Offset(leftShoulderX - 10.dp.toPx(), leftHandY))

                // Right Arm
                val rightElbowY = shoulderY + 15.dp.toPx() - pressHeight * 0.5f
                val rightHandY = shoulderY - pressHeight
                drawLine(color = strokeColor, start = Offset(rightShoulderX, shoulderY), end = Offset(rightShoulderX + 12.dp.toPx(), rightElbowY), strokeWidth = 4.dp.toPx())
                drawLine(color = strokeColor, start = Offset(rightShoulderX + 12.dp.toPx(), rightElbowY), end = Offset(rightShoulderX + 10.dp.toPx(), rightHandY), strokeWidth = 4.dp.toPx())
                drawCircle(color = accentColor, radius = 7.dp.toPx(), center = Offset(rightShoulderX + 10.dp.toPx(), rightHandY))

                // Deltoid Shoulders Target Highlight
                drawCircle(color = glowColor, radius = 12.dp.toPx(), center = Offset(leftShoulderX, shoulderY))
                drawCircle(color = glowColor, radius = 12.dp.toPx(), center = Offset(rightShoulderX, shoulderY))
            }

            ExerciseType.LUNGE -> {
                val groundY = centerY + 40.dp.toPx()
                drawLine(
                    color = KinetiqGray.copy(alpha = 0.3f),
                    start = Offset(centerX - 70.dp.toPx(), groundY),
                    end = Offset(centerX + 70.dp.toPx(), groundY),
                    strokeWidth = 2.dp.toPx()
                )

                val lungeDepth = progress * 24.dp.toPx()
                val hipY = centerY - 15.dp.toPx() + lungeDepth
                val headY = hipY - 45.dp.toPx()

                // Head & Torso
                drawCircle(color = strokeColor, radius = 10.dp.toPx(), center = Offset(centerX, headY))
                drawLine(color = strokeColor, start = Offset(centerX, headY + 10.dp.toPx()), end = Offset(centerX, hipY), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

                // Front Leg (Step forward right)
                val frontKneeX = centerX + 25.dp.toPx()
                val frontKneeY = hipY + 15.dp.toPx()
                val frontFootX = centerX + 25.dp.toPx()
                val frontLegPath = Path().apply {
                    moveTo(centerX, hipY)
                    lineTo(frontKneeX, frontKneeY)
                    lineTo(frontFootX, groundY)
                }
                drawPath(frontLegPath, color = strokeColor, style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round))

                // Back Leg (Lowering knee down)
                val backKneeX = centerX - 20.dp.toPx()
                val backKneeY = groundY - 5.dp.toPx() + (1f - progress) * 15.dp.toPx()
                val backFootX = centerX - 40.dp.toPx()
                val backLegPath = Path().apply {
                    moveTo(centerX, hipY)
                    lineTo(backKneeX, backKneeY)
                    lineTo(backFootX, groundY)
                }
                drawPath(backLegPath, color = strokeColor, style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round))

                // Leg Target Highlight
                drawCircle(color = glowColor, radius = 14.dp.toPx(), center = Offset(frontKneeX - 5.dp.toPx(), frontKneeY - 5.dp.toPx()))
                drawCircle(color = accentColor, radius = 6.dp.toPx(), center = Offset(frontKneeX - 5.dp.toPx(), frontKneeY - 5.dp.toPx()))
            }

            ExerciseType.BENCH_PRESS -> {
                val benchY = centerY + 10.dp.toPx()
                // Bench Line
                drawLine(color = KinetiqGray.copy(alpha = 0.4f), start = Offset(centerX - 50.dp.toPx(), benchY), end = Offset(centerX + 50.dp.toPx(), benchY), strokeWidth = 6.dp.toPx())

                // Head & Torso lying down
                val headX = centerX - 35.dp.toPx()
                drawCircle(color = strokeColor, radius = 9.dp.toPx(), center = Offset(headX, benchY - 8.dp.toPx()))
                drawLine(color = strokeColor, start = Offset(headX + 9.dp.toPx(), benchY - 8.dp.toPx()), end = Offset(centerX + 35.dp.toPx(), benchY - 8.dp.toPx()), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

                // Barbell Press Height
                val pressHeight = progress * 35.dp.toPx()
                val barY = benchY - 22.dp.toPx() - pressHeight
                val barX = centerX - 5.dp.toPx()

                // Barbell
                drawLine(color = accentColor, start = Offset(barX, barY - 30.dp.toPx()), end = Offset(barX, barY + 30.dp.toPx()), strokeWidth = 4.dp.toPx())
                drawCircle(color = accentColor, radius = 8.dp.toPx(), center = Offset(barX, barY - 30.dp.toPx()))
                drawCircle(color = accentColor, radius = 8.dp.toPx(), center = Offset(barX, barY + 30.dp.toPx()))

                // Pectoral Target Glow
                drawCircle(color = glowColor, radius = 12.dp.toPx(), center = Offset(barX, benchY - 12.dp.toPx()))
                drawCircle(color = accentColor, radius = 5.dp.toPx(), center = Offset(barX, benchY - 12.dp.toPx()))
            }

            ExerciseType.PULL_UP_ROW -> {
                val barY = centerY - 50.dp.toPx()
                // Pull Up Bar
                drawLine(color = KinetiqGray.copy(alpha = 0.5f), start = Offset(centerX - 60.dp.toPx(), barY), end = Offset(centerX + 60.dp.toPx(), barY), strokeWidth = 4.dp.toPx())

                val pullUpOffset = progress * 32.dp.toPx()
                val headY = centerY + 10.dp.toPx() - pullUpOffset

                // Head & Body
                drawCircle(color = strokeColor, radius = 10.dp.toPx(), center = Offset(centerX, headY))
                drawLine(color = strokeColor, start = Offset(centerX, headY + 10.dp.toPx()), end = Offset(centerX, headY + 50.dp.toPx()), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

                // Arms reaching up to bar
                drawLine(color = strokeColor, start = Offset(centerX - 5.dp.toPx(), headY + 15.dp.toPx()), end = Offset(centerX - 30.dp.toPx(), barY), strokeWidth = 4.dp.toPx())
                drawLine(color = strokeColor, start = Offset(centerX + 5.dp.toPx(), headY + 15.dp.toPx()), end = Offset(centerX + 30.dp.toPx(), barY), strokeWidth = 4.dp.toPx())

                // Upper Back / Lats Target Glow
                drawCircle(color = glowColor, radius = 15.dp.toPx(), center = Offset(centerX, headY + 22.dp.toPx()))
                drawCircle(color = accentColor, radius = 6.dp.toPx(), center = Offset(centerX, headY + 22.dp.toPx()))
            }

            ExerciseType.PLANK_CRUNCH -> {
                val groundY = centerY + 20.dp.toPx()
                drawLine(color = KinetiqGray.copy(alpha = 0.3f), start = Offset(centerX - 70.dp.toPx(), groundY), end = Offset(centerX + 70.dp.toPx(), groundY), strokeWidth = 2.dp.toPx())

                // Crunch Flex Motion
                val crunchAngle = progress * 20.dp.toPx()

                val headX = centerX - 30.dp.toPx() + crunchAngle
                val headY = groundY - 25.dp.toPx() - crunchAngle * 0.5f

                drawCircle(color = strokeColor, radius = 9.dp.toPx(), center = Offset(headX, headY))

                val spinePath = Path().apply {
                    moveTo(headX, headY + 9.dp.toPx())
                    quadraticTo(centerX - 10.dp.toPx(), groundY - 20.dp.toPx() - crunchAngle, centerX + 30.dp.toPx(), groundY - 5.dp.toPx())
                }
                drawPath(spinePath, color = strokeColor, style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round))

                // Core Abs Glow
                drawCircle(color = glowColor, radius = 14.dp.toPx(), center = Offset(centerX - 5.dp.toPx(), groundY - 18.dp.toPx()))
                drawCircle(color = accentColor, radius = 6.dp.toPx(), center = Offset(centerX - 5.dp.toPx(), groundY - 18.dp.toPx()))
            }

            ExerciseType.JUMPING_JACKS -> {
                val headY = centerY - 40.dp.toPx()
                drawCircle(color = strokeColor, radius = 10.dp.toPx(), center = Offset(centerX, headY))

                val jackSpread = progress * 30.dp.toPx()

                // Torso
                drawLine(color = strokeColor, start = Offset(centerX, headY + 10.dp.toPx()), end = Offset(centerX, centerY + 10.dp.toPx()), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

                // Arms moving up and out
                drawLine(color = strokeColor, start = Offset(centerX, headY + 15.dp.toPx()), end = Offset(centerX - jackSpread - 10.dp.toPx(), headY - progress * 25.dp.toPx()), strokeWidth = 4.dp.toPx())
                drawLine(color = strokeColor, start = Offset(centerX, headY + 15.dp.toPx()), end = Offset(centerX + jackSpread + 10.dp.toPx(), headY - progress * 25.dp.toPx()), strokeWidth = 4.dp.toPx())

                // Legs spreading out
                val footY = centerY + 45.dp.toPx()
                drawLine(color = strokeColor, start = Offset(centerX, centerY + 10.dp.toPx()), end = Offset(centerX - jackSpread, footY), strokeWidth = 5.dp.toPx())
                drawLine(color = strokeColor, start = Offset(centerX, centerY + 10.dp.toPx()), end = Offset(centerX + jackSpread, footY), strokeWidth = 5.dp.toPx())

                // Heart Rate / Full Body Pulse
                drawCircle(color = glowColor, radius = 16.dp.toPx(), center = Offset(centerX, headY + 20.dp.toPx()))
            }

            ExerciseType.GENERIC -> {
                // Rhythmic dumbbell flex & core pulse
                val headY = centerY - 35.dp.toPx()
                drawCircle(color = strokeColor, radius = 10.dp.toPx(), center = Offset(centerX, headY))
                drawLine(color = strokeColor, start = Offset(centerX, headY + 10.dp.toPx()), end = Offset(centerX, centerY + 20.dp.toPx()), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

                val motionX = (sin(progress * Math.PI * 2) * 15.dp.toPx()).toFloat()
                drawLine(color = strokeColor, start = Offset(centerX, headY + 15.dp.toPx()), end = Offset(centerX + 25.dp.toPx() + motionX, centerY), strokeWidth = 4.dp.toPx())
                drawCircle(color = accentColor, radius = 8.dp.toPx(), center = Offset(centerX + 25.dp.toPx() + motionX, centerY))

                drawCircle(color = glowColor, radius = 14.dp.toPx(), center = Offset(centerX, headY + 25.dp.toPx()))
                drawCircle(color = accentColor, radius = 5.dp.toPx(), center = Offset(centerX, headY + 25.dp.toPx()))
            }
        }
    }
}
