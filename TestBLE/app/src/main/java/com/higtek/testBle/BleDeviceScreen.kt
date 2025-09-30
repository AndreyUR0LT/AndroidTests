package com.higtek.testBle

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter.STATE_CONNECTED
import android.bluetooth.BluetoothAdapter.STATE_DISCONNECTED
import android.bluetooth.BluetoothDevice.TRANSPORT_LE
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


class BleOperationsViewModel : ViewModel(){
    private val _logText = MutableLiveData<String>()
    val logText : LiveData<String> = _logText

    private var _logCahse: String = ""

    fun AddRecordToLog(message : String){

        _logCahse += message  + "\n"
        _logText.postValue(_logCahse)
/*
        if(_logText.value == null)
            _logText.postValue(message + "\n")
        else
            _logText.postValue(_logText.value + message + "\n")
*/
    }

    fun ClearLog(){
        _logCahse = ""
        _logText.postValue(_logCahse)
        //_logText.postValue(" ")
    }
}

var bleOperationsViewModel = BleOperationsViewModel()



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BleDeviceScreen(navController: NavController, mainDataClass: MainDataClass){

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val curContext = LocalContext.current
    val scrollState = rememberScrollState()
    val logText by bleOperationsViewModel.logText.observeAsState()


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "DEVICE OPERATIONS",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            // Connect button
            OutlinedButton(
                onClick = {
                    runBlocking { connectToBle(curContext, mainDataClass.selectedBleDevice) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 25.dp, 0.dp, 0.dp)
            ) {
                Text(
                    text = "Connect",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    //.height(200.dp) // Fixed height for the scrollable area
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScrollbar(scrollState) // This must call BEFORE ".verticalScroll(scrollState)"!!!!
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        modifier = Modifier.padding(end = 10.dp),
                        text = "${logText ?: "Log Empty"}"
                    )
                }
            }


        }
    }

    //Text("About Page", fontSize = 30.sp)
}


@SuppressLint("MissingPermission")
fun connectToBle(curContext: Context, selectedBleDevice: ScanResult) {

    bleOperationsViewModel.ClearLog()
    bleOperationsViewModel.AddRecordToLog("Try to connect to device ${selectedBleDevice.device.name} (${selectedBleDevice.device.address})")
    var bluetoothGatt = selectedBleDevice.device.connectGatt(curContext, false, bluetoothGattCallback, TRANSPORT_LE)
}

