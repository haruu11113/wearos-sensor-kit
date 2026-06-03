package com.haruu11113.wearossensorkit.sensing

import android.content.Context
import android.hardware.SensorEvent
import android.hardware.SensorManager

class OxygenSaturationCollector(
    context: Context
) : BaseSensorCollector(context) {

    // TYPE_OXYGEN_SATURATION = 65572 (android.hardware.Sensor の非公開定数)
    // Android 14 以降は Sensor.TYPE_OXYGEN_SATURATION が利用可能
    override val sensorType: Int = 65572
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_OXYGEN_SATURATION,
            values = event.values.clone(),  // [SpO2 %]
            timestampNs = event.timestamp
        )
    }
}
