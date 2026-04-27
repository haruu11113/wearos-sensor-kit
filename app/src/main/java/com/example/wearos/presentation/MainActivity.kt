package com.example.wearos.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.wearos.R
import com.example.wearos.presentation.theme.WearosTheme
import com.example.wearos.sensor.AccelerometerSensorService
import com.example.wearos.sensor.HeartRateSensorService
import com.example.wearos.sensor.LightSensorService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp("Android")
        }
    }
}

@Composable
fun WearApp(greetingName: String) {
    val context = LocalContext.current
    var hasBodySensorsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BODY_SENSORS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasBodySensorsPermission = isGranted
    }

    val udpAddress = "192.168.50.236" // Replace with your desired IP address
    val udpPort = 6666 // Replace with your desired port

    WearosTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (hasBodySensorsPermission) {
                var isSensing by remember { mutableStateOf(false) }

                Greeting(greetingName = if (isSensing) "Sensing..." else "Tap to start sensor")

                Button(onClick = {
                    // Start services

                    if (isSensing) {
                        // Stop services
                        context.stopService(Intent(context, AccelerometerSensorService::class.java))
                        context.stopService(Intent(context, HeartRateSensorService::class.java))
                        context.stopService(Intent(context, LightSensorService::class.java))
                    } else {
                        context.startService(Intent(context, AccelerometerSensorService::class.java).apply {
                            putExtra(com.example.wearos.sensor.BaseSensorService.EXTRA_UDP_ADDRESS, udpAddress)
                            putExtra(com.example.wearos.sensor.BaseSensorService.EXTRA_UDP_PORT, udpPort)
                        })
                        context.startService(Intent(context, LightSensorService::class.java).apply {
                            putExtra(com.example.wearos.sensor.BaseSensorService.EXTRA_UDP_ADDRESS, udpAddress)
                            putExtra(com.example.wearos.sensor.BaseSensorService.EXTRA_UDP_PORT, udpPort)
                        })
                        context.startService(Intent(context, HeartRateSensorService::class.java).apply {
                            putExtra(com.example.wearos.sensor.BaseSensorService.EXTRA_UDP_ADDRESS, udpAddress)
                            putExtra(com.example.wearos.sensor.BaseSensorService.EXTRA_UDP_PORT, udpPort)
                        })
                    }
                    isSensing = !isSensing // Toggle the state
                }) {
                    Text(if (isSensing) "Stop" else "Start")
                }
            } else {
                Greeting(greetingName = "Tap to request permission")
                Button(onClick = {
                    launcher.launch(Manifest.permission.BODY_SENSORS)
                }) {
                    Text("Request Permission")
                }
            }
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = greetingName
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}