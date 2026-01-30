package com.gmadariaga.linktcp.domain.repository

import com.gmadariaga.linktcp.domain.model.ConnectionConfig
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val config: Flow<ConnectionConfig>
    suspend fun saveConfig(config: ConnectionConfig)
}
