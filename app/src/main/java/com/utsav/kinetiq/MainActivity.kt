package com.utsav.kinetiq

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.utsav.kinetiq.navigation.KinetiqNavHost
import com.utsav.kinetiq.navigation.SplashRoute
import com.utsav.kinetiq.ui.theme.KinetiqTheme
import com.utsav.kinetiq.utils.NotificationHelper

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),

    ) { isGranted: Boolean ->
        if (isGranted) {
            scheduleNotifications()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setupNotifications()

        setContent {
            KinetiqTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    KinetiqNavHost(
                        navController = navController,
                        startDestination = SplashRoute
                    )
                }
            }
        }
    }

    private fun setupNotifications() {
        val helper = NotificationHelper(this)
        helper.createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                )) {
                PackageManager.PERMISSION_GRANTED -> {
                    scheduleNotifications()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            scheduleNotifications()
        }
    }

    private fun scheduleNotifications() {
        NotificationHelper(this).scheduleAllNotifications()
    }
}
