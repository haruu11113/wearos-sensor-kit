# 022 StepDetectorCollector の実装

## 概要

歩行検出センサのコレクターを実装する。
1歩を踏むたびにイベントが発火する（累積ではなくイベント型）。
リアルタイムな歩行検出・歩行ペース計測に使用する。

## 前提

- 017 完了済みであること（`SensorData.TYPE_STEP_DETECTOR` が存在する）

## 対象ファイル

新規作成: `sensing/src/main/java/com/example/wearos/sensing/StepDetectorCollector.kt`

## 実装内容

```kotlin
package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class StepDetectorCollector(
    context: Context
) : BaseSensorCollector(context) {

    override val sensorType: Int = Sensor.TYPE_STEP_DETECTOR
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_STEP_DETECTOR,
            values = event.values.clone(),  // [1.0] 常に 1.0（歩行検出イベント）
            timestampNs = event.timestamp
        )
    }
}
```

## 取得できる値

| index | 内容 |
|-------|------|
| 0 | 常に `1.0`（1歩検出のトリガー） |

## StepCounter との違い

| | StepCounter | StepDetector |
|---|---|---|
| 値 | 累積歩数 | 常に 1.0 |
| 用途 | 総歩数の取得 | リアルタイム歩行検出 |
| リセット | デバイス再起動時 | なし（イベントのみ） |

## 完了条件

- `StepDetectorCollector.kt` が作成されている
- `BaseSensorCollector` を継承している
- ビルドが通ること
