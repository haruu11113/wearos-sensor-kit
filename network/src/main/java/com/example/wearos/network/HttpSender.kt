package com.example.wearos.network

import android.util.Log
import java.net.HttpURLConnection
import java.net.URL

class HttpSender(private val url: String) : DataSender {

    override fun send(payload: String) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                connectTimeout = 5000
                readTimeout = 5000
                doOutput = true
            }
            connection.outputStream.use { it.write(payload.toByteArray(Charsets.UTF_8)) }

            val code = connection.responseCode
            if (code !in 200..299) {
                Log.w("HttpSender", "Unexpected response code $code for POST $url")
            }
            connection.disconnect()
        } catch (e: Exception) {
            Log.e("HttpSender", "Failed to POST to $url: $e")
        }
    }
}
