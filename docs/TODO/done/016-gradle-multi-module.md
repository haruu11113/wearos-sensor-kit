# #016 Gradle マルチモジュール構成への移行（任意）

## 概要
`sensing` / `storage` / `network` / `pipeline` を独立した Android Library モジュール（`.aar`）として切り出す。
他のプロジェクトから Maven Local や Git Submodule 経由で依存できるようにする。

## やること

### モジュール分割
```
wearos/
├── sensing/          ← new Android Library module
├── storage/          ← new Android Library module
├── network/          ← new Android Library module
├── pipeline/         ← new Android Library module
└── app/              ← 既存サンプルアプリ (各モジュールに依存)
```

- `settings.gradle.kts` に各モジュールを追加:
  ```kotlin
  include(":sensing", ":storage", ":network", ":pipeline", ":app")
  ```
- 各モジュールの `build.gradle.kts` で `com.android.library` プラグインを設定
- `app/build.gradle.kts` で各モジュールを `implementation(project(":pipeline"))` 等で依存

### ローカル公開（任意）
- `./gradlew publishToMavenLocal` で `~/.m2` にインストール
- 利用側アプリで `mavenLocal()` リポジトリを追加すれば依存できる

## 完了条件
- `./gradlew :app:assembleDebug` がエラーなく通る
- `sensing` / `storage` / `network` が互いに依存していない
- `pipeline` モジュールだけ依存すれば全機能使えることを確認

## 備考
- #001〜#015 がすべて完了してから実施する
- 優先度は低い（コピペ運用でも十分）

## 依存
- #015 BaseSensorService の置き換え（全リファクタリング完了後）
