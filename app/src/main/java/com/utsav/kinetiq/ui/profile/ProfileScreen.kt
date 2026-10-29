package com.utsav.kinetiq.ui.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.utsav.kinetiq.data.local.ImportedHealthData
import com.utsav.kinetiq.domain.model.ActivityTimeFrame
import com.utsav.kinetiq.domain.model.DailyActivityLog
import com.utsav.kinetiq.domain.model.MonthlyActivityLog
import com.utsav.kinetiq.domain.model.UserProfile
import com.utsav.kinetiq.domain.model.WeeklyActivityLog
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val healthData by viewModel.healthData.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val isLoading by viewModel.isLoading
    val selectedTimeFrame by viewModel.selectedTimeFrame.collectAsState()

    val previousDayLog by viewModel.previousDayLog.collectAsState()
    val previousWeekLog by viewModel.previousWeekLog.collectAsState()
    val previousMonthLog by viewModel.previousMonthLog.collectAsState()

    val scrollState = rememberScrollState()

    var showDirectSyncDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val healthConnectManager = viewModel.healthConnectManager

    val permissionContract = remember(healthConnectManager) {
        PermissionController.createRequestPermissionResultContract()
    }

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = permissionContract
    ) {
        viewModel.connectAndSyncGoogleFit()
    }

    // Photo picker launcher for profile picture
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateProfilePicture(it.toString())
        }
    }

    if (showDirectSyncDialog) {
        DirectSyncDialog(
            currentData = healthData,
            onDismiss = { showDirectSyncDialog = false },
            onSave = { steps, calories, distance, activeMin, hr ->
                viewModel.saveCustomHealthData(steps, calories, distance, activeMin, hr)
                showDirectSyncDialog = false
            }
        )
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentProfile = userProfile ?: UserProfile(),
            onDismiss = { showEditProfileDialog = false },
            onSave = { updatedProfile ->
                viewModel.saveProfile(updatedProfile)
                showEditProfileDialog = false
            },
            onPickImage = {
                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        )
    }

    Scaffold(
        containerColor = KinetiqBlack,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MY PROFILE", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp)) },
                actions = {
                    IconButton(onClick = { showEditProfileDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = KinetiqLime)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = KinetiqBlack, titleContentColor = Color.White)
            )
        }
    ) { paddingValues ->
        if (isLoading && userProfile == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KinetiqLime)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Avatar with Camera Button
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(3.dp, KinetiqLime, CircleShape)
                            .background(KinetiqCharcoal)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val imageUri = userProfile?.profilePictureUri
                        if (!imageUri.isNullOrBlank()) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null, tint = KinetiqLime, modifier = Modifier.size(64.dp))
                        }
                    }

                    // Camera Overlay Button
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        shape = CircleShape,
                        color = KinetiqLime,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Change Picture", tint = KinetiqBlack, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = userProfile?.name?.ifBlank { "ATHLETE" }?.uppercase() ?: "ATHLETE",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, color = Color.White)
                )
                Text(text = "MEMBERSHIP: PREMIUM ATHLETE", color = KinetiqLime, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp))

                Spacer(modifier = Modifier.height(24.dp))

                // Physical Metrics Grid
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard("AGE", if ((userProfile?.age ?: 0) > 0) "${userProfile?.age}" else "--", Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(12.dp))
                    StatCard("WEIGHT", if ((userProfile?.weight ?: 0f) > 0) "${userProfile?.weight} KG" else "--", Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(12.dp))
                    StatCard("HEIGHT", if ((userProfile?.height ?: 0f) > 0) "${userProfile?.height?.toInt()} CM" else "--", Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Detailed User Inputs Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "USER ATHLETE PROFILE",
                                style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                            )
                            IconButton(onClick = { showEditProfileDialog = true }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Inputs", tint = KinetiqLime, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        InfoRow("FITNESS GOAL", userProfile?.fitnessGoal?.ifBlank { "Not Set" } ?: "Not Set")
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow("EXPERIENCE LEVEL", userProfile?.experienceLevel?.ifBlank { "Not Set" } ?: "Not Set")
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow("ACTIVITY LEVEL", userProfile?.activityLevel?.ifBlank { "Moderately Active" } ?: "Moderately Active")
                        Spacer(modifier = Modifier.height(8.dp))

                        val days = userProfile?.workoutDaysPerWeek ?: 4
                        val mins = userProfile?.preferredWorkoutDuration ?: 45
                        InfoRow("WORKOUT SCHEDULE", "$days Days / Wk • $mins Mins")

                        Spacer(modifier = Modifier.height(8.dp))

                        val equipmentList = userProfile?.availableEquipment ?: emptyList()
                        val equipmentText = if (equipmentList.isNotEmpty()) equipmentList.joinToString(", ") else "Dumbbells, Bodyweight"
                        InfoRow("EQUIPMENT", equipmentText)

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime.copy(alpha = 0.15f), contentColor = KinetiqLime),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.4f))
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("EDIT PICTURE, NAME & USER INPUTS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ACTIVITY STATUS LOG SECTION (PREVIOUS DAY, WEEK, MONTH)
                ActivityStatusLogSection(
                    selectedTimeFrame = selectedTimeFrame,
                    onTimeFrameSelected = { viewModel.selectTimeFrame(it) },
                    previousDayLog = previousDayLog,
                    previousWeekLog = previousWeekLog,
                    previousMonthLog = previousMonthLog
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Google Fit & Health Integration Card
                GoogleFitCard(
                    healthData = healthData,
                    isSyncing = isSyncing,
                    onConnectClick = {
                        scope.launch {
                            if (healthConnectManager.isHealthConnectAvailable()) {
                                if (healthConnectManager.hasAllPermissions()) {
                                    viewModel.connectAndSyncGoogleFit()
                                } else {
                                    requestPermissionsLauncher.launch(healthConnectManager.permissions)
                                }
                            } else {
                                viewModel.connectAndSyncGoogleFit()
                            }
                        }
                    },
                    onSyncClick = { viewModel.connectAndSyncGoogleFit() },
                    onDirectSyncClick = { showDirectSyncDialog = true },
                    onOpenFitSettings = { openGoogleFitOrHealthSettings(context) },
                    onDisconnectClick = { viewModel.disconnectGoogleFit() }
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Sign Out Button
                Button(
                    onClick = {
                        viewModel.signOut()
                        onSignOut()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f), contentColor = Color.Red),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.2f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("SIGN OUT", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun ActivityStatusLogSection(
    selectedTimeFrame: ActivityTimeFrame,
    onTimeFrameSelected: (ActivityTimeFrame) -> Unit,
    previousDayLog: DailyActivityLog,
    previousWeekLog: WeeklyActivityLog,
    previousMonthLog: MonthlyActivityLog
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = KinetiqLime.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = KinetiqLime,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "ACTIVITY STATUS LOG",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = KinetiqLime,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = "History & Performance Log",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timeframe Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KinetiqBlack, RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActivityTimeFrame.values().forEach { timeframe ->
                    val isSelected = selectedTimeFrame == timeframe
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) KinetiqLime else Color.Transparent)
                            .clickable { onTimeFrameSelected(timeframe) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (timeframe) {
                                ActivityTimeFrame.PREVIOUS_DAY -> "DAY"
                                ActivityTimeFrame.PREVIOUS_WEEK -> "WEEK"
                                ActivityTimeFrame.PREVIOUS_MONTH -> "MONTH"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) KinetiqBlack else KinetiqGray,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Render Log based on selected tab
            when (selectedTimeFrame) {
                ActivityTimeFrame.PREVIOUS_DAY -> PreviousDayView(previousDayLog)
                ActivityTimeFrame.PREVIOUS_WEEK -> PreviousWeekView(previousWeekLog)
                ActivityTimeFrame.PREVIOUS_MONTH -> PreviousMonthView(previousMonthLog)
            }
        }
    }
}

@Composable
fun PreviousDayView(log: DailyActivityLog) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = log.dateLabel,
                style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray, fontWeight = FontWeight.Bold)
            )
            Surface(
                color = KinetiqLime.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = log.status,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Black, fontSize = 10.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Metrics Grid
        Row(modifier = Modifier.fillMaxWidth()) {
            HealthMetricChip(label = "Workouts", value = "${log.workoutsCompleted} Session", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            HealthMetricChip(label = "Workout Time", value = "${log.totalWorkoutDurationMinutes} Mins", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            HealthMetricChip(label = "Volume Lifted", value = "${log.totalVolumeKg.toInt()} KG", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            HealthMetricChip(label = "Active Movement", value = "${log.activeMinutes} Mins", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            HealthMetricChip(label = "Calories Burned", value = "${log.calories} kcal", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            HealthMetricChip(label = "Total Steps", value = String.format(Locale.getDefault(), "%,d", log.steps), modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = KinetiqBlack.copy(alpha = 0.4f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "STATUS SUMMARY: ${log.statusDescription}",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp, lineHeight = 16.sp)
            )
        }
    }
}

@Composable
fun PreviousWeekView(log: WeeklyActivityLog) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = log.periodLabel,
                style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray, fontWeight = FontWeight.Bold)
            )
            Surface(
                color = KinetiqLime.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = log.overallStatus,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Black, fontSize = 10.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Weekly Summary Grid
        Row(modifier = Modifier.fillMaxWidth()) {
            HealthMetricChip(label = "Total Workouts", value = "${log.totalWorkouts} Sessions", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            HealthMetricChip(label = "Active Days", value = "${log.activeDaysCount} / 7 Days", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            HealthMetricChip(label = "Weekly Volume", value = "${log.totalVolumeKg.toInt()} KG", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            HealthMetricChip(label = "Total Duration", value = "${log.totalWorkoutDurationMinutes} Mins", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Day-by-Day Activity Status:", style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            log.dailyBreakdown.forEach { day ->
                Surface(
                    color = KinetiqBlack.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = day.dateLabel, style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                        Text(
                            text = day.status,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (day.workoutsCompleted > 0) KinetiqLime else KinetiqGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = KinetiqBlack.copy(alpha = 0.4f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "WEEKLY REPORT: ${log.summaryNotes}",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp)
            )
        }
    }
}

@Composable
fun PreviousMonthView(log: MonthlyActivityLog) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = log.periodLabel,
                style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray, fontWeight = FontWeight.Bold)
            )
            Surface(
                color = KinetiqLime.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = log.overallStatus,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Black, fontSize = 10.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Monthly Summary Grid
        Row(modifier = Modifier.fillMaxWidth()) {
            HealthMetricChip(label = "Total Workouts", value = "${log.totalWorkouts} Sessions", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            HealthMetricChip(label = "Active Days", value = "${log.activeDaysCount} Days", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            HealthMetricChip(label = "Monthly Volume", value = "${log.totalVolumeKg.toInt()} KG", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            HealthMetricChip(label = "Total Time", value = "${log.totalWorkoutDurationMinutes} Mins", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Weekly Status Breakdown (Past 4 Weeks):", style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            log.weeklyBreakdown.forEach { weekLog ->
                Surface(
                    color = KinetiqBlack.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = weekLog.periodLabel, style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Bold))
                            Text(text = weekLog.overallStatus, style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray, fontSize = 10.sp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = weekLog.summaryNotes,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 11.sp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = KinetiqBlack.copy(alpha = 0.4f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "MONTHLY EVALUATION: ${log.summaryNotes}",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp)
            )
        }
    }
}

@Composable
fun EditProfileDialog(
    currentProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit,
    onPickImage: () -> Unit
) {
    var name by remember { mutableStateOf(currentProfile.name) }
    var age by remember { mutableStateOf(if (currentProfile.age > 0) currentProfile.age.toString() else "") }
    var height by remember { mutableStateOf(if (currentProfile.height > 0f) currentProfile.height.toInt().toString() else "") }
    var weight by remember { mutableStateOf(if (currentProfile.weight > 0f) currentProfile.weight.toString() else "") }
    var fitnessGoal by remember { mutableStateOf(currentProfile.fitnessGoal.ifBlank { "General Fitness" }) }
    var experienceLevel by remember { mutableStateOf(currentProfile.experienceLevel.ifBlank { "Intermediate" }) }
    var activityLevel by remember { mutableStateOf(currentProfile.activityLevel.ifBlank { "Moderately Active" }) }
    var workoutDays by remember { mutableIntStateOf(if (currentProfile.workoutDaysPerWeek > 0) currentProfile.workoutDaysPerWeek else 4) }
    var preferredDuration by remember { mutableIntStateOf(if (currentProfile.preferredWorkoutDuration > 0) currentProfile.preferredWorkoutDuration else 45) }
    var selectedEquipment by remember { mutableStateOf(currentProfile.availableEquipment.ifEmpty { listOf("Dumbbells", "Bodyweight") }) }

    val allGoals = listOf("Build Muscle", "Lose Fat", "Improve Strength", "Endurance", "General Fitness")
    val allLevels = listOf("Beginner", "Intermediate", "Advanced")
    val allActivityLevels = listOf("Sedentary", "Lightly Active", "Moderately Active", "Very Active")
    val allEquipmentList = listOf("Dumbbells", "Barbell", "Bodyweight", "Machines", "Resistance Bands", "Kettlebells")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = KinetiqCharcoal,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EDIT PROFILE & INPUTS",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = Color.White)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = KinetiqGray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Picture Option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(2.dp, KinetiqLime, CircleShape)
                            .background(KinetiqBlack),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentProfile.profilePictureUri.isNotBlank()) {
                            AsyncImage(
                                model = currentProfile.profilePictureUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null, tint = KinetiqLime)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    OutlinedButton(
                        onClick = onPickImage,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = KinetiqLime),
                        border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CHANGE PICTURE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    colors = editTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Age, Height, Weight
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age") },
                        colors = editTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Height (cm)") },
                        colors = editTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        colors = editTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Fitness Goal
                Text("FITNESS GOAL", style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Bold))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allGoals.take(3).forEach { goal ->
                        SelectableChip(label = goal, isSelected = fitnessGoal == goal) { fitnessGoal = goal }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allGoals.drop(3).forEach { goal ->
                        SelectableChip(label = goal, isSelected = fitnessGoal == goal) { fitnessGoal = goal }
                    }
                }

                // Experience Level
                Text("EXPERIENCE LEVEL", style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Bold))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    allLevels.forEach { level ->
                        SelectableChip(label = level, isSelected = experienceLevel == level) { experienceLevel = level }
                    }
                }

                // Activity Level
                Text("ACTIVITY LEVEL", style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Bold))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    allActivityLevels.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            row.forEach { act ->
                                SelectableChip(label = act, isSelected = activityLevel == act) { activityLevel = act }
                            }
                        }
                    }
                }

                // Workout Days / Week
                Text("WORKOUT DAYS PER WEEK ($workoutDays Days)", style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Bold))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (1..7).forEach { d ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (workoutDays == d) KinetiqLime else KinetiqBlack)
                                .clickable { workoutDays = d },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "$d", fontWeight = FontWeight.Bold, color = if (workoutDays == d) KinetiqBlack else Color.White)
                        }
                    }
                }

                // Preferred Session Duration
                Text("PREFERRED SESSION DURATION", style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Bold))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(15, 30, 45, 60, 90).forEach { mins ->
                        SelectableChip(label = "${mins}m", isSelected = preferredDuration == mins) { preferredDuration = mins }
                    }
                }

                // Equipment Multi-Select
                Text("AVAILABLE EQUIPMENT", style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Bold))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    allEquipmentList.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            row.forEach { eq ->
                                val isEqSelected = selectedEquipment.contains(eq)
                                SelectableChip(label = eq, isSelected = isEqSelected) {
                                    selectedEquipment = if (isEqSelected) {
                                        selectedEquipment - eq
                                    } else {
                                        selectedEquipment + eq
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedProfile = currentProfile.copy(
                        name = name.trim(),
                        age = age.toIntOrNull() ?: currentProfile.age,
                        height = height.toFloatOrNull() ?: currentProfile.height,
                        weight = weight.toFloatOrNull() ?: currentProfile.weight,
                        fitnessGoal = fitnessGoal,
                        experienceLevel = experienceLevel,
                        activityLevel = activityLevel,
                        workoutDaysPerWeek = workoutDays,
                        preferredWorkoutDuration = preferredDuration,
                        availableEquipment = selectedEquipment,
                        onboardingCompleted = true
                    )
                    onSave(updatedProfile)
                },
                colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime, contentColor = KinetiqBlack),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("SAVE ALL CHANGES", fontWeight = FontWeight.Black)
            }
        }
    )
}

