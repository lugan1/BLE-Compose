package com.example.blecomposestudy.domain

import android.bluetooth.BluetoothDevice
import com.softnet.blecompose.domain.dto.ConnectionState
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface BluetoothLeService {
    fun connect(macAddress: String): Flow<ConnectionState>
    fun connect(device: BluetoothDevice): Flow<ConnectionState>
    fun discoverServices(): Flow<Unit>
    fun onConnectionStateChange(): Flow<ConnectionState>
    fun disconnect()

    fun characteristicNotification(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        descriptorUUID: UUID,
        value: Boolean
    ): Flow<Unit>

    fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        data: ByteArray
    ): Flow<ByteArray>
}
