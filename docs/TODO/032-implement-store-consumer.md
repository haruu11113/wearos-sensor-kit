# 032 StoreConsumer の実装

## 概要

受け取った SensorData を SensorDataStore に保存する Consumer を実装する。

## 前提

- 031 完了済みであること（`SensorConsumer` が存在する）
- 035 完了済みであること（`SensorDataStore` のインターフェースが更新されている）

## 対象ファイル

新規作成: `pipeline/src/main/java/com/example/wearos/pipeline/StoreConsumer.kt`

## 実装内容

```kotlin
package com.example.wearos.pipeline

import com.example.wearos.sensing.SensorData
import com.example.wearos.storage.SensorDataStore

class StoreConsumer(
    private val store: SensorDataStore,
    private val serializer: SensorDataSerializer = JsonSerializer()
) : SensorConsumer {

    override fun onData(data: SensorData) {
        val serialized = serializer.serialize(data)
        store.save(serialized)
    }
}
```

## 完了条件

- `StoreConsumer.kt` が作成されている
- `SensorConsumer` を実装している
- ビルドが通ること
