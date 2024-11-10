package com.higtek.testBle

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class ApplicationPrefeneces {
}

val SERVER_URL = stringPreferencesKey("server_url")

fun getServerUrl(context : Context) : Flow<String>{

    val serverUrlFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[SERVER_URL] ?: "http://192.168.1.120:83/TransportWebService.asmx"
        }

    return serverUrlFlow
}


suspend fun setServerUrl(context : Context, value : String){

    context.dataStore.edit { settings ->
        settings[SERVER_URL] = value
    }
}
