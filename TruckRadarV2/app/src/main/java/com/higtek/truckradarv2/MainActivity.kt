package com.higtek.truckradarv2

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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

/*
    internal inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {
        override fun getInfoContents(marker: Marker): View {

            var mContext = getApplicationContext();

            val info = LinearLayout(mContext)
            info.orientation = LinearLayout.VERTICAL

            val title = TextView(mContext)
            title.setTextColor(Color.BLACK)
            title.gravity = Gravity.CENTER
            title.setTypeface(null, Typeface.BOLD)
            title.setText(marker.getTitle())

            val snippet = TextView(mContext)
            snippet.setTextColor(Color.BLACK)
            snippet.gravity = Gravity.LEFT
            snippet.setText(marker.getSnippet())

            info.addView(title)
            info.addView(snippet)

            return info
        }

        override fun getInfoWindow(p0: Marker): View? {
            return null
        }

    }
*/

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

    override fun onDestroy() {
        super.onDestroy()

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

    val truckPos  = remember { mutableStateOf(mainDataClass.truckPositions) }

    val uriFromPref = runBlocking { getServerUrl(curContext).first() }
    val userNameFromPref = runBlocking { getServerUsername(curContext).first() }
    val passwordFromPref = runBlocking { getServerPassword(curContext).first() }

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
            modifier = modifier.fillMaxSize(),
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
                    snippet = pos.getSnippet()
                    ) { marker ->
                    // Implement the custom info window here
                    Column(modifier = Modifier.background(color = Color.White).padding(5.dp)) {
                        Text(marker.title ?: "Default Marker Title", fontWeight = FontWeight.Bold)
                        Text(marker.snippet ?: "Default Marker Snippet")
                    }
                }
/*
                MarkerInfoWindowContent() {
                    marker ->
                    Text(marker.title ?: "Unknown Truck")
                    Text("HUY " + marker.snippet ?: "-")
                }
                Marker(
                    state = MarkerState(
                        position = LatLng(
                            pos.event.LatitudeFloat,
                            pos.event.LongitudeFloat
                        )
                    ),
                    title = "Truck " + pos.uid.toString(),
                    snippet = pos.getSnippet(),

                )

 */
            }
/*
            Marker(
                state = MarkerState(position = jerusalim),
                title = "Singapore",
                snippet = "Marker in Singapore"
            )

 */
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
