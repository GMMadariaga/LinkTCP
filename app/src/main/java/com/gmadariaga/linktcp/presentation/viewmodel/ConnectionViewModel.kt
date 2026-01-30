package com.gmadariaga.linktcp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gmadariaga.linktcp.di.AppModule
import com.gmadariaga.linktcp.domain.model.ConnectionConfig
import com.gmadariaga.linktcp.domain.model.ConnectionMode
import com.gmadariaga.linktcp.domain.model.ConnectionState
import com.gmadariaga.linktcp.domain.model.Message
import com.gmadariaga.linktcp.domain.model.MessageDirection
import com.gmadariaga.linktcp.domain.repository.SettingsRepository
import com.gmadariaga.linktcp.domain.repository.TcpRepository
import com.gmadariaga.linktcp.domain.usecase.ConnectAsClientUseCase
import com.gmadariaga.linktcp.domain.usecase.DisconnectUseCase
import com.gmadariaga.linktcp.domain.usecase.SendMessageUseCase
import com.gmadariaga.linktcp.domain.usecase.StartServerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConnectionUiState(
    val config: ConnectionConfig = ConnectionConfig(),
    val connectionState: ConnectionState = ConnectionState.Idle,
    val messages: List<Message> = emptyList(),
    val currentMessage: String = "",
    val totalBytesSent: Long = 0,
    val totalBytesReceived: Long = 0,
    val localIp: String = "",
    val logs: List<String> = emptyList()
)

class ConnectionViewModel(
    private val tcpRepository: TcpRepository,
    private val settingsRepository: SettingsRepository,
    private val connectAsClientUseCase: ConnectAsClientUseCase,
    private val startServerUseCase: StartServerUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val disconnectUseCase: DisconnectUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectionUiState())
    val uiState: StateFlow<ConnectionUiState> = _uiState.asStateFlow()

    init {
        observeConnectionState()
        observeIncomingMessages()
        observeLogs()
        loadSavedConfig()
        loadLocalIp()
    }

    private fun observeConnectionState() {
        viewModelScope.launch {
            tcpRepository.connectionState.collect { state ->
                _uiState.update { it.copy(connectionState = state) }
            }
        }
    }

    private fun observeIncomingMessages() {
        viewModelScope.launch {
            tcpRepository.incomingMessages.collect { message ->
                _uiState.update { state ->
                    state.copy(
                        messages = state.messages + message,
                        totalBytesReceived = state.totalBytesReceived + message.sizeBytes
                    )
                }
            }
        }
    }

    private fun observeLogs() {
        viewModelScope.launch {
            tcpRepository.logs.collect { log ->
                _uiState.update { state ->
                    val newLogs = (state.logs + log).takeLast(50) // Keep last 50 logs
                    state.copy(logs = newLogs)
                }
            }
        }
    }

    private fun loadSavedConfig() {
        viewModelScope.launch {
            settingsRepository.config.collect { config ->
                _uiState.update { it.copy(config = config) }
            }
        }
    }

    private fun loadLocalIp() {
        _uiState.update { it.copy(localIp = tcpRepository.getLocalIpAddress()) }
    }

    fun refreshLocalIp() {
        loadLocalIp()
    }

    fun updateHost(host: String) {
        _uiState.update { it.copy(config = it.config.copy(host = host)) }
    }

    fun updatePort(port: String) {
        val portInt = port.toIntOrNull() ?: return
        if (portInt in 1..65535) {
            _uiState.update { it.copy(config = it.config.copy(port = portInt)) }
        }
    }

    fun updateMode(mode: ConnectionMode) {
        _uiState.update { it.copy(config = it.config.copy(mode = mode)) }
    }

    fun updateCurrentMessage(message: String) {
        _uiState.update { it.copy(currentMessage = message) }
    }

    fun connect() {
        viewModelScope.launch {
            val config = _uiState.value.config
            settingsRepository.saveConfig(config)
            refreshLocalIp()

            when (config.mode) {
                ConnectionMode.CLIENT -> connectAsClientUseCase(config.host, config.port)
                ConnectionMode.SERVER -> startServerUseCase(config.port)
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            disconnectUseCase()
        }
    }

    fun sendMessage() {
        val message = _uiState.value.currentMessage.trim()
        if (message.isEmpty()) return

        viewModelScope.launch {
            sendMessageUseCase(message)
            val sentMessage = Message(
                content = message,
                direction = MessageDirection.SENT
            )
            _uiState.update { state ->
                state.copy(
                    messages = state.messages + sentMessage,
                    currentMessage = "",
                    totalBytesSent = state.totalBytesSent + sentMessage.sizeBytes
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(messages = emptyList(), totalBytesSent = 0, totalBytesReceived = 0) }
    }

    fun clearLogs() {
        _uiState.update { it.copy(logs = emptyList()) }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ConnectionViewModel(
                tcpRepository = AppModule.provideTcpRepository(),
                settingsRepository = AppModule.provideSettingsRepository(context),
                connectAsClientUseCase = AppModule.provideConnectAsClientUseCase(),
                startServerUseCase = AppModule.provideStartServerUseCase(),
                sendMessageUseCase = AppModule.provideSendMessageUseCase(),
                disconnectUseCase = AppModule.provideDisconnectUseCase()
            ) as T
        }
    }
}
