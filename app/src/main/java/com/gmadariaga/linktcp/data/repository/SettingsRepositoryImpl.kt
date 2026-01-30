package com.gmadariaga.linktcp.data.repository

import com.gmadariaga.linktcp.data.local.SettingsDataStore
import com.gmadariaga.linktcp.domain.model.ConnectionConfig
import com.gmadariaga.linktcp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val dataStore: SettingsDataStore
) : SettingsRepository {

    override val config: Flow<ConnectionConfig> = dataStore.config

    override suspend fun saveConfig(config: ConnectionConfig) {
        dataStore.saveConfig(config)
    }
}
