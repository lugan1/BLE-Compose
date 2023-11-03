package com.softnet.blecompose.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import com.softnet.blecompose.bluetooth.dto.ConnectionState
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface BluetoothLeService {
    fun connect(address: String): Flow<ConnectionState>
    fun connect(device: BluetoothDevice): Flow<ConnectionState>
    fun discoverServices(): Flow<List<BluetoothGattService>>
    fun onConnectionStateChange(): Flow<ConnectionState>
    fun disconnect()

    fun characteristicNotification(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        descriptorUUID: UUID,
        value: Boolean
    ): Flow<Unit>

    fun characteristicNotification(
        characteristic: BluetoothGattCharacteristic,
        descriptor: BluetoothGattDescriptor,
        value: Boolean
    ): Flow<Unit>

    fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        data: ByteArray
    ): Flow<ByteArray>

    fun writeCharacteristic(
        characteristic: BluetoothGattCharacteristic,
        data: ByteArray
    ): Flow<ByteArray>
}
