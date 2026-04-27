package com.example.wearos.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log


class AccelerometerSensorService : BaseSensorService() {
    override fun onCreate() {
        super.onCreate()
        Log.i("AccelerometerSensorService", "created")
//        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        this.sensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (this.sensor != null) {
            this.sensorManager.registerListener(this, this.sensor, SensorManager.SENSOR_DELAY_GAME) //SENSOR_DELAY_NORMAL
            Log.i("AccelerometerSensorService", "Sensor: $sensor")
        } else {
            Log.e("AccelerometerSensorService", "The sensor not available.")
        }
    }

    override fun formatMessage(event: SensorEvent): String {
        var message: String = "acc,${event.values.joinToString(",")}, ${event.timestamp}\n"
        return message
    }
}
