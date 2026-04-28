package com.example.wearos.network

interface DataSender {
    fun send(payload: String)
    fun onStop() {}  // バッファを持つ実装がフラッシュ処理を行うためのライフサイクルフック
}
