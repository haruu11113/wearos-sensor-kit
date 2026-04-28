# 018 GyroscopeCollector の実装

## 概要

ジャイロスコープ（角速度）センサのコレクターを実装する。
姿勢変化・回転動作の検出に使用する。

## 前提

- 017 完了済みであること（`SensorData.TYPE_GYROSCOPE` が存在する）

## 対象ファイル

新規作成: `sensing/src/main/java/com/example/wearos/sensing/GyroscopeCollector.kt`

## 実装内容

```kotlin
package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class GyroscopeCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_GYROSCOPE
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_GAME

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_GYROSCOPE,
            values = event.values.clone(),  // [x, y, z] rad/s
            timestampNs = event.timestamp
        )
    }
}
```

## 取得できる値

| index | 内容 | 単位 |
|-------|------|------|
| 0 | X 軸角速度 | rad/s |
| 1 | Y 軸角速度 | rad/s |
| 2 | Z 軸角速度 | rad/s |

## 完了条件

- `GyroscopeCollector.kt` が作成されている
- `BaseSensorCollector` を継承している
- ビルドが通ること
