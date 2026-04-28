# 030 USAGE.md に新センサの使い方を追記

## 概要

018〜029 の実装完了後、`docs/USAGE.md` に新センサの使い方を追記する。
**このタスクは 018〜029 がすべて完了してから実施すること。**

## 対象ファイル

`docs/USAGE.md`

## 追記内容

### 追加するセクション（既存の「コード例」セクション内に追記）

#### 利用可能な Collector 一覧テーブル

```markdown
### 利用可能な Collector 一覧

| クラス | センサ | 取得できる値 | 必要なパーミッション |
|--------|--------|-------------|-------------------|
| `AccelerometerCollector` | 加速度センサ | X/Y/Z 加速度 (m/s²) | - |
| `GyroscopeCollector` | ジャイロスコープ | X/Y/Z 角速度 (rad/s) | - |
| `MagneticFieldCollector` | 地磁気センサ | X/Y/Z 磁束密度 (μT) | - |
| `RotationVectorCollector` | 回転ベクトル | クォータニオン | - |
| `GravityCollector` | 重力センサ | X/Y/Z 重力成分 (m/s²) | - |
| `LinearAccelerationCollector` | 線形加速度 | X/Y/Z 加速度（重力除去） | - |
| `StepCounterCollector` | 歩数カウンター | 累積歩数 | - |
| `StepDetectorCollector` | 歩行検出 | 1歩ごとにイベント | - |
| `PressureCollector` | 気圧センサ | 大気圧 (hPa) | - |
| `HeartRateCollector` | 心拍数 | bpm | `BODY_SENSORS` |
| `HeartBeatCollector` | 心拍ビート | 信頼度 (0〜1) ※RRI算出用 | `BODY_SENSORS` |
| `OxygenSaturationCollector` | SpO2 | 血中酸素飽和度 (%) | `BODY_SENSORS` |
| `SkinTemperatureCollector` | 皮膚温度 | 皮膚表面温度 (℃) ※Pixel Watch 2 | `BODY_SENSORS` |
| `LightCollector` | 照度センサ | 照度 (lux) | - |
| `OffBodyDetectCollector` | 装着検出 | 0=装着中 / 1=非装着 | - |
```

#### 全センサを使った例

```kotlin
pipeline = SensorPipeline(
    SensorPipelineConfig(
        collectors = listOf(
            AccelerometerCollector(this),
            GyroscopeCollector(this),
            MagneticFieldCollector(this),
            RotationVectorCollector(this),
            GravityCollector(this),
            LinearAccelerationCollector(this),
            StepCounterCollector(this),
            StepDetectorCollector(this),
            PressureCollector(this),
            HeartRateCollector(this),
            HeartBeatCollector(this),
            OxygenSaturationCollector(this),
            SkinTemperatureCollector(this),
            LightCollector(this),
            OffBodyDetectCollector(this)
        ),
        sender = UdpSender("192.168.1.100", 6666)
    )
)
```

## 完了条件

- USAGE.md に上記テーブルとコード例が追記されている
- 全クラス名・取得値・パーミッションが正確に記載されている
