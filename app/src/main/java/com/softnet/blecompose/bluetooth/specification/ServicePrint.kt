package com.softnet.blecompose.bluetooth.specification

import android.bluetooth.BluetoothGattService
import com.softnet.blecompose.bluetooth.specification.characteristic.CharacteristicUUID
import com.softnet.blecompose.bluetooth.specification.characteristic.Property
import com.softnet.blecompose.bluetooth.specification.characteristic.WriteType

fun BluetoothGattService.print(): String {
    val serviceInfo = StringBuilder("Service: ${ServiceUUID.from(this.uuid.toString())} (${this.uuid})\n")

    this.characteristics.forEach { characteristic ->
        serviceInfo.append("\tCharacteristic : ${CharacteristicUUID.from(characteristic.uuid.toString())} (${characteristic.uuid}), ")
        serviceInfo.append("Properties: ${Property.from(characteristic.properties)}, ")
        serviceInfo.append("WriteType: ${WriteType.from(characteristic.writeType)}\n")

        characteristic.descriptors.forEach { descriptor ->
            serviceInfo.append("\t\tDescriptor : ${DescriptorUUID.from(descriptor.uuid.toString())} (${descriptor.uuid})\n")
        }
    }

    return serviceInfo.toString()
}
