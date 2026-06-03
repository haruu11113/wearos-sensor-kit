package com.haruu11113.wearossensorkit.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class RotationVectorCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_ROTATION_VECTOR
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_GAME

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_ROTATION_VECTOR,
            values = event.values.clone(),  // [x*sin(θ/2), y*sin(θ/2), z*sin(θ/2), cos(θ/2)]
            timestampNs = event.timestamp
        )
    }
}
