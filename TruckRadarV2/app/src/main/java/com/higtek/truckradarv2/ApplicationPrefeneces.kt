package com.higtek.truckradarv2

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val SERVER_URL = stringPreferencesKey("server_url")
val SERVER_USERNAME = stringPreferencesKey("server_username")
val SERVER_PASSWORD = stringPreferencesKey("server_password")


fun getServerUrl(context : Context) : Flow<String>{

    val serverUrlFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[SERVER_URL] ?: "http://gprs.higtek.in:89/"
        }

    return serverUrlFlow
}

suspend fun setServerUrl(context : Context, value : String){

    context.dataStore.edit { settings ->
        settings[SERVER_URL] = value
    }
}



fun getServerUsername(context : Context) : Flow<String>{

    val serverUrlFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[SERVER_USERNAME] ?: "truckradar"
        }

    return serverUrlFlow
}

suspend fun setServerUsername(context : Context, value : String){

    context.dataStore.edit { settings ->
        settings[SERVER_USERNAME] = value
    }
}


fun getServerPassword(context : Context) : Flow<String>{

    val serverUrlFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[SERVER_PASSWORD] ?: "truckradar"
        }

    return serverUrlFlow
}

suspend fun setServerPassword(context : Context, value : String){

    context.dataStore.edit { settings ->
        settings[SERVER_PASSWORD] = value
    }
}
