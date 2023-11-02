package com.softnet.blecompose.domain.impl


/*

class ScanServiceImpl @Inject constructor(
    private val check: CheckService,
    private val scanner: BluetoothLeScanner?,
): ScanService() {
    override var isScanning: Boolean = false

    override fun startScan(): Flow<ScanResultDto>
    = scanner?.scanAsFlow(
        beforeScanning = {
            check.beforeBluetoothFlow()
            isScanning = true
        },
        onScanClosed = { isScanning = false }
    )?.map { it.toDto() } ?: callbackFlow { close() }

    override fun startScan(filters: List<ScanFilter>, settings: ScanSettings): Flow<ScanResultDto>
        = scanner?.scanAsFlow(
            filters = filters,
            settings = settings,
            beforeScanning = {
                check.beforeBluetoothFlow()
                isScanning = true
            },
            onScanClosed = { isScanning = false }
        )?.map { it.toDto() } ?: callbackFlow { close() }
}
*/
