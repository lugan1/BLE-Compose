package com.softnet.blecompose.bluetooth.callback

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class ScanCallbackImpl: ScanCallback() {
    val onScanResult = MutableSharedFlow<ScanResult>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        super.onScanResult(callbackType, result)
        result?.run { onScanResult.tryEmit(this) }
    }
}