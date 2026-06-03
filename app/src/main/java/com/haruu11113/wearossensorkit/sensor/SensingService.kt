package com.haruu11113.wearossensorkit.sensor

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.haruu11113.wearossensorkit.network.UdpSender
import com.haruu11113.wearossensorkit.pipeline.SenderConsumer
import com.haruu11113.wearossensorkit.pipeline.SensorPipeline
import com.haruu11113.wearossensorkit.sensing.AccelerometerCollector
import com.haruu11113.wearossensorkit.sensing.GravityCollector
import com.haruu11113.wearossensorkit.sensing.GyroscopeCollector
import com.haruu11113.wearossensorkit.sensing.HeartBeatCollector
import com.haruu11113.wearossensorkit.sensing.HeartRateCollector
import com.haruu11113.wearossensorkit.sensing.LightCollector
import com.haruu11113.wearossensorkit.sensing.LinearAccelerationCollector
import com.haruu11113.wearossensorkit.sensing.MagneticFieldCollector
import com.haruu11113.wearossensorkit.sensing.OffBodyDetectCollector
import com.haruu11113.wearossensorkit.sensing.OxygenSaturationCollector
import com.haruu11113.wearossensorkit.sensing.PressureCollector
import com.haruu11113.wearossensorkit.sensing.RotationVectorCollector
import com.haruu11113.wearossensorkit.sensing.SkinTemperatureCollector
import com.haruu11113.wearossensorkit.sensing.StepCounterCollector
import com.haruu11113.wearossensorkit.sensing.StepDetectorCollector

class SensingService : Service() {

    private lateinit var pipeline: SensorPipeline

    companion object {
        const val EXTRA_UDP_ADDRESS = "udp_address"
        const val EXTRA_UDP_PORT = "udp_port"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val address = intent?.getStringExtra(EXTRA_UDP_ADDRESS)
        val port = intent?.getIntExtra(EXTRA_UDP_PORT, -1)

        val sender = if (address != null && port != null && port != -1) {
            UdpSender(address, port)
        } else {
            null
        }

        val collectors = listOf(
            AccelerometerCollector(this),
            GyroscopeCollector(this),
            MagneticFieldCollector(this),
            RotationVectorCollector(this),
            GravityCollector(this),
            LinearAccelerationCollector(this),
            StepCounterCollector(this),
            StepDetectorCollector(this),
            PressureCollector(this),
            HeartRateCollector(this),
            HeartBeatCollector(this),
            OxygenSaturationCollector(this),
            SkinTemperatureCollector(this),
            LightCollector(this),
            OffBodyDetectCollector(this)
        )
        val consumers = listOfNotNull(
            sender?.let { SenderConsumer(it) }
        )
        pipeline = SensorPipeline(collectors, consumers)
        pipeline.start()
        return START_STICKY
    }

    override fun onDestroy() {
        pipeline.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
