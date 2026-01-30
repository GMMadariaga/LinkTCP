package com.gmadariaga.linktcp.di

import android.content.Context
import com.gmadariaga.linktcp.data.local.SettingsDataStore
import com.gmadariaga.linktcp.data.repository.SettingsRepositoryImpl
import com.gmadariaga.linktcp.data.repository.TcpRepositoryImpl
import com.gmadariaga.linktcp.data.source.TcpDataSource
import com.gmadariaga.linktcp.domain.repository.SettingsRepository
import com.gmadariaga.linktcp.domain.repository.TcpRepository
import com.gmadariaga.linktcp.domain.usecase.ConnectAsClientUseCase
import com.gmadariaga.linktcp.domain.usecase.DisconnectUseCase
import com.gmadariaga.linktcp.domain.usecase.SendMessageUseCase
import com.gmadariaga.linktcp.domain.usecase.StartServerUseCase

object AppModule {
    private var tcpDataSource: TcpDataSource? = null
    private var tcpRepository: TcpRepository? = null
    private var settingsDataStore: SettingsDataStore? = null
    private var settingsRepository: SettingsRepository? = null

    fun provideTcpDataSource(): TcpDataSource {
        return tcpDataSource ?: TcpDataSource().also { tcpDataSource = it }
    }

    fun provideTcpRepository(): TcpRepository {
        return tcpRepository ?: TcpRepositoryImpl(provideTcpDataSource()).also { tcpRepository = it }
    }

    fun provideSettingsDataStore(context: Context): SettingsDataStore {
        return settingsDataStore ?: SettingsDataStore(context).also { settingsDataStore = it }
    }

    fun provideSettingsRepository(context: Context): SettingsRepository {
        return settingsRepository ?: SettingsRepositoryImpl(provideSettingsDataStore(context)).also { settingsRepository = it }
    }

    fun provideConnectAsClientUseCase(): ConnectAsClientUseCase {
        return ConnectAsClientUseCase(provideTcpRepository())
    }

    fun provideStartServerUseCase(): StartServerUseCase {
        return StartServerUseCase(provideTcpRepository())
    }

    fun provideSendMessageUseCase(): SendMessageUseCase {
        return SendMessageUseCase(provideTcpRepository())
    }

    fun provideDisconnectUseCase(): DisconnectUseCase {
        return DisconnectUseCase(provideTcpRepository())
    }
}
