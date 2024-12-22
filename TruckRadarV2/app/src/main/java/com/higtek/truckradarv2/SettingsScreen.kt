package com.higtek.truckradarv2

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, mainDataClass: MainDataClass) {

    // State variables to store user input
    val serverUri = rememberSaveable {
        mutableStateOf("http://gprs.higtek.in:89/")
    }

    val userName = rememberSaveable {
        mutableStateOf("truckRadar")
    }

    val password = rememberSaveable {
        mutableStateOf("truckRadar")
    }

    val isMarkerTruckPhotoEnable = rememberSaveable {
        mutableStateOf(true)
    }


    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val curContext = LocalContext.current

    val uriFromPref = runBlocking { getServerUrl(curContext).first() }
    serverUri.value = uriFromPref

    userName.value = runBlocking { getServerUsername(curContext).first() }
    password.value = runBlocking { getServerPassword(curContext).first() }
    isMarkerTruckPhotoEnable.value = runBlocking { getIsMarkerTruckPhotoEnabled(curContext).first() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "SETTINGS",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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

            // Server URL field
            OutlinedTextField(
                value = serverUri.value, onValueChange = {
                    serverUri.value = it
                },
                leadingIcon = {
                    Icon(Icons.Default.Star, contentDescription = "Server URL")
                },
                label = {
                    Text(text = "Server URL")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp, 0.dp, 0.dp)
            )

            OutlinedTextField(
                value = userName.value, onValueChange = {
                    userName.value = it
                },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = "User Name")
                },
                label = {
                    Text(text = "User Name")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp, 0.dp, 0.dp)
            )

            OutlinedTextField(
                value = password.value, onValueChange = {
                    password.value = it
                },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "Password")
                },
                label = {
                    Text(text = "Password")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp, 0.dp, 0.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Turn on photos of trucks "
                )
                Checkbox(
                    checked = isMarkerTruckPhotoEnable.value,
                    onCheckedChange = { isMarkerTruckPhotoEnable.value = it }
                )
            }

            // Save button
            OutlinedButton(
                onClick = {
                    runBlocking {
                        setServerUrl(curContext, serverUri.value)
                        setServerUsername(curContext, userName.value)
                        setServerPassword(curContext, password.value)
                        setIsMarkerTruckPhotoEnabled(curContext, isMarkerTruckPhotoEnable.value)
                    }
                    //navController.navigateUp()
                    Toast.makeText(curContext, "Settings saved", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 25.dp, 0.dp, 0.dp)
            ) {
                Text(
                    text = "Save",
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