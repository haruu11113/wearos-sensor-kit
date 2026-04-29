# 028 SkinTemperatureCollector の実装

## 概要

皮膚温度センサのコレクターを実装する。
Pixel Watch 2 固有のセンサで、手首の皮膚表面温度を取得できる。
発熱検知・睡眠モニタリングなどに使用できる。

## 前提

- 017 完了済みであること（`SensorData.TYPE_SKIN_TEMPERATURE` が存在する）

## 対象ファイル

新規作成: `sensing/src/main/java/com/example/wearos/sensing/SkinTemperatureCollector.kt`

## 必要なパーミッション

`BODY_SENSORS`（すでに `AndroidManifest.xml` に記載されている前提）

## 実装内容

```kotlin
package com.example.wearos.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

class SkinTemperatureCollector(
    context: Context
) : BaseSensorCollector(context) {

    // TYPE_SKIN_TEMPERATURE = 65572 番台の非公開定数（デバイス依存）
    // android.hardware.Sensor.TYPE_SKIN_TEMPERATURE が定義されていれば使用する
    // Pixel Watch 2 では 65536 + 55 = 65591 が該当することが多い（要実機確認）
    override val sensorType: Int = Sensor.TYPE_AMBIENT_TEMPERATURE  // フォールバック。実機確認後に修正すること
    override val samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL

    override fun formatData(event: SensorEvent): SensorData {
        return SensorData(
            type = SensorData.TYPE_SKIN_TEMPERATURE,
            values = event.values.clone(),  // [温度] ℃
            timestampNs = event.timestamp
        )
    }
}
```

## 取得できる値

| index | 内容 | 単位 |
|-------|------|------|
| 0 | 皮膚表面温度 | ℃ |

## ⚠️ 重要な注意事項

- `TYPE_SKIN_TEMPERATURE` のセンサ定数値は **Pixel Watch 2 の実機で確認**が必要
- `sensorManager.getSensorList(Sensor.TYPE_ALL)` で全センサを列挙し、`sensor.name` に "skin" や "temperature" を含むものを特定すること
- デバイスにセンサが存在しない場合、`BaseSensorCollector.start()` は黙ってスキップする（既存の安全設計）
- `BODY_SENSORS` パーミッションが必要

## 完了条件

- `SkinTemperatureCollector.kt` が作成されている
- `BaseSensorCollector` を継承している
- ビルドが通ること
- 実機でセンサ定数値を確認し、コメントに記録すること
