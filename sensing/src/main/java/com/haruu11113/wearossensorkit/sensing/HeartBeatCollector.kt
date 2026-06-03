package com.haruu11113.wearossensorkit.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class HeartBeatCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_HEART_BEAT
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_HEART_BEAT,
            values = event.values.clone(),  // [confidence] 0.0〜1.0
            timestampNs = event.timestamp
        )
    }
}
