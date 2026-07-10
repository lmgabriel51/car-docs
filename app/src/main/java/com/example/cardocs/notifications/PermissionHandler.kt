package com.example.cardocs.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlin.contracts.contract

@Composable
fun RequestNotificationPermission(onPermissionResult: (Boolean) -> Unit){
    val context = LocalContext.current

    // Only needed for Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) {
            isGranted -> onPermissionResult(isGranted)
        }

        LaunchedEffect(Unit) {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    onPermissionResult(true)
                }
                else -> {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    } else {
        // Permission not needed for older Android versions
        LaunchedEffect(Unit) {
            onPermissionResult(true)
        }
    }
}