package com.softnet.blecompose.bluetooth.dto

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult

@SuppressLint("MissingPermission")
fun ScanResult.toDto(): ScanResultDto = ScanResultDto(
    name = device.name ?: "Unknown",
    macAddress = device.address,
    rssi = rssi,
    bytes = scanRecord?.bytes
)