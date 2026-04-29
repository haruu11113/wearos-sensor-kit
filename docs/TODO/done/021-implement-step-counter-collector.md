# 021 StepCounterCollector の実装

## 概要

歩数カウンターセンサのコレクターを実装する。
デバイス起動後からの**累積**歩数を返すハードウェアセンサ。
差分を取ることで区間の歩数を計算できる。

## 前提

- 017 完了済みであること（`SensorData.TYPE_STEP_COUNTER` が存在する）

## 対象ファイル

新規作成: `sensing/src/main/java/com/example/wearos/sensing/StepCounterCollector.kt`

## 実装内容

```kotlin
package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class StepCounterCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_STEP_COUNTER
    // STEP_COUNTER はイベント駆動のため SENSOR_DELAY_NORMAL で十分
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_STEP_COUNTER,
            values = event.values.clone(),  // [累積歩数]
            timestampNs = event.timestamp
        )
    }
}
```

## 取得できる値

| index | 内容 | 備考 |
|-------|------|------|
| 0 | 累積歩数（float） | デバイス再起動でリセット |

## 注意事項

- 値は**累積**なので、アプリ側で前回値との差分を計算すること
- `SENSOR_DELAY_*` の値はバッチ遅延のヒントにすぎず、実際の配信頻度はシステムが制御する

## 完了条件

- `StepCounterCollector.kt` が作成されている
- `BaseSensorCollector` を継承している
- ビルドが通ること
