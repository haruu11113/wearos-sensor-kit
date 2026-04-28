# 020 RotationVectorCollector の実装

## 概要

回転ベクトルセンサのコレクターを実装する。
加速度・ジャイロ・地磁気をフュージョンしたソフトウェアセンサで、デバイスの絶対姿勢をクォータニオンで取得できる。

## 前提

- 017 完了済みであること（`SensorData.TYPE_ROTATION_VECTOR` が存在する）

## 対象ファイル

新規作成: `sensing/src/main/java/com/example/wearos/sensing/RotationVectorCollector.kt`

## 実装内容

```kotlin
package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class RotationVectorCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_ROTATION_VECTOR
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_GAME

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_ROTATION_VECTOR,
            values = event.values.clone(),  // [x*sin(θ/2), y*sin(θ/2), z*sin(θ/2), cos(θ/2)]
            timestampNs = event.timestamp
        )
    }
}
```

## 取得できる値

| index | 内容 |
|-------|------|
| 0 | x * sin(θ/2) |
| 1 | y * sin(θ/2) |
| 2 | z * sin(θ/2) |
| 3 | cos(θ/2) ※省略される場合あり |

`SensorManager.getRotationMatrixFromVector()` や `getOrientation()` で方位角・ピッチ・ロールに変換可能。

## 完了条件

- `RotationVectorCollector.kt` が作成されている
- `BaseSensorCollector` を継承している
- ビルドが通ること
