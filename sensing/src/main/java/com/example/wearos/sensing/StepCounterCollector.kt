package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class StepCounterCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_STEP_COUNTER
    // STEP_COUNTER はイベント駆動のため SENSOR_DELAY_NORMAL で十分
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_STEP_COUNTER,
            values = event.values.clone(),  // [累積歩数]
            timestampNs = event.timestamp
        )
    }
}
