package com.gmadariaga.linktcp.data.source

import com.gmadariaga.linktcp.domain.model.ConnectionState
import com.gmadariaga.linktcp.domain.model.Message
import com.gmadariaga.linktcp.domain.model.MessageDirection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class TcpDataSource {
    private var socket: Socket? = null
    private var serverSocket: ServerSocket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Idle)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _incomingMessages = MutableSharedFlow<Message>()
    val incomingMessages: SharedFlow<Message> = _incomingMessages

    suspend fun connectAsClient(host: String, port: Int) = withContext(Dispatchers.IO) {
        try {
            _connectionState.value = ConnectionState.Connecting
            socket = Socket(host, port)
            setupStreams()
            _connectionState.value = ConnectionState.Connected("$host:$port")
            startReading()
        } catch (e: Exception) {
            _connectionState.value = ConnectionState.Error(e.message ?: "Connection failed")
            cleanup()
        }
    }

    suspend fun startServer(port: Int) = withContext(Dispatchers.IO) {
        try {
            _connectionState.value = ConnectionState.Listening
            serverSocket = ServerSocket(port)
            socket = serverSocket?.accept()
            val remoteAddress = socket?.remoteSocketAddress?.toString() ?: "unknown"
            setupStreams()
            _connectionState.value = ConnectionState.Connected(remoteAddress)
            startReading()
        } catch (e: Exception) {
            _connectionState.value = ConnectionState.Error(e.message ?: "Server failed")
            cleanup()
        }
    }

    private fun setupStreams() {
        socket?.let {
            writer = PrintWriter(it.getOutputStream(), true)
            reader = BufferedReader(InputStreamReader(it.getInputStream()))
        }
    }

    private suspend fun startReading() = withContext(Dispatchers.IO) {
        try {
            while (isActive && socket?.isConnected == true) {
                val line = reader?.readLine()
                if (line != null) {
                    _incomingMessages.emit(
                        Message(
                            content = line,
                            direction = MessageDirection.RECEIVED
                        )
                    )
                } else {
                    break
                }
            }
        } catch (e: Exception) {
            if (_connectionState.value is ConnectionState.Connected) {
                _connectionState.value = ConnectionState.Error("Connection lost")
            }
        } finally {
            cleanup()
        }
    }

    suspend fun sendMessage(message: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            writer?.println(message)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun disconnect() = withContext(Dispatchers.IO) {
        cleanup()
        _connectionState.value = ConnectionState.Idle
    }

    private fun cleanup() {
        try {
            writer?.close()
            reader?.close()
            socket?.close()
            serverSocket?.close()
        } catch (_: Exception) {
        }
        writer = null
        reader = null
        socket = null
        serverSocket = null
    }
}
