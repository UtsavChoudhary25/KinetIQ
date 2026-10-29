package com.utsav.kinetiq.ui.vision

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime
import com.utsav.kinetiq.ui.vision.components.CameraPreview
import com.utsav.kinetiq.utils.VoiceCoachHelper

@Composable
fun VisionScreen() {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!hasCameraPermission) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(KinetiqBlack)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = KinetiqLime,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "CAMERA PERMISSION NEEDED",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Kinetiq Vision AI uses your camera to analyze your posture and count reps in real-time.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = KinetiqGray),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { launcher.launch(Manifest.permission.CAMERA) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KinetiqLime,
                            contentColor = KinetiqBlack
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ALLOW CAMERA ACCESS", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
        return
    }

    var isFrontCamera by remember { mutableStateOf(true) }
    var selectedExercise by remember { mutableStateOf(ExerciseType.SQUAT) }
    var detectedPose by remember { mutableStateOf<Pose?>(null) }
    var feedback by remember { mutableStateOf<PoseDetectorProcessor.FormFeedback?>(null) }
    var imageWidth by remember { mutableIntStateOf(0) }
    var imageHeight by remember { mutableIntStateOf(0) }

    val processor = remember { PoseDetectorProcessor() }
    val voiceCoach = remember { VoiceCoachHelper(context) }

    DisposableEffect(Unit) {
        onDispose {
            voiceCoach.shutdown()
        }
    }

    // Voice announcements on rep complete or form cue
    LaunchedEffect(feedback) {
        feedback?.let { fb ->
            if (fb.hasRepJustCompleted) {
                voiceCoach.speak("Rep ${fb.reps}! Great form!")
            } else if (!fb.isCorrect && fb.feedbackCue.isNotBlank()) {
                voiceCoach.speak(fb.feedbackCue)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(KinetiqBlack)) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            isFrontCamera = isFrontCamera,
            analyzer = { imageProxy ->
                processor.processImage(
                    imageProxy = imageProxy,
                    onSuccess = { pose, width: Int, height: Int ->
                        detectedPose = pose
                        imageWidth = width
                        imageHeight = height
                        feedback = processor.analyzePose(pose, selectedExercise)
                    },
                    onError = { it.printStackTrace() }
                )
            }
        )

        // Skeleton Bone Overlay
        PoseSkeletonOverlay(
            pose = detectedPose,
            sourceWidth = imageWidth,
            sourceHeight = imageHeight,
            isFrontCamera = isFrontCamera,
            isFormCorrect = feedback?.isCorrect ?: true
        )

        // Top Controls Header (Exercise Selector, AI Badge & Camera Toggle)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = KinetiqBlack.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = KinetiqLime, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "VISION AI ACTIVE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = KinetiqLime, letterSpacing = 1.sp)
                        )
                    }
                }

                // Camera Switch Button (Front <-> Rear)
                IconButton(
                    onClick = {
                        isFrontCamera = !isFrontCamera
                        voiceCoach.speak(if (isFrontCamera) "Front camera selected" else "Rear camera selected")
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(KinetiqBlack.copy(alpha = 0.8f), CircleShape)
                        .border(1.dp, KinetiqLime.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Switch Camera",
                        tint = KinetiqLime,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Exercise Selection Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ExerciseType.entries.toTypedArray()) { exercise ->
                    val isSelected = selectedExercise == exercise
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedExercise = exercise
                            processor.setExerciseType(exercise)
                            voiceCoach.speak("Switched to ${exercise.displayName}")
                        },
                        label = {
                            Text(
                                text = "${exercise.iconEmoji} ${exercise.displayName}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = KinetiqBlack.copy(alpha = 0.8f),
                            labelColor = KinetiqGray,
                            selectedContainerColor = KinetiqLime,
                            selectedLabelColor = KinetiqBlack
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color.Transparent,
                            selectedBorderColor = KinetiqLime
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }

        // Form Feedback & Angle Display (Bottom Center)
        feedback?.let { fb ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 120.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        color = if (fb.isCorrect) KinetiqLime else Color(0xFFFF3D00),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = fb.message,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = KinetiqBlack)
                        )
                    }

                    if (fb.feedbackCue.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = KinetiqBlack.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqGray.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = fb.feedbackCue,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 11.sp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Joint Angle: ${fb.angle.toInt()}°",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Rep Counter UI (Bottom Left)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Column {
                    Text("${selectedExercise.displayName.uppercase()} REPS", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp))
                    Text(
                        text = "${feedback?.reps ?: 0}",
                        color = KinetiqLime,
                        style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = {
                        processor.resetCount()
                        voiceCoach.speak("Reps reset")
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(KinetiqBlack.copy(alpha = 0.8f), CircleShape)
                        .border(1.dp, KinetiqGray.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset Reps", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun PoseSkeletonOverlay(
    pose: Pose?,
    sourceWidth: Int,
    sourceHeight: Int,
    isFrontCamera: Boolean,
    isFormCorrect: Boolean
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (pose == null || sourceWidth <= 0 || sourceHeight <= 0) return@Canvas

        val scaleX = size.width / sourceHeight.toFloat()
        val scaleY = size.height / sourceWidth.toFloat()

        val lineColor = if (isFormCorrect) KinetiqLime else Color(0xFFFF3D00)

        fun transformX(y: Float): Float {
            return if (isFrontCamera) {
                (sourceHeight - y) * scaleX
            } else {
                y * scaleX
            }
        }

        fun transformY(x: Float): Float {
            return x * scaleY
        }

        // Draw Skeleton Connections
        PoseDetectorProcessor.SKELETON_CONNECTIONS.forEach { connection ->
            val startLm = pose.getPoseLandmark(connection.startLandmark)
            val endLm = pose.getPoseLandmark(connection.endLandmark)

            if (startLm != null && endLm != null && startLm.inFrameLikelihood >= 0.5f && endLm.inFrameLikelihood >= 0.5f) {
                val startX = transformX(startLm.position.y)
                val startY = transformY(startLm.position.x)
                val endX = transformX(endLm.position.y)
                val endY = transformY(endLm.position.x)

                drawLine(
                    color = lineColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
            }
        }

        // Draw Joint Circles
        pose.allPoseLandmarks.forEach { landmark ->
            if (landmark.inFrameLikelihood >= 0.5f) {
                val x = transformX(landmark.position.y)
                val y = transformY(landmark.position.x)

                drawCircle(
                    color = Color.White,
                    radius = 8f,
                    center = Offset(x, y)
                )
                drawCircle(
                    color = lineColor,
                    radius = 5f,
                    center = Offset(x, y)
                )
            }
        }
    }
}
