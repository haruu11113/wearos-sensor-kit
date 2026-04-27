package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class LightCollector(
    context: Context,
    listener: SensorCollectorListener
) : BaseSensorCollector(context, listener) {

    override val sensorType: Int = Sensor.TYPE_LIGHT
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_LIGHT,
            values = event.values.clone(),
            timestampNs = event.timestamp
        )
    }
}
