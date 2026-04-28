package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class SkinTemperatureCollector(
    context: Context
) : BaseSensorCollector(context) {

    // ⚠️ 暫定フォールバック: TYPE_AMBIENT_TEMPERATURE（環境温度）を使用しているが、
    // これは皮膚温度センサとは別物であることに注意。
    // TYPE_SKIN_TEMPERATURE の実際の定数値はデバイス依存（非公開定数）。
    // Pixel Watch 2 では 65536 + 55 = 65591 が該当することが多い（要実機確認）。
    // 実機で `sensorManager.getSensorList(Sensor.TYPE_ALL)` を使い "skin" や "temperature" を
    // 含むセンサを特定してから、この値を適切な定数に置き換えること。
    override val sensorType: Int = Sensor.TYPE_AMBIENT_TEMPERATURE  // TODO: 実機確認後に皮膚温度センサの定数値に修正すること
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_SKIN_TEMPERATURE,
            values = event.values.clone(),  // [温度] ℃
            timestampNs = event.timestamp
        )
    }
}
