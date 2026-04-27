# #008 JsonSerializer の実装

## 概要
`SensorDataSerializer` を実装し、`SensorData` を JSON 文字列に変換するクラスを作る。
外部ライブラリは使わず `org.json.JSONObject`（Android 標準）で実装する。

## やること
- `storage/JsonSerializer.kt` を新規作成する
- 出力 JSON フォーマット:

```json
{
  "type": "accelerometer",
  "values": [0.12, -9.80, 0.05],
  "timestamp_ns": 123456789
}
```

- `values` は JSONArray に変換する
- `timestamp_ns` は Long をそのまま格納する

## 完了条件
- `JsonSerializer` が `SensorDataSerializer` を実装している
- 出力 JSON が上記フォーマットに一致する
- `org.json` 以外の JSON ライブラリに依存していない

## 依存
- #007 SensorDataSerializer インターフェースの定義
