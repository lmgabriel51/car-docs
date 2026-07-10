package com.example.cardocs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cardocs.ui.AppNavigation
import com.example.cardocs.ui.CarsScreen
import com.example.cardocs.ui.theme.CarDocsTheme
import com.example.cardocs.notifications.RequestNotificationPermission
import com.example.cardocs.notifications.WorkScheduler
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarDocsTheme {
                var permissionGranted by remember { mutableStateOf(false) }

                RequestNotificationPermission { granted ->
                    permissionGranted = granted
                    if (granted) {
                        // Schedule the daily check
                        WorkScheduler.scheduleExpiryCheck(applicationContext)
                    }
                }

                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

