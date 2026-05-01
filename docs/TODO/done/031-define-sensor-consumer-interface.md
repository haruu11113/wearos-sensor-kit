# 031 SensorConsumer インターフェースの定義

## 概要

Pipeline からデータを受け取る統一インターフェース `SensorConsumer` を定義する。
StoreConsumer / SenderConsumer / ActivityRecognizer など、全ての Consumer がこれを実装する。

**このタスクを最初に完了させること。032・033・034 が依存する。**

## 対象ファイル

新規作成: `pipeline/src/main/java/com/example/wearos/pipeline/SensorConsumer.kt`

## 実装内容

```kotlin
package com.example.wearos.pipeline

import com.example.wearos.sensing.SensorData

interface SensorConsumer {
    fun onData(data: SensorData)
}
```

## 完了条件

- `SensorConsumer.kt` が作成されている
- `pipeline` モジュールに配置されている
- ビルドが通ること
