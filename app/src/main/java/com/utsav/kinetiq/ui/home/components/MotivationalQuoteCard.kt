package com.utsav.kinetiq.ui.home.components

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utsav.kinetiq.data.local.QuotesData
import com.utsav.kinetiq.domain.model.Quote
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime
import kotlinx.coroutines.isActive

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MotivationalQuoteCard(
    modifier: Modifier = Modifier,
    quotes: List<Quote> = QuotesData.quotes,
) {
    if (quotes.isEmpty()) return

    var currentIndex by remember { mutableIntStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    val timerProgress = remember { Animatable(0f) }

    val currentQuote = quotes[currentIndex % quotes.size]

    // 15-second Timer Loop
    LaunchedEffect(currentIndex, isPaused) {
        if (!isPaused) {
            timerProgress.snapTo(0f)
            val animationResult = timerProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 15_000,
                    easing = LinearEasing
                )
            )
            if ((animationResult.endReason == androidx.compose.animation.core.AnimationEndReason.Finished) && isActive) {
                currentIndex = (currentIndex + 1) % quotes.size
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = KinetiqLime.copy(alpha = 0.25f)
            ),
        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.25f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            KinetiqLime.copy(alpha = 0.08f),
                            Color.Transparent,
                            KinetiqLime.copy(alpha = 0.03f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category & Lightning Bolt Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = KinetiqLime.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.4f))
                        ) {
                            Box(
                                modifier = Modifier.padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = "Lightning",
                                    tint = KinetiqLime,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = KinetiqLime.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.2f))
                        ) {
                            Text(
                                text = "${currentQuote.category.emoji} ${currentQuote.category.label}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = KinetiqLime,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Quote Index Counter & Controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${(currentIndex % quotes.size) + 1}/${quotes.size}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = KinetiqGray,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(end = 6.dp)
                        )

                        // Previous Button
                        IconButton(
                            onClick = {
                                currentIndex = if (currentIndex - 1 < 0) quotes.size - 1 else currentIndex - 1
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Quote",
                                tint = KinetiqGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Play/Pause Button
                        IconButton(
                            onClick = { isPaused = !isPaused },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = if (isPaused) "Resume" else "Pause",
                                tint = if (isPaused) KinetiqLime else KinetiqGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Next Button
                        IconButton(
                            onClick = {
                                currentIndex = (currentIndex + 1) % quotes.size
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Quote",
                                tint = KinetiqGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Animated Quote Body
                AnimatedContent(
                    targetState = currentQuote,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(500)) + slideInVertically(animationSpec = tween(500)) { height -> height / 2 })
                            .togetherWith(fadeOut(animationSpec = tween(400)) + slideOutVertically(animationSpec = tween(400)) { height -> -height / 2 })
                    },
                    label = "QuoteTransition"
                ) { quote ->
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "“",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    color = KinetiqLime.copy(alpha = 0.25f),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.offset(x = (-4).dp, y = (-12).dp)
                            )

                            Text(
                                text = quote.text,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 17.sp,
                                    lineHeight = 25.sp
                                ),
                                modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "— ${quote.author.uppercase()}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = KinetiqLime,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 15-Second Progress Timer Bar
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(CircleShape)
                            .background(KinetiqLime.copy(alpha = 0.12f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(if (isPaused) 1f else timerProgress.value)
                                .clip(CircleShape)
                                .background(
                                    if (isPaused) KinetiqGray.copy(alpha = 0.5f)
                                    else KinetiqLime
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isPaused) "PAUSED" else "AUTO-ROTATING (15s)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isPaused) KinetiqGray else KinetiqLime.copy(alpha = 0.7f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.8.sp
                            )
                        )

                        Text(
                            text = "⚡ KINETIQ DAILY MOTIVATION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = KinetiqGray.copy(alpha = 0.6f),
                                fontSize = 9.sp,
                                letterSpacing = 0.8.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