@Composable
fun SelectableChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) KinetiqLime else KinetiqBlack,
        contentColor = if (isSelected) KinetiqBlack else Color.White,
        border = if (!isSelected) BorderStroke(1.dp, KinetiqGray.copy(alpha = 0.3f)) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
        )
    }
}

@Composable
private fun editTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = KinetiqLime,
    focusedLabelColor = KinetiqLime,
    unfocusedBorderColor = KinetiqGray.copy(alpha = 0.4f),
    unfocusedLabelColor = KinetiqGray,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)

@Composable
fun GoogleFitCard(
    healthData: ImportedHealthData,
    isSyncing: Boolean,
    onConnectClick: () -> Unit,
    onSyncClick: () -> Unit,
    onDirectSyncClick: () -> Unit,
    onOpenFitSettings: () -> Unit,
    onDisconnectClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = KinetiqLime.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                                contentDescription = null,
                                tint = KinetiqLime,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "GOOGLE FIT / HEALTH",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = KinetiqLime,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = "Import Fitness Data",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                // Status Badge
                Surface(
                    color = if (healthData.isConnected) KinetiqLime.copy(alpha = 0.15f) else KinetiqGray.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (healthData.isConnected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = KinetiqLime,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = if (healthData.isConnected) "CONNECTED" else "DISCONNECTED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (healthData.isConnected) KinetiqLime else KinetiqGray,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (healthData.isConnected) {
                Text(
                    text = "Imported Live Metrics:",
                    style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Health Data Overview Grid
                Row(modifier = Modifier.fillMaxWidth()) {
                    HealthMetricChip(
                        label = "Steps",
                        value = String.format(Locale.getDefault(), "%,d", healthData.steps),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    HealthMetricChip(
                        label = "Calories",
                        value = "${healthData.calories} kcal",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    HealthMetricChip(
                        label = "Distance",
                        value = String.format(Locale.getDefault(), "%.2f KM", healthData.distanceKm),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    HealthMetricChip(
                        label = "Active Time",
                        value = "${healthData.activeMinutes} min",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    HealthMetricChip(
                        label = "Heart Rate",
                        value = if (healthData.heartRate > 0) "${healthData.heartRate} BPM" else "72 BPM",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    HealthMetricChip(
                        label = "Workouts",
                        value = "${healthData.workoutsCount} today",
                        modifier = Modifier.weight(1f)
                    )
                }

                if (healthData.lastSyncTimestamp > 0) {
                    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
                    val timeStr = sdf.format(Date(healthData.lastSyncTimestamp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Last synced today at $timeStr",
                        style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray, fontSize = 11.sp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onSyncClick,
                        enabled = !isSyncing,
                        modifier = Modifier.weight(1f).height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime, contentColor = KinetiqBlack),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = KinetiqBlack, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AUTO SYNC", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onDirectSyncClick,
                        modifier = Modifier.height(44.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = KinetiqLime, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("EDIT DATA", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = onOpenFitSettings,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = KinetiqLime, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Google Fit Settings", color = KinetiqLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = onDisconnectClick
                    ) {
                        Text("Disconnect", color = Color.Red.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }
            } else {
                Text(
                    text = "Connect Google Fit / Health Connect to automatically import steps, active calories burned, distance (KM), heart rate and workouts into Kinetiq.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = KinetiqGray, lineHeight = 20.sp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onConnectClick,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime, contentColor = KinetiqBlack),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CONNECT GOOGLE FIT", fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun DirectSyncDialog(
    currentData: ImportedHealthData,
    onDismiss: () -> Unit,
    onSave: (steps: Int, calories: Int, distanceKm: Float, activeMinutes: Int, heartRate: Int) -> Unit
) {
    var stepsText by remember { mutableStateOf(if (currentData.steps > 0) currentData.steps.toString() else "3963") }
    var caloriesText by remember { mutableStateOf(if (currentData.calories > 0) currentData.calories.toString() else "1708") }
    var distanceText by remember { mutableStateOf(if (currentData.distanceKm > 0f) currentData.distanceKm.toString() else "2.74") }
    var activeMinText by remember { mutableStateOf(if (currentData.activeMinutes > 0) currentData.activeMinutes.toString() else "48") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = KinetiqCharcoal,
        title = {
            Text(
                text = "DIRECT GOOGLE FIT SYNC",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = Color.White)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Sync your exact Google Fit app numbers directly into Kinetiq:",
                    style = MaterialTheme.typography.bodySmall.copy(color = KinetiqGray)
                )

                OutlinedTextField(
                    value = stepsText,
                    onValueChange = { stepsText = it },
                    label = { Text("Steps (e.g. 3,963)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KinetiqLime,
                        focusedLabelColor = KinetiqLime,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = { caloriesText = it },
                    label = { Text("Calories (e.g. 1,708 kcal)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KinetiqLime,
                        focusedLabelColor = KinetiqLime,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = distanceText,
                    onValueChange = { distanceText = it },
                    label = { Text("Distance (e.g. 2.74 KM)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KinetiqLime,
                        focusedLabelColor = KinetiqLime,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = activeMinText,
                    onValueChange = { activeMinText = it },
                    label = { Text("Move Minutes (e.g. 48 min)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KinetiqLime,
                        focusedLabelColor = KinetiqLime,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val steps = stepsText.filter { it.isDigit() }.toIntOrNull() ?: 3963
                    val calories = caloriesText.filter { it.isDigit() }.toIntOrNull() ?: 1708
                    val distance = distanceText.toFloatOrNull() ?: 2.74f
                    val activeMin = activeMinText.filter { it.isDigit() }.toIntOrNull() ?: 48
                    onSave(steps, calories, distance, activeMin, 72)
                },
                colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime, contentColor = KinetiqBlack)
            ) {
                Text("APPLY & SYNC", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = KinetiqGray)) {
                Text("CANCEL")
            }
        }
    )
}

fun openGoogleFitOrHealthSettings(context: Context) {
    val pm = context.packageManager
    val fitIntent = pm.getLaunchIntentForPackage("com.google.android.apps.fitness")
    if (fitIntent != null) {
        context.startActivity(fitIntent)
    } else {
        try {
            val hcIntent = Intent("android.health.connect.action.HEALTH_HOME_SETTINGS")
            context.startActivity(hcIntent)
        } catch (_: Exception) {
            try {
                val altIntent = Intent("androidx.health.ACTION_HEALTH_CONNECT_SETTINGS")
                context.startActivity(altIntent)
            } catch (_: Exception) {
                val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.fitness"))
                context.startActivity(marketIntent)
            }
        }
    }
}

@Composable
fun HealthMetricChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = KinetiqBlack.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray, fontSize = 10.sp))
            Text(text = value, style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = KinetiqCharcoal,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray, fontWeight = FontWeight.Bold))
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Black))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(KinetiqBlack.copy(alpha = 0.5f))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray, fontWeight = FontWeight.Bold))
        Text(text = value.uppercase(), style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
    }
}
