package com.gmadariaga.linktcp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gmadariaga.linktcp.domain.model.ConnectionConfig
import com.gmadariaga.linktcp.domain.model.ConnectionMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val HOST = stringPreferencesKey("host")
        val PORT = intPreferencesKey("port")
        val MODE = stringPreferencesKey("mode")
    }

    val config: Flow<ConnectionConfig> = context.dataStore.data.map { prefs ->
        ConnectionConfig(
            host = prefs[Keys.HOST] ?: "",
            port = prefs[Keys.PORT] ?: 8080,
            mode = prefs[Keys.MODE]?.let { ConnectionMode.valueOf(it) } ?: ConnectionMode.CLIENT
        )
    }

    suspend fun saveConfig(config: ConnectionConfig) {
        context.dataStore.edit { prefs ->
            prefs[Keys.HOST] = config.host
            prefs[Keys.PORT] = config.port
            prefs[Keys.MODE] = config.mode.name
        }
    }
}
