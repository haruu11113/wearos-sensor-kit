# 023 GravityCollector の実装

## 概要

重力センサのコレクターを実装する。
加速度センサから重力成分のみを抽出したソフトウェア合成センサ。
デバイスの傾き（チルト）検出に使用する。

## 前提

- 017 完了済みであること（`SensorData.TYPE_GRAVITY` が存在する）

## 対象ファイル

新規作成: `sensing/src/main/java/com/example/wearos/sensing/GravityCollector.kt`

## 実装内容

```kotlin
package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class GravityCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_GRAVITY
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_GAME

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_GRAVITY,
            values = event.values.clone(),  // [x, y, z] m/s²
            timestampNs = event.timestamp
        )
    }
}
```

## 取得できる値

| index | 内容 | 単位 |
|-------|------|------|
| 0 | 重力 X 成分 | m/s² |
| 1 | 重力 Y 成分 | m/s² |
| 2 | 重力 Z 成分 | m/s² |

静止時、`sqrt(x²+y²+z²) ≈ 9.81 m/s²`

## 完了条件

- `GravityCollector.kt` が作成されている
- `BaseSensorCollector` を継承している
- ビルドが通ること
