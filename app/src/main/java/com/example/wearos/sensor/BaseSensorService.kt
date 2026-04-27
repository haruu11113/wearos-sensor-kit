package com.example.wearos.sensor

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.wearos.network.DataSender
import java.io.File
import java.io.FileWriter
import android.util.Log

import com.example.wearos.network.UdpSender

open class BaseSensorService : Service(), SensorEventListener {
    public var sender: DataSender = UdpSender("192.168.50.78", 6666)
    public var sensorManager: SensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    public var sensor: Sensor? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onSensorChanged(event: SensorEvent) {
        val csvString: String = this.formatMessage(event)
        Thread {
            this.sender.send(csvString)
        }.start()
        // saveToCSV(csvString)
    }

    open fun formatMessage(event: SensorEvent): String {
        var message: String = "${event.timestamp}, ${event.values.joinToString(",")}\n"
        return message
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // 精度が変更された場合の処理
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun saveToCSV(dataString: String) {
        val file = File(applicationContext.filesDir, "sensor_data.csv")
        val writer = FileWriter(file, true)
        writer.append()
        writer.flush()
        writer.close()
    }

    override fun onDestroy() {
        super.onDestroy()
//        sensorManager.unregisterListener(this)
    }
}
