package com.higtek.testBle

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.higtek.testBle.ui.theme.TestBLETheme
import kotlinx.coroutines.launch
import org.ksoap2.serialization.SoapObject


var mainDataClass = MainDataClass(false)
var  bluetoothLeScanner : BluetoothLeScanner? = null
lateinit var  bluetoothAdapter : BluetoothAdapter
lateinit var  bluetoothManager : BluetoothManager

private var scanning = false
private val handlerForBleScan = Handler()

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


fun TestSoap() {

    val soapObject = SoapObject("1", "2")

}
//************************************************************************************************
//************************************************************************************************

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        setContent {
            TestBLETheme {
/*
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }

*/
                //MainScreen(MainDataClass(false))
                MainScreen(com.higtek.testBle.mainDataClass)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestBLETheme {
        MainScreen(MainDataClass(false))
    }
}

/************************************************************************************************/

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Login : NavRoutes("login")
    object Settings : NavRoutes("settings")
    object About : NavRoutes("about")
    object BleDeviceScreen : NavRoutes("bleDeviceScreen")
}

@Composable
fun MainScreen(mainDataClass: MainDataClass) {

    val navController = rememberNavController()

    Column(Modifier.padding(8.dp)) {
         NavHost(navController, startDestination = NavRoutes.Home.route) {
            composable(NavRoutes.Home.route) { HomeScreen(navController, mainDataClass) }
            composable(NavRoutes.Login.route) { LoginScreen(navController, mainDataClass) }
            composable(NavRoutes.Settings.route) { SettingsScreen(navController, mainDataClass)  }
            composable(NavRoutes.About.route) { About() }
            composable(NavRoutes.BleDeviceScreen.route) { BleDeviceScreen(navController, mainDataClass) }
        }
    }
}


//@SuppressLint("MissingPermission")
@Composable
fun TagList(
    scanResults: List<ScanResult>,
    mainDataClass: MainDataClass,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        val curContext = LocalContext.current

        scanResults.forEach { scanResult ->
            Row(
                modifier = Modifier
                    .padding(5.dp)
                    .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(5.dp)
            )
            {
                var messageToShow = "Name: " + scanResult.device.name.toString() + " Address: " + scanResult.device.address + " RSSI: " + scanResult.rssi.toString()

                Text(
                    text = messageToShow,
                    modifier = Modifier.clickable
                    {
                        mainDataClass.selectedBleDevice = scanResult
                        Toast.makeText(curContext, messageToShow, Toast.LENGTH_LONG).show()
                        navController.navigate(NavRoutes.BleDeviceScreen.route)
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, mainDataClass: MainDataClass) {

    if(mainDataClass.isAuth == false) {
        navController.navigate(NavRoutes.Login.route)
        return
    }

    // State variables to store user input
    val userName = rememberSaveable {
        mutableStateOf("")
    }

    val userPassword = rememberSaveable {
        mutableStateOf("")
    }

    val composableScope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val requestMultiplePermissions =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d("MyTag", "${it.key} = ${it.value}")
            }
        }

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
                        "TAGS",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate(NavRoutes.Settings.route) }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->

        // Column to arrange UI elements vertically
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(80.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5F),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TagList(mainDataClass.bleScanResults.toList(), mainDataClass, navController)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {

                        composableScope.launch {

                            if(!scanning)
                                mainDataClass.bleScanResults.clear()

                            if(!checkPermissions(curContext, requestMultiplePermissions)){
                                Toast.makeText(curContext, "Please grant the application permissions", Toast.LENGTH_LONG).show()
                                return@launch
                            }

                            startScan()
                        }


                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 25.dp, 0.dp, 0.dp)
                ) {
                    Text(
                        text = "Scan",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }

            }

        }

    }

    // Suppress Back Button.
//    BackHandler {  }

}

fun checkPermissions(curContext: Context, requestMultiplePermissions: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>): Boolean {

    var isPermGranted : Boolean = false

    val locationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION
    val bluetoothPermission = android.Manifest.permission.BLUETOOTH_SCAN

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if ((ContextCompat.checkSelfPermission(curContext, locationPermission) == PackageManager.PERMISSION_GRANTED) &&
            (ContextCompat.checkSelfPermission(curContext, bluetoothPermission) == PackageManager.PERMISSION_GRANTED))
            isPermGranted = true

    } else {
        if (ContextCompat.checkSelfPermission(curContext, locationPermission) == PackageManager.PERMISSION_GRANTED)
            isPermGranted = true
    }

    if(!isPermGranted) {

        requestMultiplePermissions.launch(
            arrayOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )

        return false
    }

    return true
}

// Stops scanning after 10 seconds.
private val SCAN_PERIOD: Long = 10000

fun startScan() {

    if(bluetoothAdapter.isEnabled != true){
        //promptEnableBluetooth()
        // TODO Show message
        return;
    }

    bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    if(bluetoothLeScanner == null){
        // TODO Show message
        return;
    }

    if (!scanning) { // Stops scanning after a pre-defined scan period.
        handlerForBleScan.postDelayed({
            scanning = false
            bluetoothLeScanner?.stopScan(leScanCallback)
        }, SCAN_PERIOD)
        scanning = true
        bluetoothLeScanner?.startScan(leScanCallback)
    } else {
        scanning = false
        bluetoothLeScanner?.stopScan(leScanCallback)
    }

}

private val leScanCallback: ScanCallback = object : ScanCallback() {

    override fun onScanResult(callbackType: Int, result: ScanResult) {
        // Found device:

        var isContains : Boolean = false
        val address = result.device.address.toString()

        mainDataClass.bleScanResults.forEach{
            if(it.device.address.contains(address)){
                isContains = true
                return@forEach
            }
        }

        if(!isContains)
            mainDataClass.bleScanResults.add(result)
    }

    /*
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            // Found device:

            val device: BluetoothDevice = result.getDevice()
            val address = device.address.toString()
            var isContains : Boolean = false


            var record = "Name: " + device.name.toString() + " Address: " + address + " RSSI: " + result.rssi.toString()

            mainDataClass.tags.forEach{
                if(it.contains(address)){
                    isContains = true
                    return@forEach
                }
            }

            //if(!mainDataClass.tags.contains(address))
            if(!isContains)
                mainDataClass.tags.add(record)



        }
    */

    override fun onBatchScanResults(results: List<ScanResult?>?) {
        // Ignore for now
    }

    override fun onScanFailed(errorCode: Int) {
        // Ignore for now
    }
}



