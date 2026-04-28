# TODO / Issue 一覧

## ステータス

| # | issue | 状態 |
|---|-------|------|
| [001](done/001-define-sensor-data.md) | `SensorData` データクラスの定義 | ✅ 完了 |
| [002](done/002-define-sensor-collector-listener.md) | `SensorCollectorListener` インターフェースの定義 | ✅ 完了 |
| [003](done/003-implement-base-sensor-collector.md) | `BaseSensorCollector` 抽象クラスの実装 | ✅ 完了 |
| [004](done/004-implement-accelerometer-collector.md) | `AccelerometerCollector` の実装 | ✅ 完了 |
| [005](done/005-implement-heartrate-collector.md) | `HeartRateCollector` の実装 | ✅ 完了 |
| [006](done/006-implement-light-collector.md) | `LightCollector` の実装 | ✅ 完了 |
| [007](done/007-define-sensor-data-serializer.md) | `SensorDataSerializer` インターフェースの定義 | ✅ 完了 |
| [008](done/008-implement-json-serializer.md) | `JsonSerializer` の実装 | ✅ 完了 |
| [009](done/009-define-sensor-data-store.md) | `SensorDataStore` インターフェースの定義 | ✅ 完了 |
| [010](done/010-implement-local-file-store.md) | `LocalFileStore` の実装 | ✅ 完了 |
| [011](done/011-define-data-sender-interface.md) | `DataSender` インターフェースの定義 | ✅ 完了 |
| [012](done/012-refactor-udp-sender.md) | `UdpSender` を `DataSender` に合わせてリファクタリング | ✅ 完了 |
| [013](done/013-define-sensor-pipeline-config.md) | `SensorPipelineConfig` の定義 | ✅ 完了 |
| [014](done/014-implement-sensor-pipeline.md) | `SensorPipeline` の実装 | ✅ 完了 |
| [015](done/015-replace-base-sensor-service.md) | `BaseSensorService` を `SensorPipeline` ベースに置き換え | ✅ 完了 |
| [016](done/016-gradle-multi-module.md) | Gradle マルチモジュール構成への移行 | ✅ 完了 |

全 16 件完了。初期ライブラリ化対応は完結。

---

## フェーズ2：新センサ追加

| # | issue | 状態 |
|---|-------|------|
| [017](017-add-sensor-type-constants.md) | `SensorData` に新センサの TYPE 定数を追加（**先行必須**） | 🔲 未着手 |
| [018](018-implement-gyroscope-collector.md) | `GyroscopeCollector` の実装 | 🔲 未着手 |
| [019](019-implement-magnetic-field-collector.md) | `MagneticFieldCollector` の実装 | 🔲 未着手 |
| [020](020-implement-rotation-vector-collector.md) | `RotationVectorCollector` の実装 | 🔲 未着手 |
| [021](021-implement-step-counter-collector.md) | `StepCounterCollector` の実装 | 🔲 未着手 |
| [022](022-implement-step-detector-collector.md) | `StepDetectorCollector` の実装 | 🔲 未着手 |
| [023](023-implement-gravity-collector.md) | `GravityCollector` の実装 | 🔲 未着手 |
| [024](024-implement-linear-acceleration-collector.md) | `LinearAccelerationCollector` の実装 | 🔲 未着手 |
| [025](025-implement-pressure-collector.md) | `PressureCollector` の実装 | 🔲 未着手 |
| [026](026-implement-heart-beat-collector.md) | `HeartBeatCollector` の実装（RRI/HRV 用） | 🔲 未着手 |
| [027](027-implement-oxygen-saturation-collector.md) | `OxygenSaturationCollector` の実装（SpO2） | 🔲 未着手 |
| [028](028-implement-skin-temperature-collector.md) | `SkinTemperatureCollector` の実装（Pixel Watch 2 固有） | 🔲 未着手 |
| [029](029-implement-off-body-detect-collector.md) | `OffBodyDetectCollector` の実装 | 🔲 未着手 |
| [030](030-update-usage-docs-new-sensors.md) | `USAGE.md` に新センサの使い方を追記（**018〜029 完了後**） | 🔲 未着手 |

### 並列作業の進め方

1. **017 を単独で先に完了させる**（他全てが `SensorData` の定数に依存するため）
2. **018〜029 は完全並列**（各自が新規ファイル1つを作成するだけ。コンフリクトなし）
3. **030 は 018〜029 完了後**にまとめてドキュメント更新
