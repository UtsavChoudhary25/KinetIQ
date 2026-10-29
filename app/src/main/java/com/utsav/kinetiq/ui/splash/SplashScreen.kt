package com.utsav.kinetiq.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.utsav.kinetiq.data.local.OnboardingPrefs
import com.utsav.kinetiq.navigation.AuthRoute
import com.utsav.kinetiq.navigation.MainRoute
import com.utsav.kinetiq.navigation.OnboardingRoute
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onNavigationNext: (Any) -> Unit
) {
    val context = LocalContext.current
    val onboardingPrefs = remember { OnboardingPrefs(context) }
    val isOnboardingCompleted by onboardingPrefs.isOnboardingCompleted.collectAsState(initial = null)

    // Animation States
    val logoScale = remember { Animatable(0.7f) }
    val logoAlpha = remember { Animatable(0f) }
    val pathProgress = remember { Animatable(0f) }
    val progressWidth = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "aura_pulse")
    val auraGlow by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_glow"
    )

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    // Sequence Effect
    LaunchedEffect(isOnboardingCompleted) {
        launch {
            logoAlpha.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
        }
        launch {
            logoScale.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            pathProgress.animateTo(1f, tween(1000, easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)))
        }
        launch {
            delay(400)
            progressWidth.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))
        }

        delay(2000)

        if (isOnboardingCompleted != null) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            when {
                isOnboardingCompleted == false -> onNavigationNext(OnboardingRoute)
                currentUser == null -> onNavigationNext(AuthRoute)
                else -> onNavigationNext(MainRoute)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KinetiqBlack),
        contentAlignment = Alignment.Center
    ) {
        // Luxury Ambient Glow Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width.coerceAtMost(size.height) * 0.45f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        KinetiqLime.copy(alpha = 0.18f * auraGlow),
                        Color(0xFF00FF9D).copy(alpha = 0.06f * auraGlow),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 1.5f
                ),
                radius = radius * 1.5f,
                center = center
            )
        }

        // Center Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(logoScale.value)
                .alpha(logoAlpha.value)
        ) {
            // Sleek Luxury Brand Emblem
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val p = pathProgress.value

                    // Geometric Modern Kinetic Diamond/K Symbol
                    val logoPath = Path().apply {
                        moveTo(w * 0.2f, h * 0.15f)
                        lineTo(w * 0.2f, h * 0.85f)
                        moveTo(w * 0.2f, h * 0.5f)
                        lineTo(w * 0.8f, h * 0.15f)
                        moveTo(w * 0.45f, h * 0.35f)
                        lineTo(w * 0.8f, h * 0.85f)
                    }

                    // Outer Hex Ring Accent
                    val ringPath = Path().apply {
                        moveTo(w * 0.5f, h * 0.02f)
                        lineTo(w * 0.95f, h * 0.28f)
                        lineTo(w * 0.95f, h * 0.72f)
                        lineTo(w * 0.5f, h * 0.98f)
                        lineTo(w * 0.05f, h * 0.72f)
                        lineTo(w * 0.05f, h * 0.28f)
                        close()
                    }

                    drawPath(
                        path = ringPath,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                KinetiqLime.copy(alpha = 0.9f * p),
                                Color.White.copy(alpha = 0.3f * p),
                                KinetiqLime.copy(alpha = 0.2f * p)
                            )
                        ),
                        style = Stroke(width = 2.5.dp.toPx())
                    )

                    drawPath(
                        path = logoPath,
                        color = KinetiqLime,
                        style = Stroke(
                            width = 6.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Main Brand Title
            Text(
                text = "KINETIQ",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 12.sp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White,
                            KinetiqLime,
                            Color.White
                        )
                    )
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Luxury Tagline
            Text(
                text = "ELEVATE YOUR PERFORMANCE",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = KinetiqGray,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 3.5.sp,
                    fontSize = 10.sp
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Premium Shimmering Progress Bar
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressWidth.value)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    KinetiqLime.copy(alpha = 0.4f),
                                    KinetiqLime,
                                    Color.White
                                ),
                                startX = shimmerOffset * 300f,
                                endX = (shimmerOffset + 1f) * 300f
                            )
                        )
                )
            }
        }
    }
}

