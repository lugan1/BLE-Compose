package com.softnet.blecompose

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
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
import com.softnet.blecompose.bluetooth.dto.ConnectionState
import com.softnet.blecompose.bluetooth.specification.DescriptorUUID
import com.softnet.blecompose.bluetooth.specification.ServiceUUID
import com.softnet.blecompose.bluetooth.specification.characteristic.CharacteristicUUID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var connectServiceImpl : BLEBoundService? = null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e("MainActivity", "onServiceConnected: ")
            val binder = service as BLEBoundService.LocalBinder
            binder.getService().let { bindService -> connectServiceImpl = bindService }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e("MainActivity", "onServiceDisconnected: ")
            connectServiceImpl = null
        }
    }

    private var rxCharacteristic: BluetoothGattCharacteristic? = null


    @OptIn(FlowPreview::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scope = rememberCoroutineScope()

            MainScreen(
                bindService = {
                    val gattServiceIntent = Intent(this, BLEBoundService::class.java)
                    bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE)
                },
                connect = {
                    connectServiceImpl?.let { service ->
                        scope.launch {
                            service.connect("D3:72:6C:76:16:63")
                                .filter { it == ConnectionState.Connected }
                                .flatMapConcat { service.discoverServices() }
                                .flatMapConcat { services -> setupNotificationForUartService(services) }
                                .collect {
                                    Log.e("MainActivity", "onCreate: $it")
                                }
                        }
                    }
                },
                disconnect = {
                    connectServiceImpl?.disconnect()
                },
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
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

    private suspend fun setupNotificationForUartService(services: List<BluetoothGattService>): Flow<Unit> {
        val nordicService = services.find { it.uuid.toString() == ServiceUUID.NORDIC_UART_SERVICE.uuid }
        return nordicService?.let { service ->
            rxCharacteristic = service.characteristics.find { ct -> ct.uuid.toString() == CharacteristicUUID.RX_CHARACTERISTIC.uuid }
            val txCharacteristic = service.characteristics.find { ct -> ct.uuid.toString() == CharacteristicUUID.TX_CHARACTERISTIC.uuid }
            val descriptor = txCharacteristic?.descriptors?.find { dst -> dst.uuid.toString() == DescriptorUUID.CLIENT_CHARACTERISTIC_CONFIGURATION.uuid }
            descriptor?.let { connectServiceImpl?.characteristicNotification(txCharacteristic, it, true) }
        } ?: flow { emit(Unit) }
    }
}
