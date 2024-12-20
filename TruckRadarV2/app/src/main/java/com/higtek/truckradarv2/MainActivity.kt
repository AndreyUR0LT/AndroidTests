package com.higtek.truckradarv2

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.higtek.truckradarv2.ui.theme.TruckRadarV2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


var mainDataClass = MainDataClass()

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TruckRadarV2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
/*
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
*/
                    //MapScreen(modifier = Modifier.padding(innerPadding))

                    MainScreen(mainDataClass, modifier = Modifier.padding(innerPadding))

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )

    //GoogleMap()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TruckRadarV2Theme {
        Greeting("Android")
    }
}


/************************************************************************************************/

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Settings : NavRoutes("settings")
}

@Composable
fun MainScreen(mainDataClass: MainDataClass, modifier: Modifier) {

    val navController = rememberNavController()

    Column(Modifier.padding(8.dp)) {
        NavHost(navController, startDestination = NavRoutes.Home.route) {
            composable(NavRoutes.Home.route) { MapScreen(navController, modifier, mainDataClass) }
            composable(NavRoutes.Settings.route) { SettingsScreen(navController, mainDataClass)  }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, modifier: Modifier, mainDataClass: MainDataClass) {

    val openClearEventsDialog = remember { mutableStateOf(false) }

    val composableScope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val jerusalim = LatLng(31.8, 35.1)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(jerusalim, 8f)
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
                        "Truck Radar",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = { openClearEventsDialog.value = !openClearEventsDialog.value }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Clear Events"
                        )
                    }
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

        GoogleMap(
            modifier = modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = jerusalim),
                title = "Singapore",
                snippet = "Marker in Singapore"
            )
        }

    }


    when {
        openClearEventsDialog.value -> {
            ClearEventsDialog(
                onDismissRequest = { openClearEventsDialog.value = false },
                onConfirmation = {
                    openClearEventsDialog.value = false

                    println("Confirmation registered") // Add logic here to handle confirmation.
                }
            )
        }
    }



}


@Composable
fun ClearEventsDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icons.Filled.Warning
        },
        title = {
            Text(text = "CLEAR EVENTS")
        },
        text = {
            Text(text = "Do you want clear all events on server?")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MapScreenOld(modifier: Modifier = Modifier) {

    val jerusalim = LatLng(31.8, 35.1)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(jerusalim, 8f)
    }


    GoogleMap(
        //modifier = Modifier.fillMaxSize(),
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = jerusalim),
            title = "Singapore",
            snippet = "Marker in Singapore"
        )
    }
}


@Composable
fun MapScreen2(modifier111: Modifier = Modifier) {

    var uiSettings by remember { mutableStateOf(MapUiSettings()) }

    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.SATELLITE))
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            properties = properties,
            uiSettings = uiSettings
        )
        Switch(
            checked = uiSettings.zoomControlsEnabled,
            onCheckedChange = {
                uiSettings = uiSettings.copy(zoomControlsEnabled = it)
            }
        )
    }

}
