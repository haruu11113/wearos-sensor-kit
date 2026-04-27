# #006 LightCollector の実装

## 概要
照度センサー用の Collector を `BaseSensorCollector` を継承して実装する。
現在の `LightSensorService` のロジックをここに移植する。

## やること
- `sensing/LightCollector.kt` を新規作成する
- `sensorType = Sensor.TYPE_LIGHT`
- `samplingRate = SensorManager.SENSOR_DELAY_NORMAL`
- `formatData()` で `SensorData(type = TYPE_LIGHT, values = event.values.clone(), timestampNs = event.timestamp)` を返す

## 完了条件
- `LightSensorService` に存在した `formatMessage()` のロジックが `formatData()` に移植されている
- `SensorData.type` が `"light"` になっている

## 依存
- #003 BaseSensorCollector 抽象クラスの実装
