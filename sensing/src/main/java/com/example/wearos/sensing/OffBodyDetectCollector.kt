package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class OffBodyDetectCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_OFF_BODY_DETECT,
            values = event.values.clone(),  // [0.0 = 装着中, 1.0 = 非装着]
            timestampNs = event.timestamp
        )
    }
}
