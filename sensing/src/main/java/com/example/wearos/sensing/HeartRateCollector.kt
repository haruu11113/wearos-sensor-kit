package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

/**
 * 心拍数センサーからデータを収集する Collector。
 *
 * 必要なパーミッション:
 * - `android.permission.BODY_SENSORS`（全 API）
 * - `android.permission.health.READ_HEART_RATE`（API 34 以降）
 *
 * API 34 以降ではいずれか一方でも未付与の場合、センサーは利用できない。
 */
class HeartRateCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_HEART_RATE
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_GAME

    override fun formatData(event: SensorEvent): SensorData =
        SensorData(
            type = SensorData.TYPE_HEART_RATE,
            values = event.values.clone(),
            timestampNs = event.timestamp
        )
}
