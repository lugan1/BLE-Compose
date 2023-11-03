package com.softnet.blecompose

import android.app.Service
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import com.softnet.blecompose.bluetooth.BluetoothLeService
import com.softnet.blecompose.bluetooth.dto.ConnectionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class TestServiceImpl: Service(), BluetoothLeService {
    @Inject lateinit var bluetoothLE : BluetoothLeService
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): TestServiceImpl {
            return this@TestServiceImpl
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
        disconnect()
    }

    override fun connect(address: String): Flow<ConnectionState> {
        TODO()
    }

    override fun connect(device: BluetoothDevice): Flow<ConnectionState> {
        TODO()
    }

    override fun discoverServices(): Flow<List<BluetoothGattService>> {
        TODO()
    }

    override fun onConnectionStateChange(): Flow<ConnectionState> {
        TODO()
    }

    override fun disconnect() {
        TODO()
    }

    override fun characteristicNotification(serviceUUID: UUID, characteristicUUID: UUID, descriptorUUID: UUID, value: Boolean): Flow<Unit> {
        TODO()
    }

    override fun characteristicNotification(
        characteristic: BluetoothGattCharacteristic,
        descriptor: BluetoothGattDescriptor,
        value: Boolean
    ): Flow<Unit> {
        TODO()
    }


    override fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        data: ByteArray
    ): Flow<ByteArray> {
        TODO()
    }

    override fun writeCharacteristic(
        characteristic: BluetoothGattCharacteristic,
        data: ByteArray
    ): Flow<ByteArray> {
        TODO()
    }
}
