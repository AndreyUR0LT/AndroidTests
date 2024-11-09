package com.higtek.testBle

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CenterAlignedTopAppBar
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
        mutableStateOf("http://192.168.1.120:83/TransportWebService.asmx")
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val curContext = LocalContext.current

    val uriFromPref = runBlocking { getServerUrl(curContext).first() }

    serverUri.value = uriFromPref

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
                /*
                                actions = {
                                    IconButton(onClick = { /* do something */ }) {
                                        Icon(
                                            imageVector = Icons.Filled.Settings,
                                            contentDescription = "Settings"
                                        )
                                    }
                                },

                 */
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

            // Username input field
            OutlinedTextField(
                value = serverUri.value, onValueChange = {
                    serverUri.value = it
                },
                leadingIcon = {
                    Icon(Icons.Default.Info, contentDescription = "Server URL")
                },
                label = {
                    Text(text = "Server URL")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp, 0.dp, 0.dp)
            )

            // Save button
            OutlinedButton(
                onClick = {
                    runBlocking { setServerUrl(curContext, serverUri.value) }
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

            //MenuWithScrollStateSample()
            //MultiAutocompleteExposedDropdownMenuSample()
            //ExposedDropdownMenuSample()
            //EditableExposedDropdownMenuSample()
        }

    }

}