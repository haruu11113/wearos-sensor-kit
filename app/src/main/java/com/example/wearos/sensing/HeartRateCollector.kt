package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

/**
 * 心拍数センサーからデータを収集する Collector。
 *
 * **必要なパーミッション:** `android.permission.BODY_SENSORS`
 * このパーミッションが付与されていない場合、センサーは利用できない。
 */
class HeartRateCollector(
    context: Context,
    listener: SensorCollectorListener
) : BaseSensorCollector(context, listener) {

    override val sensorType: Int = Sensor.TYPE_HEART_RATE
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_GAME

    override fun formatData(event: SensorEvent): SensorData =
        SensorData(
            type = SensorData.TYPE_HEART_RATE,
            values = event.values.clone(),
            timestampNs = event.timestamp
        )
}
