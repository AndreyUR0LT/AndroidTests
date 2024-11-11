package com.higtek.testBle

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, mainDataClass: MainDataClass) {

    // State variables to store user input
    val userName = rememberSaveable {
        mutableStateOf("")
    }
    val userPassword = rememberSaveable {
        mutableStateOf("")
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val userNameTextFieldState = rememberTextFieldState()

    val curContext = LocalContext.current

    val composableScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "HI-G-TEK",
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

            // User select field
            LoginField(mainDataClass.listOfUserNames.toList(), userNameTextFieldState)

            // Password input field
            OutlinedTextField(
                value = userPassword.value, onValueChange = {
                    userPassword.value = it
                },
                leadingIcon = {
                    Icon(Icons.Default.Info, contentDescription = "password")
                },
                label = {
                    Text(text = "Password")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp, 0.dp, 0.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            // Login button
            OutlinedButton(
                onClick = {
                    mainDataClass.listOfUserNames.add("Hren")
                    //mainDataClass.isAuth = true;
                    //navController.navigate(NavRoutes.Home.route)
                    Toast.makeText(curContext, userNameTextFieldState.text, Toast.LENGTH_LONG).show()

                    composableScope.launch(Dispatchers.IO) {
                        testExecuteWebmethod()
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 25.dp, 0.dp, 0.dp)
            ) {
                Text(
                    text = "Login",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
        }

    }

    // Suppress Back Button.
    BackHandler {  }

}

val SOAP_ADDRESS: String = "http://192.168.1.120:81/communication.asmx"
val GET_USERS_ACTION: String = "/GetMhhtUsers"
val OPERATION_NAME: String = "GetMhhtUsers"
val WSDL_TARGET_NAMESPACE: String = ""
val XML_MhhtApplicationIdentifier : String = "IkqhgbC+NyZHpu3ardXVQOPsg6Ge0zxKHcs5wYCUwM8Sv7HjXg/9KD3pHyI4q4ltRhI9LVafBK8JwXGNX5KkfoYcDKSuyxbqxt41s1eAOVVMUTfTgBdsBpfDhZ/hmY+RZS3B6yqT9tMIdiJdUeHhr7FARC56hwr1iq4iMyJJSVXWG07YaZ1zKvW/w3vHGYkj8pS38HuIzC9zDv7GL9v95SIdBm8jqVTrHZUazoo3z33ZRyRpfsMiNtwWj2Q0QbxvLL98wRkmT40dYlBsUR/LTA=="


private fun testExecuteWebmethod(){

    //val soapH = SoapHelper()
    //soapH.Test()

    var result = ""
    //val SOAP_ACTION = Utils.SOAP_NAMESPACE + methodName
    val soapObject = SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME)


    soapObject.addProperty("xml", XML_MhhtApplicationIdentifier)
    soapObject.addProperty("login", "samarin")
    soapObject.addProperty("password", "samarin")

    val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
    envelope.setOutputSoapObject(soapObject)
    envelope.dotNet = true

    val httpTransportSE = HttpTransportSE(SOAP_ADDRESS)

    try {
        //httpTransportSE.call(SOAP_ACTION, envelope)
        httpTransportSE.call(GET_USERS_ACTION, envelope)
        val soapPrimitive = envelope.response
        result = soapPrimitive.toString()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    //return result
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginField(listOfUserNames: List<String>, userNameTextFieldState: TextFieldState) {

    val options: List<String> = listOfUserNames

    val textFieldState = userNameTextFieldState

    // The text that the user inputs into the text field can be used to filter the options.
    // This sample uses string subsequence matching.
    //val filteredOptions = options.filteredBy(textFieldState.text)
    val filteredOptions = options.filter{s -> s.contains(textFieldState.text, ignoreCase = true)}

    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }
    val expanded = allowExpanded && filteredOptions.isNotEmpty()


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = setExpanded,
    ) {
        TextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. An editable text field has
            // the anchor type `PrimaryEditable`.
            modifier =
            Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).padding(0.dp, 20.dp, 0.dp, 0.dp),
            state = textFieldState,
            lineLimits = TextFieldLineLimits.SingleLine,
            label = { Text("Login") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "person")
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                    // If the text field is editable, it is recommended to make the
                    // trailing icon a `menuAnchor` of type `SecondaryEditable`. This
                    // provides a better experience for certain accessibility services
                    // to choose a menu option without typing.
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable),
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),

        )
        ExposedDropdownMenu(
            modifier = Modifier.heightIn(max = 280.dp),
            expanded = expanded,
            onDismissRequest = { setExpanded(false) },
        ) {
            filteredOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        textFieldState.setTextAndPlaceCursorAtEnd(option)
                        setExpanded(false)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
