package com.higtek.truckradarv2

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val SERVER_URL = stringPreferencesKey("server_url")
val SERVER_USERNAME = stringPreferencesKey("server_username")
val SERVER_PASSWORD = stringPreferencesKey("server_password")
val IS_MARKER_TRUCK_PHOTO_ENABLED = booleanPreferencesKey("is_marker_truck_photo_enabled")


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

    val serverUsername: Flow<String> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[SERVER_USERNAME] ?: "truckradar"
        }

    return serverUsername
}

suspend fun setServerUsername(context : Context, value : String){

    context.dataStore.edit { settings ->
        settings[SERVER_USERNAME] = value
    }
}


fun getServerPassword(context : Context) : Flow<String>{

    val serverPassword: Flow<String> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[SERVER_PASSWORD] ?: "truckradar"
        }

    return serverPassword
}

suspend fun setServerPassword(context : Context, value : String){

    context.dataStore.edit { settings ->
        settings[SERVER_PASSWORD] = value
    }
}


fun getIsMarkerTruckPhotoEnabled(context : Context) : Flow<Boolean>{

    val isMarkerTruckPhotoEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[IS_MARKER_TRUCK_PHOTO_ENABLED] ?: true
        }

    return isMarkerTruckPhotoEnabled
}

suspend fun setIsMarkerTruckPhotoEnabled(context : Context, value : Boolean){

    context.dataStore.edit { settings ->
        settings[IS_MARKER_TRUCK_PHOTO_ENABLED] = value
    }
}
