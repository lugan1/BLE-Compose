package com.softnet.blecompose.bluetooth

import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import kotlinx.coroutines.flow.Flow

interface ScanService {
    val isScanning: Boolean
    fun startScan(): Flow<ScanResult>
    fun startScan(filters: List<ScanFilter>, settings: ScanSettings): Flow<ScanResult>
}
