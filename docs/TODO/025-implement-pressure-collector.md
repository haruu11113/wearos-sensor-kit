# 025 PressureCollector の実装

## 概要

気圧センサ（バロメーター）のコレクターを実装する。
大気圧を取得でき、高度変化の推定や天気予測の参考データとして使用できる。

## 前提

- 017 完了済みであること（`SensorData.TYPE_PRESSURE` が存在する）

## 対象ファイル

新規作成: `sensing/src/main/java/com/example/wearos/sensing/PressureCollector.kt`

## 実装内容

```kotlin
package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class PressureCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_PRESSURE
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_PRESSURE,
            values = event.values.clone(),  // [気圧] hPa
            timestampNs = event.timestamp
        )
    }
}
```

## 取得できる値

| index | 内容 | 単位 | 参考値 |
|-------|------|------|--------|
| 0 | 大気圧 | hPa | 海面: 1013.25 hPa |

高度の概算: `SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure)` が使用可能。

## 完了条件

- `PressureCollector.kt` が作成されている
- `BaseSensorCollector` を継承している
- ビルドが通ること
