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

## フェーズ2：新センサ追加（完了）

| # | issue | 状態 |
|---|-------|------|
| [017](done/017-add-sensor-type-constants.md) | `SensorData` に新センサの TYPE 定数を追加 | ✅ 完了 |
| [018](done/018-implement-gyroscope-collector.md) | `GyroscopeCollector` の実装 | ✅ 完了 |
| [019](done/019-implement-magnetic-field-collector.md) | `MagneticFieldCollector` の実装 | ✅ 完了 |
| [020](done/020-implement-rotation-vector-collector.md) | `RotationVectorCollector` の実装 | ✅ 完了 |
| [021](done/021-implement-step-counter-collector.md) | `StepCounterCollector` の実装 | ✅ 完了 |
| [022](done/022-implement-step-detector-collector.md) | `StepDetectorCollector` の実装 | ✅ 完了 |
| [023](done/023-implement-gravity-collector.md) | `GravityCollector` の実装 | ✅ 完了 |
| [024](done/024-implement-linear-acceleration-collector.md) | `LinearAccelerationCollector` の実装 | ✅ 完了 |
| [025](done/025-implement-pressure-collector.md) | `PressureCollector` の実装 | ✅ 完了 |
| [026](done/026-implement-heart-beat-collector.md) | `HeartBeatCollector` の実装（RRI/HRV 用） | ✅ 完了 |
| [027](done/027-implement-oxygen-saturation-collector.md) | `OxygenSaturationCollector` の実装（SpO2） | ✅ 完了 |
| [028](done/028-implement-skin-temperature-collector.md) | `SkinTemperatureCollector` の実装（Pixel Watch 2 固有） | ✅ 完了 |
| [029](done/029-implement-off-body-detect-collector.md) | `OffBodyDetectCollector` の実装 | ✅ 完了 |
| [030](done/030-update-usage-docs-new-sensors.md) | `USAGE.md` に新センサの使い方を追記 | ✅ 完了 |

---

## フェーズ3：アーキテクチャ刷新（完了）

### 設計方針

- `SensorPipelineConfig` を廃止し `SensorPipelineFactory` に一本化
- `SensorConsumer` インターフェースで Store / Sender / ML 推論を統一
- `SyncJob` で Store-and-Forward パターンを実現
- `SensorDataStore` に ID ベースの部分削除を追加
- `SQLiteStore` を新規追加（store-and-forward 向け）

### タスク一覧

| # | issue | 状態 |
|---|-------|------|
| [031](done/031-define-sensor-consumer-interface.md) | `SensorConsumer` インターフェースの定義 | ✅ 完了 |
| [035](done/035-update-sensor-data-store-interface.md) | `SensorDataStore` インターフェースの更新 | ✅ 完了 |
| [032](done/032-implement-store-consumer.md) | `StoreConsumer` の実装 | ✅ 完了 |
| [033](done/033-implement-sender-consumer.md) | `SenderConsumer` の実装 | ✅ 完了 |
| [036](done/036-update-local-file-store.md) | `LocalFileStore` の更新 | ✅ 完了 |
| [037](done/037-implement-sqlite-store.md) | `SQLiteStore` の新規実装 | ✅ 完了 |
| [034](done/034-refactor-sensor-pipeline.md) | `SensorPipeline` のリファクタリング・`SensorPipelineConfig` 削除 | ✅ 完了 |
| [038](done/038-implement-sync-job.md) | `SyncJob` の実装 | ✅ 完了 |
| [039](done/039-implement-sensor-pipeline-factory.md) | `SensorPipelineFactory` の実装 | ✅ 完了 |
| [040](done/040-update-usage-docs-new-architecture.md) | `USAGE.md` を新アーキテクチャに合わせて更新 | ✅ 完了 |

全 10 件完了。フェーズ3（アーキテクチャ刷新）は完結。
