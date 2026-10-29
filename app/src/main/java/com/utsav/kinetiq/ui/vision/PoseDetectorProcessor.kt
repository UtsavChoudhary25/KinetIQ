package com.utsav.kinetiq.ui.vision

import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import kotlin.math.abs
import kotlin.math.atan2

enum class ExerciseType(val displayName: String, val iconEmoji: String) {
    SQUAT("Squat", "🏋️"),
    PUSHUP("Push-up", "💪"),
    BICEP_CURL("Bicep Curl", "🦾"),
    SHOULDER_PRESS("Shoulder Press", "🏋️‍♂️"),
    LUNGE("Lunge", "🦵")
}

class PoseDetectorProcessor {
    private val options = AccuratePoseDetectorOptions.Builder()
        .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
        .build()

    private val detector = PoseDetection.getClient(options)

    // State Tracking
    private var currentExercise = ExerciseType.SQUAT
    private var repCount = 0
    private var isStateDownOrFlexed = false
    private var smoothedAngle = 0.0

    private val MIN_CONFIDENCE = 0.5f

    data class FormFeedback(
        val message: String,
        val isCorrect: Boolean,
        val angle: Double = 0.0,
        val reps: Int = 0,
        val exerciseType: ExerciseType = ExerciseType.SQUAT,
        val feedbackCue: String = "",
        val hasRepJustCompleted: Boolean = false
    )

    data class SkeletonJoint(
        val startLandmark: Int,
        val endLandmark: Int
    )

    fun setExerciseType(type: ExerciseType) {
        if (currentExercise != type) {
            currentExercise = type
            repCount = 0
            isStateDownOrFlexed = false
            smoothedAngle = 0.0
        }
    }

    fun resetCount() {
        repCount = 0
        isStateDownOrFlexed = false
        smoothedAngle = 0.0
    }

    fun analyzePose(pose: Pose, exerciseType: ExerciseType): FormFeedback {
        currentExercise = exerciseType
        return when (exerciseType) {
            ExerciseType.SQUAT -> checkSquatForm(pose)
            ExerciseType.PUSHUP -> checkPushupForm(pose)
            ExerciseType.BICEP_CURL -> checkBicepCurlForm(pose)
            ExerciseType.SHOULDER_PRESS -> checkShoulderPressForm(pose)
            ExerciseType.LUNGE -> checkLungeForm(pose)
        }
    }

    // --- SQUAT ---
    private fun checkSquatForm(pose: Pose): FormFeedback {
        val (hip, knee, ankle) = getBestSideLandmarks(
            pose,
            PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE,
            PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE
        ) ?: return FormFeedback("STEP IN FRAME", false, reps = repCount, exerciseType = ExerciseType.SQUAT)

        val angle = smoothAngle(calculateAngle(hip, knee, ankle))
        var repJustCompleted = false

        val (message, isCorrect, cue) = when {
            angle < 95 -> {
                if (!isStateDownOrFlexed) {
                    isStateDownOrFlexed = true
                }
                Triple("PERFECT DEPTH!", true, "Great depth! Drive up through heels.")
            }
            angle > 155 -> {
                if (isStateDownOrFlexed) {
                    repCount++
                    isStateDownOrFlexed = false
                    repJustCompleted = true
                }
                Triple("STANDING POSITION", true, "Good extension.")
            }
            else -> {
                if (isStateDownOrFlexed) {
                    Triple("DRIVING UP", true, "Push up to standing!")
                } else {
                    Triple("SQUAT LOWER", false, "Lower hips below knee level.")
                }
            }
        }

        return FormFeedback(message, isCorrect, angle, repCount, ExerciseType.SQUAT, cue, repJustCompleted)
    }

