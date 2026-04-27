package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class AccelerometerCollector(
    context: Context,
    listener: SensorCollectorListener
) : BaseSensorCollector(context, listener) {

    override val sensorType: Int = Sensor.TYPE_ACCELEROMETER
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_GAME

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_ACCELEROMETER,
            values = event.values.clone(),
            timestampNs = event.timestamp
        )
    }
}
