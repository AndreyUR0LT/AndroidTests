package com.higtek.truckradarv2

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.higtek.truckradarv2.ui.theme.TruckRadarV2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


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
    val openSendSetCommandDialog = remember { mutableStateOf(false) }
    //val selectedTruckUid = remember { mutableStateOf("") }

    val composableScope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val jerusalem = LatLng(31.8, 35.1)
    val kharkiv = LatLng(50.0033, 36.2711)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(kharkiv, 8f)
    }

    val curContext = LocalContext.current

    val truckPos  = remember { mutableStateOf(mainDataClass.truckPositions) }

    val currentTruckPos = remember { mutableStateOf(TruckPosition()) }

    val uriFromPref = runBlocking { getServerUrl(curContext).first() }
    val userNameFromPref = runBlocking { getServerUsername(curContext).first() }
    val passwordFromPref = runBlocking { getServerPassword(curContext).first() }
    val isMarkerTruckPhotoEnable = runBlocking { getIsMarkerTruckPhotoEnabled(curContext).first() }

    HgcApi.updateUrl(uriFromPref)

    composableScope.launch(Dispatchers.IO) {
        while(true){

            var result = getEvents(userNameFromPref, passwordFromPref, mainDataClass)
            if (result){
                truckPos.value = mainDataClass.truckPositions
                delay(SERVER_DELAY)
            }
            else
                delay(SERVER_DELAY_LONG)
        }
    }


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
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            cameraPositionState = cameraPositionState,
        ) {
            truckPos.value.forEach{ pos ->

                MarkerInfoWindow(
                    state = MarkerState(
                        position = LatLng(
                            pos.event.LatitudeFloat,
                            pos.event.LongitudeFloat
                        )
                    ),
                    title = "Truck " + pos.uid.toString(),
                    snippet = pos.getSnippet(),
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.truckmarker24),
                    onInfoWindowLongClick = { marker ->
                        currentTruckPos.value = pos
//                        selectedTruckUid.value = pos.uid
                        openSendSetCommandDialog.value = !openSendSetCommandDialog.value
                    }
                    ) { marker ->
                    // Custom info window
                    Column(modifier = Modifier.background(color = Color.White).padding(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(marker.title ?: "Default Marker Title", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Row()
                        {
                            if(isMarkerTruckPhotoEnable){
                                Image(
                                    painter = painterResource(pos.truckPhotoResourceId),
                                    contentDescription = "",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(150.dp)
                                )
                            }
                            Text(marker.snippet ?: "Default Marker Snippet")
                        }
                        pos.sealStatuses?.forEach{seal -> Text(seal.sealId + " - " + seal.statusDescription)}

                        Button(onClick = {}) {
                            Text("Long tap to SET")
                        }
                    }
                }

            }

        }

    }


    when {
        openClearEventsDialog.value -> {
            ClearEventsDialog(
                onDismissRequest = { openClearEventsDialog.value = false },
                onConfirmation = {
                    openClearEventsDialog.value = false
                    composableScope.launch(Dispatchers.IO) {
                        clearEvents(userNameFromPref, passwordFromPref)
                    }

                    try{
                        mainDataClass.truckPositions.clear()
                        truckPos.value = mainDataClass.truckPositions
                    }catch(t: Throwable){
                        t.printStackTrace()
                    }

                }
            )
        }


    }

    when {
        openSendSetCommandDialog.value -> {
            SendSetCommandDialog(
                onDismissRequest = { openSendSetCommandDialog.value = false },
                onConfirmation = {
                    openSendSetCommandDialog.value = false
                    composableScope.launch(Dispatchers.IO) {
                        sendSetCommand(userNameFromPref, passwordFromPref, currentTruckPos.value)
                        composableScope.launch(Dispatchers.Main) {
                            Toast.makeText(curContext, "SET Command was send", Toast.LENGTH_LONG).show()
                        }
                    }

                },
                currentTruckPos.value.uid
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
fun SendSetCommandDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    truckUid : String
) {
    AlertDialog(
        icon = {
            Icons.Filled.Warning
        },
        title = {
            Text(text = "SET Operation")
        },
        text = {
            Text(text = "Do you want send SET command to truck $truckUid?")
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