    // --- PUSH-UP ---
    private fun checkPushupForm(pose: Pose): FormFeedback {
        val (shoulder, elbow, wrist) = getBestSideLandmarks(
            pose,
            PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST,
            PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST
        ) ?: return FormFeedback("STEP IN FRAME", false, reps = repCount, exerciseType = ExerciseType.PUSHUP)

        val (hip, knee, ankle) = getBestSideLandmarks(
            pose,
            PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE,
            PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE
        ) ?: Triple(null, null, null)

        val elbowAngle = smoothAngle(calculateAngle(shoulder, elbow, wrist))
        var repJustCompleted = false

        // Check body line alignment (Shoulder - Hip - Ankle)
        var bodyLineStraight = true
        if (hip != null && ankle != null) {
            val bodyAngle = calculateAngle(shoulder, hip, ankle)
            if (bodyAngle < 150) {
                bodyLineStraight = false
            }
        }

        val (message, isCorrect, cue) = when {
            !bodyLineStraight -> {
                Triple("KEEP BODY STRAIGHT", false, "Engage core, don't let hips sag or pike!")
            }
            elbowAngle < 90 -> {
                if (!isStateDownOrFlexed) {
                    isStateDownOrFlexed = true
                }
                Triple("GOOD PUSHUP DEPTH", true, "Now push back up!")
            }
            elbowAngle > 150 -> {
                if (isStateDownOrFlexed) {
                    repCount++
                    isStateDownOrFlexed = false
                    repJustCompleted = true
                }
                Triple("FULL EXTENSION", true, "Lower chest to ground.")
            }
            else -> {
                Triple("GO LOWER", false, "Lower chest to 90 degrees.")
            }
        }

        return FormFeedback(message, isCorrect, elbowAngle, repCount, ExerciseType.PUSHUP, cue, repJustCompleted)
    }

    // --- BICEP CURL ---
    private fun checkBicepCurlForm(pose: Pose): FormFeedback {
        val (shoulder, elbow, wrist) = getBestSideLandmarks(
            pose,
            PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST,
            PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST
        ) ?: return FormFeedback("STEP IN FRAME", false, reps = repCount, exerciseType = ExerciseType.BICEP_CURL)

        val armAngle = smoothAngle(calculateAngle(shoulder, elbow, wrist))
        var repJustCompleted = false

        val (message, isCorrect, cue) = when {
            armAngle < 50 -> {
                if (!isStateDownOrFlexed) {
                    isStateDownOrFlexed = true
                }
                Triple("FULL BICEP CURL!", true, "Squeeze bicep at the top!")
            }
            armAngle > 145 -> {
                if (isStateDownOrFlexed) {
                    repCount++
                    isStateDownOrFlexed = false
                    repJustCompleted = true
                }
                Triple("BOTTOM EXTENSION", true, "Curl weights upwards.")
            }
            else -> {
                if (isStateDownOrFlexed) {
                    Triple("CONTROL DESCENT", true, "Lower under control.")
                } else {
                    Triple("CURL HIGHER", false, "Flex arm completely.")
                }
            }
        }

        return FormFeedback(message, isCorrect, armAngle, repCount, ExerciseType.BICEP_CURL, cue, repJustCompleted)
    }

    // --- SHOULDER PRESS ---
    private fun checkShoulderPressForm(pose: Pose): FormFeedback {
        val (shoulder, elbow, wrist) = getBestSideLandmarks(
            pose,
            PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST,
            PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST
        ) ?: return FormFeedback("STEP IN FRAME", false, reps = repCount, exerciseType = ExerciseType.SHOULDER_PRESS)

        val armAngle = smoothAngle(calculateAngle(shoulder, elbow, wrist))
        var repJustCompleted = false

        val (message, isCorrect, cue) = when {
            armAngle > 155 && wrist.position.y < shoulder.position.y -> {
                if (isStateDownOrFlexed) {
                    repCount++
                    isStateDownOrFlexed = false
                    repJustCompleted = true
                }
                Triple("FULL OVERHEAD PRESS!", true, "Lower weights to chin level.")
            }
            armAngle < 95 -> {
                if (!isStateDownOrFlexed) {
                    isStateDownOrFlexed = true
                }
                Triple("BOTTOM RACK POSITION", true, "Press overhead powerfully!")
            }
            else -> {
                Triple("PRESS HIGHER", false, "Lock out overhead.")
            }
        }

        return FormFeedback(message, isCorrect, armAngle, repCount, ExerciseType.SHOULDER_PRESS, cue, repJustCompleted)
    }

