package com.haruu11113.wearossensorkit.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class GyroscopeCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_GYROSCOPE
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_GAME

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_GYROSCOPE,
            values = event.values.clone(),  // [x, y, z] rad/s
            timestampNs = event.timestamp
        )
    }
}
