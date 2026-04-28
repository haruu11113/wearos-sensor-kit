package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class LinearAccelerationCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_LINEAR_ACCELERATION
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_GAME

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_LINEAR_ACCELERATION,
            values = event.values.clone(),  // [x, y, z] m/s²（重力除去済み）
            timestampNs = event.timestamp
        )
    }
}
