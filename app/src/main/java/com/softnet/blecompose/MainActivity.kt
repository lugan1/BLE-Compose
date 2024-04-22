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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.blecomposestudy.compose.MainScreen
import com.softnet.blecompose.bluetooth.dto.ConnectionState
import com.softnet.blecompose.bluetooth.impl.BluetoothLeServiceImpl
import com.softnet.blecompose.bluetooth.specification.DescriptorUUID
import com.softnet.blecompose.bluetooth.specification.ServiceUUID
import com.softnet.blecompose.bluetooth.specification.characteristic.CharacteristicUUID
import com.softnet.blecompose.compose.ScanScreen
import com.softnet.blecompose.service.BLEBoundService
import com.softnet.blecompose.service.BLEScanService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var bleScanServiceImpl : BLEScanService? = null
    private val bleConnector: BluetoothLeServiceImpl? = null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BLEScanService.LocalBinder
            binder.getService().let { bindService -> bleScanServiceImpl = bindService }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bleScanServiceImpl = null
        }
    }

    private var rxCharacteristic: BluetoothGattCharacteristic? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "scan"
            ) {
                composable("scan") {
                    val scope = rememberCoroutineScope()
                    ScanScreen(
                        bindService = {
                            val gattServiceIntent = Intent(this@MainActivity, BLEScanService::class.java)
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
                        },
                        moveToMainScreen = {
                            navController.navigate("main")
                        }
                    )
                }

                composable("main") {
                    val scope = rememberCoroutineScope()
                    MainScreen(
                        bindService = {
                            val gattServiceIntent = Intent(this@MainActivity, BLEBoundService::class.java)
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
                        disconnect = { bleConnector?.disconnect() },
                        write = {
                            rxCharacteristic?.let { characteristic ->
                                scope.launch {
                                    bleConnector?.writeCharacteristic(characteristic, "gdal".toByteArray())
                                        ?.collect {
                                            Log.e("MainActivity", "glal: ${it.contentToString()}")
                                        }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

    @OptIn(FlowPreview::class)
    fun connectFlow(): Flow<Unit>? {
        return bleConnector?.let { service ->
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
            descriptor?.let { bleConnector?.characteristicNotification(txCharacteristic, it, true) }
        } ?: flow { emit(Unit) }
    }
}
