package com.softnet.blecompose

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.rememberCoroutineScope
import com.example.blecomposestudy.compose.MainScreen
import com.softnet.blecompose.domain.dto.ConnectionState
import com.softnet.blecompose.domain.impl.BluetoothLeServiceImpl
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : ComponentActivity() {
    private var connectServiceImpl : BluetoothLeServiceImpl? = null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e("MainActivity", "onServiceConnected: ")
            val binder = service as BluetoothLeServiceImpl.LocalBinder
            binder.getService().let { bindService -> connectServiceImpl = bindService }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e("MainActivity", "onServiceDisconnected: ")
            connectServiceImpl = null
        }
    }


    @OptIn(FlowPreview::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scope = rememberCoroutineScope()

            MainScreen(
                bindService = {
                    val gattServiceIntent = Intent(this, BluetoothLeServiceImpl::class.java)
                    bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE)
                },
                connect = {
                    connectServiceImpl?.let { service ->
                        scope.launch {
                            service.connect("D3:72:6C:76:16:63")
                                .filter { it == ConnectionState.Connected }
                                .flatMapConcat {
                                    service.discoverServices()
                                }
                                .flatMapConcat {
                                    service.characteristicNotification(
                                        UUID.fromString(NORDIC_UART_SERVICE),
                                        UUID.fromString(TX_CHARACTERISTIC),
                                        UUID.fromString(TX_CHARACTERISTIC_DESCRIPTOR),
                                        true
                                    )
                                }
                                .collect {
                                    Log.e("MainActivity", "onCreate: ${it.javaClass.simpleName}")
                                }
                        }
                    }
                },
                disconnect = {
                    connectServiceImpl?.disconnect()
                },
                write = {
                    scope.launch {
                        connectServiceImpl?.writeCharacteristic(
                            UUID.fromString(NORDIC_UART_SERVICE),
                            UUID.fromString(RX_CHARACTERISTIC),
                            "gdal".toByteArray()
                        )?.collect {
                            Log.e("MainActivity", "onCreate: ${it.contentToString()}")
                        }
                    }
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

    companion object {
        const val NORDIC_UART_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"
        const val RX_CHARACTERISTIC = "6e400002-b5a3-f393-e0a9-e50e24dcca9e" // 쓰기용 특성
        const val TX_CHARACTERISTIC = "6e400003-b5a3-f393-e0a9-e50e24dcca9e" // 알림용 특성 (읽기)
        const val TX_CHARACTERISTIC_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb"
    }
}
