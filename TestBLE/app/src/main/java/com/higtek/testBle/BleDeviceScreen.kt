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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BleDeviceScreen(navController: NavController, mainDataClass: MainDataClass){

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val curContext = LocalContext.current

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
                    //navController.navigateUp()
                    //Toast.makeText(curContext, "Settings saved", Toast.LENGTH_LONG).show()
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

        }
    }

    //Text("About Page", fontSize = 30.sp)
}

//private var bluetoothGatt: BluetoothGatt? = null

@SuppressLint("MissingPermission")
fun connectToBle(curContext: Context, selectedBleDevice: ScanResult) {

    val makAddress = selectedBleDevice.device.address;

    var bluetoothGatt = selectedBleDevice.device.connectGatt(curContext, false, bluetoothGattCallback, TRANSPORT_LE)
}

private val bluetoothGattCallback = object : BluetoothGattCallback() {
    @SuppressLint("MissingPermission")
    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {

/*
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            // successfully connected to the GATT Server
            //broadcastUpdate(ACTION_GATT_CONNECTED)
            //connectionState = STATE_CONNECTED
            // Attempts to discover services after successful connection.
            //bluetoothGatt?.discoverServices()
            gatt?.discoverServices()
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            // disconnected from the GATT Server
            //broadcastUpdate(ACTION_GATT_DISCONNECTED)
            //connectionState = STATE_DISCONNECTED
            gatt?.close()
        }
*/

        if(status == BluetoothGatt.GATT_SUCCESS) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // Attempts to discover services after successful connection.
                gatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // Disconnected from the GATT Server
                gatt?.close()
            } else {
                // Ignore connecting and disconnecing state
            }
        } else {
            // Some error.
            gatt?.close()
            Log.w("BluetoothLeService.TAG", "onConnectionStateChange received: $status $newState")
        }
    }

    @SuppressLint("MissingPermission")
    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            //broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
            var listOfServices = gatt?.services

            listOfServices?.forEach { gattService ->
                if(gattService.uuid.toString().contains("1130")){
                    gattService.characteristics.forEach{characteristic ->
                        if(characteristic.uuid.toString().contains("1131")){
                            if(gatt?.readCharacteristic(characteristic) == true){
                                Log.w("BluetoothLeService.readCharacteristic", "reading characteristic  ${characteristic.uuid}")
                            }
                            else {
                                Log.w("BluetoothLeService.readCharacteristic", "ERROR: readCharacteristic failed for characteristic  ${characteristic.uuid}")
                            }
                        }
                    }
                }
            }

            var i = 0

        } else {
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
            Log.w("BluetoothLeService.onCharacteristicRead", "ERROR: readCharacteristic failed for characteristic  ${characteristic.uuid} status $status")
            return
        }

        var str = value.toString(Charsets.UTF_8)

        Log.w("BluetoothLeService.onCharacteristicRead", "Counter value: ${str}")

        var counter = str.toIntOrNull()

        if(counter == null)
            counter = 0;
        else
            counter++

        var strToWrite = counter.toString()
        var arrayToWrite = strToWrite.toByteArray(Charsets.UTF_8)

        characteristic.setValue(arrayToWrite)

        //gatt?.writeCharacteristic(characteristic, arrayToWrite, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
        gatt?.writeCharacteristic(characteristic)
    }

    @SuppressLint("MissingPermission")
    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {

        gatt?.disconnect()
        runBlocking {
            delay(100)
        }
        gatt?.close()
    }
}