@OptIn(ExperimentalStdlibApi::class)
private val bluetoothGattCallback = object : BluetoothGattCallback() {
    @SuppressLint("MissingPermission")
    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {

        if(status == BluetoothGatt.GATT_SUCCESS) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                bleOperationsViewModel.AddRecordToLog("Connection state - CONNECTED")
                // Attempts to discover services after successful connection.
                gatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                bleOperationsViewModel.AddRecordToLog("Connection state - DISCONNECTED, close connection.")
                // Disconnected from the GATT Server
                gatt?.close()
            } else {
                // Ignore connecting and disconnecing state
            }
        } else {
            bleOperationsViewModel.AddRecordToLog("Connection error, status: ${status}, newState: (${newState})")
            // Some error.
            gatt?.close()
            Log.w("BluetoothLeService.TAG", "onConnectionStateChange received: $status $newState")
        }
    }

    @SuppressLint("MissingPermission")
    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            bleOperationsViewModel.AddRecordToLog("BLE Services discovered successfully.")

            var listOfServices = gatt?.services

            listOfServices?.forEach { gattService ->
                if(gattService.uuid.toString().contains("1130")){
                    bleOperationsViewModel.AddRecordToLog("BLE Service ${gattService.uuid.toString()} found.")
                    gattService.characteristics.forEach{characteristic ->
                        if(characteristic.uuid.toString().contains("1131")){
                            bleOperationsViewModel.AddRecordToLog("BLE Service Characteristic ${characteristic.uuid.toString()} found.")
                            if(gatt?.readCharacteristic(characteristic) == true){
                                bleOperationsViewModel.AddRecordToLog("Start BLE Service Characteristic Read operation return TRUE")
                                //Log.w("BluetoothLeService.readCharacteristic", "reading characteristic  ${characteristic.uuid}")
                            }
                            else {
                                bleOperationsViewModel.AddRecordToLog("Start BLE Service Characteristic Read operation return FALSE")
                                //Log.w("BluetoothLeService.readCharacteristic", "ERROR: readCharacteristic failed for characteristic  ${characteristic.uuid}")
                            }
                        }
                    }
                }
            }
        } else {
            bleOperationsViewModel.AddRecordToLog("BLE Services discover failed. Status: $status")
            gatt?.disconnect()
            Log.w("BluetoothLeService.TAG", "onServicesDiscovered received: $status")
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated(
        "Used natively in Android 12 and lower",
        ReplaceWith("onCharacteristicRead(gatt, characteristic, characteristic.value, status)")
    )
    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ) = onCharacteristicRead(gatt, characteristic, characteristic.value, status)

    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray,
        status: Int
    ) {
        if (status != BluetoothGatt.GATT_SUCCESS) {
            bleOperationsViewModel.AddRecordToLog("BLE Service Characteristic Read operation FAILED. Characteristic:  ${characteristic.uuid}, Status: $status")
            Log.w("BluetoothLeService.onCharacteristicRead", "ERROR: readCharacteristic failed for characteristic  ${characteristic.uuid} status $status")
            closeConnection(gatt)
            return
        }

        bleOperationsViewModel.AddRecordToLog("BLE Service Characteristic Read operation successfully.")

        var str = value.toString(Charsets.UTF_8)

        val customHexFormat = HexFormat {
            bytes {
                byteSeparator = " " // Separate bytes with a space
                upperCase = true     // Use uppercase hex digits
                bytePrefix = "0x"    // Add a prefix to each byte
            }
        }
        bleOperationsViewModel.AddRecordToLog("HEX Value: ${value.toHexString(customHexFormat)}")
        bleOperationsViewModel.AddRecordToLog("Value: $str")

        //Log.w("BluetoothLeService.onCharacteristicRead", "Counter value: ${str}")

        var counter = str.toIntOrNull()

        if(counter == null)
            counter = 0;
        else
            counter++

        var strToWrite = counter.toString()
        var arrayToWrite = strToWrite.toByteArray(Charsets.UTF_8)

        characteristic.setValue(arrayToWrite)

        bleOperationsViewModel.AddRecordToLog("Start BLE Service Characteristic Write operation")

        gatt?.writeCharacteristic(characteristic)
    }

    @SuppressLint("MissingPermission")
    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        bleOperationsViewModel.AddRecordToLog("BLE Service Characteristic Write operation finished. Status: $status")
        bleOperationsViewModel.AddRecordToLog("Close connection")

        gatt?.disconnect()
        runBlocking {
            delay(100)
        }
        gatt?.close()

        //bleOperationsDataClass.logText.value = bleOperationsDataClass.logText.value + "Huy! "
        //bleOperationsViewModel.AddRecordToLog("Huy! ")
    }

    @SuppressLint("MissingPermission")
    fun closeConnection(gatt: BluetoothGatt){
        gatt.disconnect()
        runBlocking {
            delay(100)
        }
        gatt.close()
    }
}


@Composable
fun Modifier.verticalScrollbar(state: ScrollState, scrollbarWidth: Dp = 6.dp, color: Color = Color.LightGray): Modifier{
    val alpha by animateFloatAsState(targetValue = if(state.isScrollInProgress) 1f else 0f,
        animationSpec = tween(400, delayMillis = if(state.isScrollInProgress) 0 else 700)
    )

    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //This must call BEFORE ".verticalScroll(scrollState)"!!!!
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    return this then Modifier.drawWithContent {
        drawContent()


        val viewHeight = state.viewportSize.toFloat()
        val contentHeight = state.maxValue + viewHeight

        val scrollbarHeight = (viewHeight * (viewHeight / contentHeight )).coerceIn(10.dp.toPx() .. viewHeight)
        val variableZone = viewHeight - scrollbarHeight
        val scrollbarYoffset = (state.value.toFloat() / state.maxValue) * variableZone

        drawRoundRect(
            cornerRadius = CornerRadius(scrollbarWidth.toPx() / 2, scrollbarWidth.toPx() / 2),
            color = color,
            topLeft = Offset(this.size.width - scrollbarWidth.toPx(), scrollbarYoffset),
            size = Size(scrollbarWidth.toPx(), scrollbarHeight),
            alpha = alpha
        )
    }
}
