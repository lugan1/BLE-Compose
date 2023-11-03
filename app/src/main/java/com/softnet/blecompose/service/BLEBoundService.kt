package com.softnet.blecompose.service

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
class BLEBoundService: Service(), BluetoothLeService {
    @Inject lateinit var bluetoothLE : BluetoothLeService

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): BLEBoundService = this@BLEBoundService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
        disconnect()
    }

    override fun connect(address: String): Flow<ConnectionState> = bluetoothLE.connect(address)

    override fun connect(device: BluetoothDevice): Flow<ConnectionState> = bluetoothLE.connect(device)

    override fun discoverServices(): Flow<List<BluetoothGattService>> = bluetoothLE.discoverServices()

    override fun onConnectionStateChange(): Flow<ConnectionState> = bluetoothLE.onConnectionStateChange()

    override fun disconnect() = bluetoothLE.disconnect()

    override fun characteristicNotification(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        descriptorUUID: UUID,
        value: Boolean
    ): Flow<Unit> = bluetoothLE.characteristicNotification(serviceUUID, characteristicUUID, descriptorUUID, value)

    override fun characteristicNotification(
        characteristic: BluetoothGattCharacteristic,
        descriptor: BluetoothGattDescriptor,
        value: Boolean
    ): Flow<Unit> = bluetoothLE.characteristicNotification(characteristic, descriptor, value)


    override fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        data: ByteArray
    ): Flow<ByteArray> = bluetoothLE.writeCharacteristic(serviceUUID, characteristicUUID, data)

    override fun writeCharacteristic(
        characteristic: BluetoothGattCharacteristic,
        data: ByteArray
    ): Flow<ByteArray> = bluetoothLE.writeCharacteristic(characteristic, data)

}
