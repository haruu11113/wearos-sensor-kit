# 029 OffBodyDetectCollector の実装

## 概要

装着検出センサのコレクターを実装する。
ウォッチが手首に装着されているかどうかを検出する。
非装着時のセンシング停止・アラート通知などに使用できる。

## 前提

- 017 完了済みであること（`SensorData.TYPE_OFF_BODY_DETECT` が存在する）

## 対象ファイル

新規作成: `sensing/src/main/java/com/example/wearos/sensing/OffBodyDetectCollector.kt`

## 実装内容

```kotlin
package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class OffBodyDetectCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_OFF_BODY_DETECT,
            values = event.values.clone(),  // [0.0 = 装着中, 1.0 = 非装着]
            timestampNs = event.timestamp
        )
    }
}
```

## 取得できる値

| index | 値 | 意味 |
|-------|-----|------|
| 0 | `0.0` | 装着中（on body） |
| 0 | `1.0` | 非装着（off body） |

## 特徴

- `LOW_LATENCY` の名前通り、状態変化を低遅延で検出する
- 連続的にポーリングするのではなく、状態変化時のみイベントが来る

## 完了条件

- `OffBodyDetectCollector.kt` が作成されている
- `BaseSensorCollector` を継承している
- ビルドが通ること
