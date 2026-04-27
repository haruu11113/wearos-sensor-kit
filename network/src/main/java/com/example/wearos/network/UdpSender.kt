package com.example.wearos.network

import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

/**
 * @param address 送信先のIPアドレスまたはホスト名
 * @param port 送信先のポート番号
 *
 * UdpSenderは、指定されたアドレスとポートにUDPメッセージを送信するクラスです。
 * このクラスは、DataSenderインターフェースを実装しています。
 */
class UdpSender(private val address: String, private val port: Int) : DataSender {
    // この関数はバックグラウンドスレッドで呼び出す必要があります
    // 例: Coroutine, AsyncTask, Threadなどを使用してください
    // 以下は単純なスレッドでの使用例です
    override fun send(payload: String) {
        Log.i("UdpSender", "sendUDPMessage message: ${payload} on ${this.address}:${this.port}")
        try {
            val sendData: ByteArray = payload.toByteArray()
            val socket: DatagramSocket = DatagramSocket() // UDPソケットを開く
            val IPAddress = InetAddress.getByName(this.address)
            val socketAddress: InetSocketAddress = InetSocketAddress(IPAddress, this.port)
            val pack = DatagramPacket(sendData, sendData.size, socketAddress)
            socket.send(pack)
            socket.close()
            Log.i("UdpSender", "Done send")
        } catch (e: Exception) {
            Log.i("UdpSender", "sendUDPMessage error ${e}")
            e.printStackTrace()
        }
    }
}
