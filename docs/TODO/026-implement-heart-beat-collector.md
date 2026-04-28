# 026 HeartBeatCollector の実装

## 概要

心拍ビート検出センサのコレクターを実装する。
1拍ごとにイベントが発火するため、RRI（R-R間隔）の計算や HRV（心拍変動）解析に使用できる。

## 前提

- 017 完了済みであること（`SensorData.TYPE_HEART_BEAT` が存在する）

## 対象ファイル

新規作成: `sensing/src/main/java/com/example/wearos/sensing/HeartBeatCollector.kt`

## 必要なパーミッション

`BODY_SENSORS`（すでに `AndroidManifest.xml` に記載されている前提）

## 実装内容

```kotlin
package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class HeartBeatCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_HEART_BEAT
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_HEART_BEAT,
            values = event.values.clone(),  // [confidence] 0.0〜1.0
            timestampNs = event.timestamp
        )
    }
}
```

## 取得できる値

| index | 内容 | 範囲 |
|-------|------|------|
| 0 | ピーク検出の信頼度 | 0.0 〜 1.0 |

- `timestamp` の差分が **RRI（ms）**
- RRI から HRV（SDNN, RMSSD など）の計算が可能

## 注意事項

- `BODY_SENSORS` パーミッションが実行時に付与されていない場合、`start()` 時にセンサが `null` になり黙ってスキップされる（`BaseSensorCollector` の挙動）

## 完了条件

- `HeartBeatCollector.kt` が作成されている
- `BaseSensorCollector` を継承している
- ビルドが通ること
