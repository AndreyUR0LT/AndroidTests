package com.higtek.testBle

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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


//import org.ksoap2.SoapEnvelope
//import com.google.code.ksoap2
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject


var mainDataClass = MainDataClass(false)
//var _tags = mutableStateListOf<String>("One", "Two")

//lateinit var  bluetoothLeScanner : BluetoothLeScanner
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
/*
fun requestBluetooth() {
    // check android 12+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        requestMultiplePermissions.launch(
            arrayOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
            )
        )
    } else {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        requestEnableBluetooth.launch(enableBtIntent)
    }
}

private val requestEnableBluetooth =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // granted
        } else {
            // denied
        }
    }

private val requestMultiplePermissions =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            Log.d("MyTag", "${it.key} = ${it.value}")
        }
    }
*/
//************************************************************************************************

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        //bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

/*
        val requestEnableBluetooth =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // granted
                } else {
                    // denied
                }
            }

        val requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    Log.d("MyTag", "${it.key} = ${it.value}")
                }
            }

        val locationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION
        //val locationPermission = android.Manifest.permission.ACCESS_COARSE_LOCATION
        val requestLocationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                //connectHC05(bluetoothAdapter, deviceList, connectStatus)
            } else {
                //connectStatus.value = "Location Permission not accepted"
            }
        }

        //Check whether the user has already granted the runtime permission
        if (ContextCompat.checkSelfPermission(applicationContext, locationPermission) == PackageManager.PERMISSION_GRANTED) {
            //connectHC05(bluetoothAdapter, deviceList, connectStatus)
            val i = 0
        } else {
            //requestLocationPermissionLauncher.launch(locationPermission)
            requestMultiplePermissions.launch(
                arrayOf(
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
*/

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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
        Text("Huy11111111")

        Text(
            text = "Hello $name!",
            modifier = modifier
        )

        Text("Huy000000000")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestBLETheme {
        //Greeting("Android")
        MainScreen(MainDataClass(false))
    }
}

/************************************************************************************************/

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Login : NavRoutes("login")
    object Settings : NavRoutes("settings")
    object About : NavRoutes("about")
}

@Composable
fun MainScreen(mainDataClass: MainDataClass) {

    val navController = rememberNavController()

    Column(Modifier.padding(8.dp)) {
         NavHost(navController, startDestination = NavRoutes.Home.route) {
            composable(NavRoutes.Home.route) { HomeScreen(navController, mainDataClass) }
            //composable(NavRoutes.Login.route) { Login(navController)  }
             composable(NavRoutes.Login.route) { LoginScreen(navController, mainDataClass) }
             composable(NavRoutes.Settings.route) { SettingsScreen(navController, mainDataClass)  }
            composable(NavRoutes.About.route) { About() }
        }
    }

/*
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LoginScreen(
            modifier = Modifier.padding(innerPadding)
        )
    }
*/
}


@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){

        Text("Huy11111111!!!!!!!!!!!!!!!!!!!!!")

        Text(
            text = "Hello Stariy Her",
            modifier = modifier
        )

        Text("Huy000000000")
    }
}


@Composable
fun Home(navController: NavController, mainDataClass: MainDataClass){

    if(mainDataClass.isAuth == false) {
        navController.navigate(NavRoutes.Login.route)
        return
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
        Text("Home Page", fontSize = 30.sp)
        Button(onClick = {navController.navigate(NavRoutes.Login.route)} ) { Text("To Login")}
    }
}

@Composable
fun Login(navController: NavController){
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
        Text("Contact Page", fontSize = 30.sp)
        Button(onClick = {navController.navigate(NavRoutes.Home.route)} ) { Text("Back")}
    }

    // Suppress Back Button.
    BackHandler {  }
}



@Composable
fun TagList(messages: List<String>) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        messages.forEach { message ->
            Row(
                modifier = Modifier
                    .padding(5.dp)
                    .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(5.dp)
            )
            {
                Text(text = message)
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

/*
            // top row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.Yellow),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Top")
            }
*/
            // content row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5F),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //Text(text = "Some Other Contents")
                TagList(mainDataClass.tags.toList())

/*
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                )
                {
                    for( v in 0..10)
                        Text(text = "ttt $v")

                    Text(text = "Tag 0")
                    Text(text = "Tag 1")
                    Text(text = "Tag 2")
                    Text(text = "Tag 3")
                    Text(text = "Tag 4")
                    Text(text = "Tag 5")

                }
*/
            }

            // bottom row
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {

                        composableScope.launch {
                            //mainDataClass.tags.add("Hren")
                            if(!scanning)
                                mainDataClass.tags.clear()

                            //checkPermissions(requestMultiplePermissions)
                            if(!check2(curContext, requestMultiplePermissions)){
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldSnackbarDemo() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val count = remember{ mutableStateOf(0) }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                content = {Icon(Icons.Filled.Warning, contentDescription = "Добавить")},
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Count: ${++count.value}")
                    }
                }
            )
        }
    ){
        Text("Count: ${count.value}", fontSize = 28.sp, modifier=Modifier.padding(it))
    }
}

fun check2(curContext: Context, requestMultiplePermissions: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>): Boolean {

    var isPermGranted : Boolean = false

    val locationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION
    val bluetoothPermission = android.Manifest.permission.BLUETOOTH_SCAN

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if ((ContextCompat.checkSelfPermission(curContext, locationPermission) == PackageManager.PERMISSION_GRANTED) &&
            (ContextCompat.checkSelfPermission(curContext, bluetoothPermission) == PackageManager.PERMISSION_GRANTED))
            isPermGranted = true

    } else {
//        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//        requestEnableBluetooth.launch(enableBtIntent)
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


/*
private var requestBluetooth = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    if (result.resultCode == RESULT_OK) {
        //granted
    }else{
        //deny
    }
}

var activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.getResultCode() !== MainActivity.RESULT_OK) {
        promptEnableBluetooth()
    }
}
*/
/*
private fun promptEnableBluetooth() {
    if (!bluetoothAdapter.isEnabled) {
        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        //activityResultLauncher.launch(enableIntent)
        //startActivityForResult(enableIntent, 1)
    }
}

*/
/*
fun <I, O> Activity.registerForActivityResult(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>
) = (this as ComponentActivity).registerForActivityResult(contract, callback)
*/

//@Composable
private fun checkPermissions(requestMultiplePermissions: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>){

/*
    val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d("MyTag", "${it.key} = ${it.value}")
            }
        }
*/
/*
    val locationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION
    //val locationPermission = android.Manifest.permission.ACCESS_COARSE_LOCATION
    val requestLocationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            //connectHC05(bluetoothAdapter, deviceList, connectStatus)
        } else {
            //connectStatus.value = "Location Permission not accepted"
        }
    }
*/
/*
    //Check whether the user has already granted the runtime permission
    val locationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION
    if (ContextCompat.checkSelfPermission(LocalContext.current, locationPermission) == PackageManager.PERMISSION_GRANTED) {
        //connectHC05(bluetoothAdapter, deviceList, connectStatus)
        val i = 0
    } else {
        //requestLocationPermissionLauncher.launch(locationPermission)

        requestMultiplePermissions.launch(
            arrayOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
    }
*/
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
        val device: BluetoothDevice = result.getDevice()
        val address = device.address.toString()
        var isContains : Boolean = false

        // ...do whatever you want with this found device
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

    override fun onBatchScanResults(results: List<ScanResult?>?) {
        // Ignore for now
    }

    override fun onScanFailed(errorCode: Int) {
        // Ignore for now
    }
}



