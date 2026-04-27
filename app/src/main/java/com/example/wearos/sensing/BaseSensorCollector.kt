package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

abstract class BaseSensorCollector(
    context: Context,
    private val listener: SensorCollectorListener
) : SensorEventListener {

    abstract val sensorType: Int
    abstract val samplingRate: Int
    abstract fun formatData(event: SensorEvent): SensorData

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun start() {
        val sensor = sensorManager.getDefaultSensor(sensorType)
        sensorManager.registerListener(this, sensor, samplingRate)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val data = formatData(event)
        listener.onSensorData(data)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}
