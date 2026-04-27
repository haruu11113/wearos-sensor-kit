package com.example.wearos.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import com.example.wearos.sensor.BaseSensorService
import android.util.Log


class HeartRateSensorService : BaseSensorService() {
    override fun onCreate() {
        super.onCreate()
        Log.i("HeartRateSensorService", "created")
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        this.sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        if (this.sensor != null) {
            sensorManager.registerListener(this, this.sensor, SensorManager.SENSOR_DELAY_GAME) //SENSOR_DELAY_NORMAL
            Log.i("HeartRateSensorService", "Sensor: $sensor")
        } else {
            Log.e("HeartRateSensorService", "The sensor not available.")
        }
    }

    override fun formatMessage(event: SensorEvent): String {
        var message: String = "heart_rate,${event.values.joinToString(",")},${event.timestamp}\n"
        return message
    }
}
