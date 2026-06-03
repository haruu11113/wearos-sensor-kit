package com.haruu11113.wearossensorkit.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.haruu11113.wearossensorkit.presentation.theme.WearosTheme
import com.haruu11113.wearossensorkit.sensor.SensingService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp("Android")
        }
    }
}

private fun requiredPermissions(): Array<String> = buildList {
    add(Manifest.permission.BODY_SENSORS)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        add("android.permission.health.READ_HEART_RATE")
    }
}.toTypedArray()

private fun Context.hasAllPermissions(): Boolean =
    requiredPermissions().all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

@Composable
fun WearApp(greetingName: String) {
    val context = LocalContext.current
    var hasPermissions by remember { mutableStateOf(context.hasAllPermissions()) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasPermissions = results.values.all { it }
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
            if (hasPermissions) {
                var isSensing by remember { mutableStateOf(false) }

                Greeting(greetingName = if (isSensing) "Sensing..." else "Tap to start sensor")

                Button(onClick = {
                    if (isSensing) {
                        context.stopService(Intent(context, SensingService::class.java))
                    } else {
                        context.startService(Intent(context, SensingService::class.java).apply {
                            putExtra(SensingService.EXTRA_UDP_ADDRESS, udpAddress)
                            putExtra(SensingService.EXTRA_UDP_PORT, udpPort)
                        })
                    }
                    isSensing = !isSensing // Toggle the state
                }) {
                    Text(if (isSensing) "Stop" else "Start")
                }
            } else {
                Greeting(greetingName = "Tap to request permission")
                Button(onClick = {
                    launcher.launch(requiredPermissions())
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