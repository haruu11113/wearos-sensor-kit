# 019 MagneticFieldCollector の実装

## 概要

地磁気センサのコレクターを実装する。
方位角の算出・コンパス機能に使用する。

## 前提

- 017 完了済みであること（`SensorData.TYPE_MAGNETIC_FIELD` が存在する）

## 対象ファイル

新規作成: `sensing/src/main/java/com/example/wearos/sensing/MagneticFieldCollector.kt`

## 実装内容

```kotlin
package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class MagneticFieldCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_MAGNETIC_FIELD
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_MAGNETIC_FIELD,
            values = event.values.clone(),  // [x, y, z] μT
            timestampNs = event.timestamp
        )
    }
}
```

## 取得できる値

| index | 内容 | 単位 |
|-------|------|------|
| 0 | X 軸磁束密度 | μT |
| 1 | Y 軸磁束密度 | μT |
| 2 | Z 軸磁束密度 | μT |

## 完了条件

- `MagneticFieldCollector.kt` が作成されている
- `BaseSensorCollector` を継承している
- ビルドが通ること
