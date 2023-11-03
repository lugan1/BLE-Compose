package com.softnet.blecompose.bluetooth.impl

import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import com.softnet.blecompose.bluetooth.CheckService
import com.softnet.blecompose.bluetooth.ScanService
import com.softnet.blecompose.bluetooth.dto.scanAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject


class ScanServiceImpl @Inject constructor(
    private val check: CheckService,
    private val scanner: BluetoothLeScanner?,
): ScanService {
    override var isScanning: Boolean = false

    override fun startScan(): Flow<ScanResult>
    = scanner?.scanAsFlow(
        beforeScanning = {
            check.beforeBluetoothFlow()
            isScanning = true
        },
        onScanClosed = { isScanning = false }
    ) ?: callbackFlow { close() }

    override fun startScan(filters: List<ScanFilter>, settings: ScanSettings): Flow<ScanResult>
        = scanner?.scanAsFlow(
            filters = filters,
            settings = settings,
            beforeScanning = {
                check.beforeBluetoothFlow()
                isScanning = true
            },
            onScanClosed = { isScanning = false }
        ) ?: callbackFlow { close() }
}
