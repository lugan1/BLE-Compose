package com.softnet.blecompose.bluetooth.impl

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.softnet.blecompose.bluetooth.BluetoothLeService
import com.softnet.blecompose.bluetooth.callback.GattCallbackImpl
import com.softnet.blecompose.bluetooth.dto.ConnectionState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class BluetoothLeServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
): BluetoothLeService {
    private var gatt: BluetoothGatt? = null
    private var manager: BluetoothManager? = null
    private var adapter: BluetoothAdapter? = null
    private val gattCallback = GattCallbackImpl()


    override fun connect(address: String): Flow<ConnectionState> {
        if(adapter == null) {
            throw Exception("BluetoothAdapter is null")
        }

        adapter?.getRemoteDevice(address)?.let { device ->
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                throw Exception("Permission not granted")
            }

            gatt = device.connectGatt(context, false, gattCallback)
        }
        return gattCallback.onConnectionStateChange
    }

    override fun connect(device: BluetoothDevice): Flow<ConnectionState> {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            throw Exception("Permission not granted")
        }
        if(gatt != null) {
            throw Exception("Gatt is not null")
        }

        gatt = device.connectGatt(context, false, gattCallback)
        return gattCallback.onConnectionStateChange
    }

    override fun discoverServices(): Flow<List<BluetoothGattService>> {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            throw Exception("Permission not granted")
        }
        if(gatt == null) {
            throw Exception("Gatt is null")
        }

        gatt?.discoverServices()
        return gattCallback.onServiceDiscovered
    }

    override fun onConnectionStateChange(): Flow<ConnectionState> {
        return gattCallback.onConnectionStateChange
    }

    override fun disconnect() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        gatt?.disconnect()
    }

    override fun characteristicNotification(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        descriptorUUID: UUID,
        value: Boolean
    ): Flow<Unit> = flow {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            throw Exception("Permission not granted")
        }

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

    override fun characteristicNotification(
        characteristic: BluetoothGattCharacteristic,
        descriptor: BluetoothGattDescriptor,
        value: Boolean
    ): Flow<Unit> = flow {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            throw Exception("Permission not granted")
        }

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

    override fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        data: ByteArray
    ): Flow<ByteArray> {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            throw Exception("Permission not granted")
        }
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

        return gattCallback.onCharacteristicChanged
    }

    override fun writeCharacteristic(
        characteristic: BluetoothGattCharacteristic,
        data: ByteArray
    ): Flow<ByteArray> {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            throw Exception("Permission not granted")
        }
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

        return gattCallback.onCharacteristicChanged
    }

}