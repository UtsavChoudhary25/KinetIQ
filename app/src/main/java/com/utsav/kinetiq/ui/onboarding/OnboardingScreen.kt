package com.utsav.kinetiq.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utsav.kinetiq.data.local.OnboardingPrefs
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val pageType: Int
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Train Smarter",
        subtitle = "AI WORKOUT ENGINE",
        description = "Personalized adaptive workout programs generated instantly for your goals.",
        pageType = 0
    ),
    OnboardingPage(
        title = "Move Better",
        subtitle = "VISION AI FORM SCANNER",
        description = "Real-time posture and motion tracking powered by ML Kit pose detection.",
        pageType = 1
    ),
    OnboardingPage(
        title = "Track Progress",
        subtitle = "PERFORMANCE ANALYTICS",
        description = "Deep biometric insights, calorie tracking, and Google Fit health syncing.",
        pageType = 2
    )
)

@Composable
fun OnboardingScreen(
    onOnboardingFinished: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val onboardingPrefs = remember { OnboardingPrefs(context) }

    Scaffold(
        containerColor = KinetiqBlack,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                if (pagerState.currentPage < onboardingPages.size - 1) {
                    TextButton(onClick = {
                        scope.launch {
                            onboardingPrefs.saveOnboardingCompleted(true)
                            onOnboardingFinished()
                        }
                    }) {
                        Text(
                            "SKIP",
                            color = KinetiqGray,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                OnboardingPageContent(onboardingPages[pageIndex])
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Expanding Animated Page Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(onboardingPages.size) { iteration ->
                        val isSelected = pagerState.currentPage == iteration
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 28.dp else 8.dp,
                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                            label = "indicatorWidth"
                        )
                        val color = if (isSelected) KinetiqLime else KinetiqGray.copy(alpha = 0.3f)

                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                // Next Button
                Button(
                    onClick = {
                        if (pagerState.currentPage < onboardingPages.size - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            scope.launch {
                                onboardingPrefs.saveOnboardingCompleted(true)
                                onOnboardingFinished()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KinetiqLime,
                        contentColor = KinetiqBlack
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp),
                    modifier = Modifier.shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = KinetiqLime.copy(alpha = 0.4f)
                    )
                ) {
                    Text(
                        text = if (pagerState.currentPage == onboardingPages.size - 1) "GET STARTED" else "NEXT",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    val infiniteTransition = rememberInfiniteTransition(label = "visual_anim")
    
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Luxury Animated Kinetic Canvas Graphic
        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width * 0.4f

                // Ambient Radial Backlight
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            KinetiqLime.copy(alpha = 0.25f * pulseGlow),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius * 1.4f
                    ),
                    radius = radius * 1.4f,
                    center = center
                )

                when (page.pageType) {
                    0 -> {
                        // AI Neural Ring Visual
                        drawCircle(
                            color = KinetiqLime.copy(alpha = 0.15f),
                            radius = radius,
                            center = center,
                            style = Stroke(width = 3.dp.toPx())
                        )
                        drawArc(
                            color = KinetiqLime,
                            startAngle = rotation,
                            sweepAngle = 220f,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
                            style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawCircle(
                            color = KinetiqLime,
                            radius = 16.dp.toPx() * pulseGlow,
                            center = center
                        )
                    }
                    1 -> {
                        // Vision AI Pose Scanner Box
                        drawCircle(
                            color = Color.White.copy(alpha = 0.1f),
                            radius = radius,
                            center = center
                        )
                        drawArc(
                            color = KinetiqLime,
                            startAngle = -rotation,
                            sweepAngle = 180f,
                            useCenter = false,
                            topLeft = Offset(center.x - radius * 0.8f, center.y - radius * 0.8f),
                            size = androidx.compose.ui.geometry.Size(radius * 1.6f, radius * 1.6f),
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawCircle(
                            color = KinetiqLime,
                            radius = 10.dp.toPx(),
                            center = center
                        )
                    }
                    else -> {
                        // Analytics Growth Rings
                        drawArc(
                            color = KinetiqLime,
                            startAngle = -90f,
                            sweepAngle = 270f * pulseGlow,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = Color.White.copy(alpha = 0.5f),
                            startAngle = -90f,
                            sweepAngle = 200f,
                            useCenter = false,
                            topLeft = Offset(center.x - radius * 0.7f, center.y - radius * 0.7f),
                            size = androidx.compose.ui.geometry.Size(radius * 1.4f, radius * 1.4f),
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Surface(
            color = KinetiqLime.copy(alpha = 0.1f),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.2f))
        ) {
            Text(
                text = page.subtitle,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = KinetiqLime,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = page.title.uppercase(),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = KinetiqGray,
                lineHeight = 24.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

