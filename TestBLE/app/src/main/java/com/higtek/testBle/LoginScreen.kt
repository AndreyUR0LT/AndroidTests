package com.higtek.testBle

import android.content.Context
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
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


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

    val passwordTextFieldState = rememberTextFieldState()

    val curContext = LocalContext.current

    val composableScope = rememberCoroutineScope()

    val uriFromPref = runBlocking { getServerUrl(curContext).first() }

    LaunchedEffect(true) {
        composableScope.launch(Dispatchers.IO) {
            updateUsers(uriFromPref, curContext, composableScope)
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
                        "HI-G-TEK",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = { composableScope.launch(Dispatchers.IO) {updateUsers(uriFromPref, curContext, composableScope)} }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh"
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
                    //mainDataClass.listOfUserNames.add("Hren")
                    //mainDataClass.isAuth = true;
                    //navController.navigate(NavRoutes.Home.route)
                    //Toast.makeText(curContext, userNameTextFieldState.text, Toast.LENGTH_LONG).show()

                    for (item in com.higtek.testBle.mainDataClass.listOfMhhtUsers){
                        if(item.Login == userNameTextFieldState.text){
                            var pwdHash = calculateMd5Hash(userPassword.value)
                            if (item.PasswordHash == pwdHash)
                                composableScope.launch(Dispatchers.Main) {Toast.makeText(curContext, "Password OK", Toast.LENGTH_LONG).show()}
                            else
                                composableScope.launch(Dispatchers.Main) {Toast.makeText(curContext, "Password is incorrect", Toast.LENGTH_LONG).show()}
                            break
                        }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 25.dp, 0.dp, 0.dp)
            ) {
                Text(
                    text = "Check Login",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }

            // Go to Scanner screen button
            OutlinedButton(
                onClick = {
                    mainDataClass.isAuth = true;
                    navController.navigate(NavRoutes.Home.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 25.dp, 0.dp, 0.dp)
            ) {
                Text(
                    text = "Go to Scanner",
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

fun updateUsers(url: String, curContext: Context, composableScope: CoroutineScope){

    var users = getMHHTUsersForWorkStation(url)

    if(users == null){
        composableScope.launch(Dispatchers.Main) {Toast.makeText(curContext, "Updating failed", Toast.LENGTH_LONG).show()}
        return
    }

    if(users.count() <= 0){
        composableScope.launch(Dispatchers.Main) {Toast.makeText(curContext, "List of users from ESP is empty", Toast.LENGTH_LONG).show()}
        return
    }

    mainDataClass.listOfUserNames.clear()
    mainDataClass.listOfMhhtUsers.clear()

    for ( item in users){
        mainDataClass.listOfUserNames.add(item.Login)
        mainDataClass.listOfMhhtUsers.add(item)
    }

    composableScope.launch(Dispatchers.Main) {Toast.makeText(curContext, "List of users was updated", Toast.LENGTH_LONG).show()}
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

//********************************************************************************************************
// Utils
//********************************************************************************************************

val SOAP_ADDRESS: String = "http://192.168.1.120:81/communication.asmx"
//val SOAP_ADDRESS: String = "https://gprs.higtek.com:99/communication.asmx"
val GET_USERS_ACTION: String = "/GetMhhtUsers"
val OPERATION_NAME: String = "GetMhhtUsers"

val XML_MhhtApplicationIdentifier : String = "IkqhgbC+NyZHpu3ardXVQOPsg6Ge0zxKHcs5wYCUwM8Sv7HjXg/9KD3pHyI4q4ltRhI9LVafBK8JwXGNX5KkfoYcDKSuyxbqxt41s1eAOVVMUTfTgBdsBpfDhZ/hmY+RZS3B6yqT9tMIdiJdUeHhr7FARC56hwr1iq4iMyJJSVXWG07YaZ1zKvW/w3vHGYkj8pS38HuIzC9zDv7GL9v95SIdBm8jqVTrHZUazoo3z33ZRyRpfsMiNtwWj2Q0QbxvLL98wRkmT40dYlBsUR/LTA=="

val TST_STR : String = "<MhhtApplicationIdentifier HardwareId=\"1\" SoftwareId=\"1\" />"

private fun testExecuteWebmethod(){

    //val soapH = SoapHelper()
    //soapH.Test()

    compressBuffer(TST_STR.toByteArray(Charsets.UTF_8))

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


/*
//val ESP_SOAP_ADDRESS: String = "https://esp.higtek.com/TransportWebService.asmx"
val ESP_SOAP_ADDRESS: String = "http://192.168.1.120:83/TransportWebService.asmx"
val ESP_OPERATION_NAME: String = "GetMHHTUsersForWorkStation"
val ESP_GET_USERS_ACTION: String = "/GetMHHTUsersForWorkStation"



private fun testEspExecuteWebmethod(){

    //val soapH = SoapHelper()
    //soapH.Test()

    //compressBuffer(TST_STR.toByteArray(Charsets.UTF_8))

    var result = ""
    //val SOAP_ACTION = Utils.SOAP_NAMESPACE + methodName
    val soapObject = SoapObject(WSDL_TARGET_NAMESPACE, ESP_OPERATION_NAME)


    //soapObject.addProperty("xml", XML_MhhtApplicationIdentifier)
    //soapObject.addProperty("login", "samarin")
    //soapObject.addProperty("password", "samarin")
    soapObject.addProperty("erpCode", "")

    val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
    envelope.setOutputSoapObject(soapObject)
    envelope.dotNet = true

    val httpTransportSE = HttpTransportSE(ESP_SOAP_ADDRESS)

    try {
        //httpTransportSE.call(SOAP_ACTION, envelope)
        httpTransportSE.call(ESP_GET_USERS_ACTION, envelope)
        val soapPrimitive = envelope.response
        result = soapPrimitive.toString()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    var res = result;


    //val encodedString: String = Base64.getEncoder().encodeToString(originalString.toByteArray())
    //val decodedString: String = String(Base64.getDecoder().decode(result))
    val decoded: ByteArray = Base64.getDecoder().decode(result)

    res = decompressBuffer(decoded)


    val xmlDeserializer = XmlMapper(JacksonXmlModule().apply {
        setDefaultUseWrapper(false)
    }).registerKotlinModule()
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    //var desObj = xmlDeserializer.readValue(res, MHHTUser[]::class.java)

    val genres = xmlDeserializer.readValue<List<MHHTUser>>(res)

    var hash0 = genres[0].PasswordHash

    var hash1 = calculateMd5Hash("123")

    if(hash0 == hash1){
        var i = 0
    }

    //return result
}
*/
