package com.haruu11113.wearossensorkit.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class StepDetectorCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_STEP_DETECTOR
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_STEP_DETECTOR,
            values = event.values.clone(),  // [1.0] 常に 1.0（歩行検出イベント）
            timestampNs = event.timestamp
        )
    }
}
