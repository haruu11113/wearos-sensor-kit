# #004 AccelerometerCollector の実装

## 概要
加速度センサー用の Collector を `BaseSensorCollector` を継承して実装する。
現在の `AccelerometerSensorService` の `formatMessage()` ロジックをここに移植する。

## やること
- `sensing/AccelerometerCollector.kt` を新規作成する
- `sensorType = Sensor.TYPE_ACCELEROMETER`
- `samplingRate = SensorManager.SENSOR_DELAY_GAME`
- `formatData()` で `SensorData(type = TYPE_ACCELEROMETER, values = event.values.clone(), timestampNs = event.timestamp)` を返す

## 完了条件
- `AccelerometerSensorService` に存在した `formatMessage()` のロジックが `formatData()` に移植されている
- `SensorData.type` が `"accelerometer"` になっている

## 依存
- #003 BaseSensorCollector 抽象クラスの実装
