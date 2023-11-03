package com.softnet.blecompose.bluetooth.impl

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.os.Build
import com.softnet.blecompose.bluetooth.BluetoothLeService
import com.softnet.blecompose.bluetooth.CheckService
import com.softnet.blecompose.bluetooth.callback.GattCallbackImpl
import com.softnet.blecompose.bluetooth.dto.ConnectionState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class BluetoothLeServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val checkService: CheckService,
    private val adapter: BluetoothAdapter?,
    private val callback: GattCallbackImpl
): BluetoothLeService {
    private var gatt: BluetoothGatt? = null

    @SuppressLint("MissingPermission")
    override fun connect(address: String): Flow<ConnectionState> {
        checkService.beforeBluetoothFlow()
        adapter?.getRemoteDevice(address)?.let { device ->
            if(gatt != null) {
                gatt?.disconnect()
            }

            gatt = device.connectGatt(context, false, callback)
        }
        return callback.onConnectionStateChange
    }

    @SuppressLint("MissingPermission")
    override fun connect(device: BluetoothDevice): Flow<ConnectionState> {
        checkService.beforeBluetoothFlow()

        if(gatt != null) {
            gatt?.disconnect()
        }

        gatt = device.connectGatt(context, false, callback)
        return callback.onConnectionStateChange
    }

    @SuppressLint("MissingPermission")
    override fun discoverServices(): Flow<List<BluetoothGattService>> {
        checkService.beforeBluetoothFlow()

        if(gatt == null) {
            throw Exception("Gatt is null")
        }

        gatt?.discoverServices()
        return callback.onServiceDiscovered
    }

    override fun onConnectionStateChange(): Flow<ConnectionState> {
        return callback.onConnectionStateChange
    }

    @SuppressLint("MissingPermission")
    override fun disconnect() {
        checkService.beforeBluetoothFlow()
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }

    @SuppressLint("MissingPermission")
    override fun characteristicNotification(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        descriptorUUID: UUID,
        value: Boolean
    ): Flow<Unit> = flow {
        checkService.beforeBluetoothFlow()

        gatt?.run {
            val service = getService(serviceUUID)
            val characteristic = service.getCharacteristic(characteristicUUID)
            val descriptor = characteristic.getDescriptor(descriptorUUID)
            setCharacteristicNotification(characteristic, value)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            }
            else {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                writeDescriptor(descriptor)
            }
        }
        emit(Unit)
    }

    @SuppressLint("MissingPermission")
    override fun characteristicNotification(
        characteristic: BluetoothGattCharacteristic,
        descriptor: BluetoothGattDescriptor,
        value: Boolean
    ): Flow<Unit> = flow {
        checkService.beforeBluetoothFlow()

        gatt?.run {
            setCharacteristicNotification(characteristic, value)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            }
            else {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                writeDescriptor(descriptor)
            }
        }

        emit(Unit)
    }

    @SuppressLint("MissingPermission")
    override fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        data: ByteArray
    ): Flow<ByteArray> {
        checkService.beforeBluetoothFlow()

        if(gatt == null) {
            throw Exception("Gatt is null")
        }

        gatt?.run {
            val service = getService(serviceUUID)
            val characteristic = service.getCharacteristic(characteristicUUID)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                writeCharacteristic(characteristic, data, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
            }
            else {
                characteristic.value = data
                writeCharacteristic(characteristic)
            }
        }

        return callback.onCharacteristicChanged
    }

    @SuppressLint("MissingPermission")
    override fun writeCharacteristic(
        characteristic: BluetoothGattCharacteristic,
        data: ByteArray
    ): Flow<ByteArray> {
        checkService.beforeBluetoothFlow()

        if(gatt == null) {
            throw Exception("Gatt is null")
        }

        gatt?.run {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                writeCharacteristic(characteristic, data, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
            }
            else {
                characteristic.value = data
                writeCharacteristic(characteristic)
            }
        }

        return callback.onCharacteristicChanged
    }

}