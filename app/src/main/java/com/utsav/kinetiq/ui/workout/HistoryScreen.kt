package com.utsav.kinetiq.ui.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utsav.kinetiq.domain.model.WorkoutSession
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBackClick: () -> Unit,
    viewModel: WorkoutViewModel
) {
    val sessions by viewModel.sessions.collectAsState()

    Scaffold(
        containerColor = KinetiqBlack,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("WORKOUT HISTORY", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = KinetiqBlack)
            )
        }
    ) { paddingValues ->
        if (sessions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No workouts yet. Start your journey!", color = KinetiqGray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 24.dp)
            ) {
                items(sessions) { session ->
                    HistoryItem(session)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(session: WorkoutSession) {
    val dateStr = remember(session.date) {
        SimpleDateFormat("MMM d, yyyy • HH:mm", Locale.getDefault()).format(Date(session.date))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "WORKOUT SESSION",
                        style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = dateStr.uppercase(),
                        style = MaterialTheme.typography.bodySmall.copy(color = KinetiqGray)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SessionMetric(
                    icon = Icons.Default.Timer,
                    label = "DURATION",
                    value = formatDuration(session.durationSeconds)
                )
                SessionMetric(
                    icon = Icons.Default.TrendingUp,
                    label = "VOLUME",
                    value = "${session.totalVolume.toInt()} KG"
                )
                SessionMetric(
                    icon = Icons.Default.CalendarToday,
                    label = "EXERCISES",
                    value = session.completedExercises.size.toString()
                )
            }
        }
    }
}

@Composable
fun SessionMetric(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = KinetiqGray, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray, fontSize = 10.sp))
        }
        Text(value, style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Black))
    }
}

private fun formatDuration(seconds: Long): String {
    val m = seconds / 60
    return "${m}M"
}
