# 027 OxygenSaturationCollector の実装

## 概要

血中酸素飽和度（SpO2）センサのコレクターを実装する。
Pixel Watch 2 はハードウェア対応済み。

## 前提

- 017 完了済みであること（`SensorData.TYPE_OXYGEN_SATURATION` が存在する）

## 対象ファイル

新規作成: `sensing/src/main/java/com/example/wearos/sensing/OxygenSaturationCollector.kt`

## 必要なパーミッション

`BODY_SENSORS`（すでに `AndroidManifest.xml` に記載されている前提）

## 実装内容

```kotlin
package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class OxygenSaturationCollector(
    context: Context
) : BaseSensorCollector(context) {

    // TYPE_OXYGEN_SATURATION = 65572 (android.hardware.Sensor の非公開定数)
    // Android 14 以降は Sensor.TYPE_OXYGEN_SATURATION が利用可能
    override val sensorType: Int = 65572
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_OXYGEN_SATURATION,
            values = event.values.clone(),  // [SpO2 %]
            timestampNs = event.timestamp
        )
    }
}
```

## 取得できる値

| index | 内容 | 単位 | 正常範囲 |
|-------|------|------|---------|
| 0 | 血中酸素飽和度 | % | 95〜100% |

## 注意事項

- Android 14 以降で `Sensor.TYPE_OXYGEN_SATURATION` 定数が利用可能になった場合はそちらに切り替える
- Wear OS はバックグラウンドでの連続計測を制限する場合がある（バッテリー保護のため）
- `BODY_SENSORS` パーミッションが必要

## 完了条件

- `OxygenSaturationCollector.kt` が作成されている
- `BaseSensorCollector` を継承している
- ビルドが通ること