    // --- LUNGE ---
    private fun checkLungeForm(pose: Pose): FormFeedback {
        val (hip, knee, ankle) = getBestSideLandmarks(
            pose,
            PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE,
            PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE
        ) ?: return FormFeedback("STEP IN FRAME", false, reps = repCount, exerciseType = ExerciseType.LUNGE)

        val kneeAngle = smoothAngle(calculateAngle(hip, knee, ankle))
        var repJustCompleted = false

        val (message, isCorrect, cue) = when {
            kneeAngle < 95 -> {
                if (!isStateDownOrFlexed) {
                    isStateDownOrFlexed = true
                }
                Triple("GREAT LUNGE DEPTH!", true, "Drive back up to standing.")
            }
            kneeAngle > 155 -> {
                if (isStateDownOrFlexed) {
                    repCount++
                    isStateDownOrFlexed = false
                    repJustCompleted = true
                }
                Triple("STANDING STANCE", true, "Step out and lunge.")
            }
            else -> {
                Triple("LUNGE LOWER", false, "Lower back knee towards ground.")
            }
        }

        return FormFeedback(message, isCorrect, kneeAngle, repCount, ExerciseType.LUNGE, cue, repJustCompleted)
    }

    // --- HELPER METHODS ---

    private fun getBestSideLandmarks(
        pose: Pose,
        left1: Int, left2: Int, left3: Int,
        right1: Int, right2: Int, right3: Int
    ): Triple<PoseLandmark, PoseLandmark, PoseLandmark>? {
        val l1 = pose.getPoseLandmark(left1)
        val l2 = pose.getPoseLandmark(left2)
        val l3 = pose.getPoseLandmark(left3)

        val r1 = pose.getPoseLandmark(right1)
        val r2 = pose.getPoseLandmark(right2)
        val r3 = pose.getPoseLandmark(right3)

        val leftScore = if (l1 != null && l2 != null && l3 != null) {
            l1.inFrameLikelihood + l2.inFrameLikelihood + l3.inFrameLikelihood
        } else 0f

        val rightScore = if (r1 != null && r2 != null && r3 != null) {
            r1.inFrameLikelihood + r2.inFrameLikelihood + r3.inFrameLikelihood
        } else 0f

        return when {
            leftScore >= rightScore && leftScore >= (3 * MIN_CONFIDENCE) -> Triple(l1!!, l2!!, l3!!)
            rightScore > leftScore && rightScore >= (3 * MIN_CONFIDENCE) -> Triple(r1!!, r2!!, r3!!)
            else -> null
        }
    }

    private fun calculateAngle(firstPoint: PoseLandmark, midPoint: PoseLandmark, lastPoint: PoseLandmark): Double {
        var result = Math.toDegrees(
            (atan2(lastPoint.position.y - midPoint.position.y, lastPoint.position.x - midPoint.position.x)
                    - atan2(firstPoint.position.y - midPoint.position.y, firstPoint.position.x - midPoint.position.x)).toDouble()
        )
        result = abs(result)
        if (result > 180) {
            result = 360.0 - result
        }
        return result
    }

    private fun smoothAngle(newAngle: Double): Double {
        if (smoothedAngle == 0.0) {
            smoothedAngle = newAngle
        } else {
            // Exponential moving average filter (alpha = 0.35)
            smoothedAngle = (smoothedAngle * 0.65) + (newAngle * 0.35)
        }
        return smoothedAngle
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    fun processImage(
        imageProxy: ImageProxy,
        onSuccess: (Pose, Int, Int) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val width = imageProxy.width
        val height = imageProxy.height
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )
            detector.process(image)
                .addOnSuccessListener { onSuccess(it, width, height) }
                .addOnFailureListener { onError(it) }
                .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }
    }

    companion object {
        val SKELETON_CONNECTIONS = listOf(
            SkeletonJoint(PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER),
            SkeletonJoint(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW),
            SkeletonJoint(PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST),
            SkeletonJoint(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW),
            SkeletonJoint(PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST),
            SkeletonJoint(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP),
            SkeletonJoint(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP),
            SkeletonJoint(PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP),
            SkeletonJoint(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE),
            SkeletonJoint(PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE),
            SkeletonJoint(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE),
            SkeletonJoint(PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE)
        )
    }
}
