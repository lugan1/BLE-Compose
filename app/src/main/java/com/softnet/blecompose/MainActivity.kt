package com.softnet.blecompose

import android.bluetooth.BluetoothGattCharacteristic
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.rememberCoroutineScope
import com.softnet.blecompose.compose.ScanScreen
import com.softnet.blecompose.service.BLEScanService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var bleScanServiceImpl : BLEScanService? = null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e("MainActivity", "onServiceConnected: ")
            val binder = service as BLEScanService.LocalBinder
            binder.getService().let { bindService -> bleScanServiceImpl = bindService }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e("MainActivity", "onServiceDisconnected: ")
            bleScanServiceImpl = null
        }
    }

    private var rxCharacteristic: BluetoothGattCharacteristic? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scope = rememberCoroutineScope()
            ScanScreen(
                bindService = {
                    val gattServiceIntent = Intent(this, BLEScanService::class.java)
                    bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE)
                },
                onScan = {
                         scope.launch {
                                bleScanServiceImpl?.startScan()?.collect { result ->
                                    Log.e("MainActivity", "result: $result")
                                }
                         }
                },
                onStop = {
                    bleScanServiceImpl?.stopScan()
                }
            )

/*            MainScreen(
                bindService = {
                    val gattServiceIntent = Intent(this, BLEBoundService::class.java)
                    bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE)
                },
                connect = {
                          connectFlow()?.let {
                                scope.launch {
                                    it.collect { state ->
                                        Log.e("MainActivity", "state: $state")
                                    }
                                }
                          }
                },
                disconnect = { connectServiceImpl?.disconnect() },
                write = {
                    rxCharacteristic?.let { characteristic ->
                        scope.launch {
                            connectServiceImpl?.writeCharacteristic(characteristic, "gdal".toByteArray())
                                ?.collect {
                                    Log.e("MainActivity", "glal: ${it.contentToString()}")
                                }
                        }
                    }
                }
            )*/
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

/*    @OptIn(FlowPreview::class)
    fun connectFlow(): Flow<Unit>? {
        return bleScanServiceImpl?.let { service ->
            service.connect("D3:72:6C:76:16:63")
                .filter { it == ConnectionState.Connected }
                .flatMapConcat { service.discoverServices() }
                .flatMapConcat { services -> setupNotificationForUartService(services) }
        }
    }

    private suspend fun setupNotificationForUartService(services: List<BluetoothGattService>): Flow<Unit> {
        val nordicService = services.find { it.uuid.toString() == ServiceUUID.NORDIC_UART_SERVICE.uuid }
        return nordicService?.let { service ->
            rxCharacteristic = service.characteristics.find { ct -> ct.uuid.toString() == CharacteristicUUID.RX_CHARACTERISTIC.uuid }
            val txCharacteristic = service.characteristics.find { ct -> ct.uuid.toString() == CharacteristicUUID.TX_CHARACTERISTIC.uuid }
            val descriptor = txCharacteristic?.descriptors?.find { dst -> dst.uuid.toString() == DescriptorUUID.CLIENT_CHARACTERISTIC_CONFIGURATION.uuid }
            descriptor?.let { bleScanServiceImpl?.characteristicNotification(txCharacteristic, it, true) }
        } ?: flow { emit(Unit) }
    }*/
}
