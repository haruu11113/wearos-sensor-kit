# #010 LocalFileStore の実装

## 概要
`SensorDataStore` を実装し、シリアライズ済みデータをアプリ内ストレージのファイルに追記保存するクラスを作る。
現在 `BaseSensorService.saveToCSV()` に残っている未実装コードをここで完成させる。

## やること
- `storage/LocalFileStore.kt` を新規作成する
- コンストラクタで `Context` とファイル名（デフォルト `"sensor_data.jsonl"`）を受け取る
- `save(serialized)`: ファイルへ 1 行追記（JSONL 形式: 1レコード = 1行）
- `readAll()`: ファイルを行単位で読み込み `List<String>` を返す
- `clear()`: ファイルを削除または空にする
- ファイルの書き込みは `BufferedWriter` を都度 open/close する（高頻度書き込みを想定するなら将来的にバッファリングを検討）

## 完了条件
- `save()` を呼ぶとアプリ内ストレージにファイルが生成・追記される
- `readAll()` で保存した文字列をすべて取り出せる
- `clear()` でファイルが空になる
- `sensing` / `network` パッケージに依存していない

## 依存
- #009 SensorDataStore インターフェースの定義
