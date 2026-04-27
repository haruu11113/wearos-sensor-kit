# #005 HeartRateCollector の実装

## 概要
心拍数センサー用の Collector を `BaseSensorCollector` を継承して実装する。
現在の `HeartRateSensorService` のロジックをここに移植する。

## やること
- `sensing/HeartRateCollector.kt` を新規作成する
- `sensorType = Sensor.TYPE_HEART_RATE`
- `samplingRate = SensorManager.SENSOR_DELAY_GAME`
- `formatData()` で `SensorData(type = TYPE_HEART_RATE, values = event.values.clone(), timestampNs = event.timestamp)` を返す
- `BODY_SENSORS` パーミッションが必要な旨を KDoc コメントで記載する

## 完了条件
- `HeartRateSensorService` に存在した `formatMessage()` のロジックが `formatData()` に移植されている
- `SensorData.type` が `"heart_rate"` になっている

## 依存
- #003 BaseSensorCollector 抽象クラスの実装
