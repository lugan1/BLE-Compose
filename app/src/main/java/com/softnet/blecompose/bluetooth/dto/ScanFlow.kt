package com.softnet.blecompose.bluetooth.dto

import android.annotation.SuppressLint
import android.bluetooth.le.*
import com.softnet.temperature.core.bluetooth.callback.scanCallback
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@SuppressLint("MissingPermission")
fun BluetoothLeScanner.scanAsFlow(
    beforeScanning: () -> Unit = {},
    filters: List<ScanFilter>,
    settings: ScanSettings,
    onScanClosed: () -> Unit = {}
): Flow<ScanResult> = callbackFlow {
    beforeScanning()

    this.scanCallback()

    val callback = scanCallback()

    startScan(filters, settings, callback)

    awaitClose {
        stopScan(callback)
        onScanClosed()
    }
}

@SuppressLint("MissingPermission")
fun BluetoothLeScanner.scanAsFlow(
    beforeScanning: () -> Unit = {},
    onScanClosed: () -> Unit = {}
): Flow<ScanResult> = callbackFlow {
    beforeScanning()

    val callback = scanCallback()

    startScan(callback)

    awaitClose {
        stopScan(callback)
        onScanClosed()
    }
}
