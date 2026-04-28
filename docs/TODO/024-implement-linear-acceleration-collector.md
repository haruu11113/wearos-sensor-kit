# 024 LinearAccelerationCollector の実装

## 概要

線形加速度センサのコレクターを実装する。
加速度センサから重力成分を除いた値を返すソフトウェア合成センサ。
純粋なデバイスの動き（ユーザーの動作）のみを取得したい場合に使用する。

## 前提

- 017 完了済みであること（`SensorData.TYPE_LINEAR_ACCELERATION` が存在する）

## 対象ファイル

新規作成: `sensing/src/main/java/com/example/wearos/sensing/LinearAccelerationCollector.kt`

## 実装内容

```kotlin
package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class LinearAccelerationCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_LINEAR_ACCELERATION
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_GAME

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_LINEAR_ACCELERATION,
            values = event.values.clone(),  // [x, y, z] m/s²（重力除去済み）
            timestampNs = event.timestamp
        )
    }
}
```

## 取得できる値

| index | 内容 | 単位 |
|-------|------|------|
| 0 | X 軸加速度（重力除去） | m/s² |
| 1 | Y 軸加速度（重力除去） | m/s² |
| 2 | Z 軸加速度（重力除去） | m/s² |

静止時は `≈ [0, 0, 0]`

## AccelerometerCollector との違い

| | AccelerometerCollector | LinearAccelerationCollector |
|---|---|---|
| 値 | 加速度 + 重力 | 加速度のみ（重力除去） |
| 静止時 | `[0, 0, 9.81]` など | `[0, 0, 0]` |

## 完了条件

- `LinearAccelerationCollector.kt` が作成されている
- `BaseSensorCollector` を継承している
- ビルドが通ること
