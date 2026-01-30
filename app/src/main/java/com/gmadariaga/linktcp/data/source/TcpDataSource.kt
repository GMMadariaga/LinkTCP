package com.gmadariaga.linktcp.data.source

import com.gmadariaga.linktcp.domain.model.ConnectionState
import com.gmadariaga.linktcp.domain.model.Message
import com.gmadariaga.linktcp.domain.model.MessageDirection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

class TcpDataSource {
    private var socket: Socket? = null
    private var serverSocket: ServerSocket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null
    private var connectionJob: Job? = null

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Idle)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _incomingMessages = MutableSharedFlow<Message>()
    val incomingMessages: SharedFlow<Message> = _incomingMessages

    private val _logs = MutableSharedFlow<String>()
    val logs: SharedFlow<String> = _logs

    fun getLocalIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                if (networkInterface.isLoopback || !networkInterface.isUp) continue

                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (address is Inet4Address && !address.isLoopbackAddress) {
                        return address.hostAddress ?: "Unknown"
                    }
                }
            }
        } catch (e: Exception) {
            return "Error: ${e.message}"
        }
        return "No network"
    }

    suspend fun connectAsClient(host: String, port: Int) = withContext(Dispatchers.IO) {
        try {
            _logs.emit("Connecting to $host:$port...")
            _connectionState.value = ConnectionState.Connecting
            socket = Socket(host, port)
            setupStreams()
            _logs.emit("Connected to $host:$port")
            _connectionState.value = ConnectionState.Connected("$host:$port")
            startReading()
        } catch (e: Exception) {
            val errorMsg = e.message ?: "Connection failed"
            _logs.emit("Error: $errorMsg")
            _connectionState.value = ConnectionState.Error(errorMsg)
            cleanup()
        }
    }

    suspend fun startServer(port: Int) = withContext(Dispatchers.IO) {
        try {
            val localIp = getLocalIpAddress()
            _logs.emit("Starting server on $localIp:$port...")
            _connectionState.value = ConnectionState.Listening
            serverSocket = ServerSocket(port)
            serverSocket?.soTimeout = 0 // No timeout, but we can close it to cancel
            _logs.emit("Listening on $localIp:$port - Waiting for connection...")

            socket = serverSocket?.accept()
            val remoteAddress = socket?.remoteSocketAddress?.toString()?.removePrefix("/") ?: "unknown"
            setupStreams()
            _logs.emit("Client connected from $remoteAddress")
            _connectionState.value = ConnectionState.Connected(remoteAddress)
            startReading()
        } catch (e: SocketException) {
            if (_connectionState.value is ConnectionState.Listening) {
                _logs.emit("Server stopped")
                _connectionState.value = ConnectionState.Idle
            }
            cleanup()
        } catch (e: Exception) {
            val errorMsg = e.message ?: "Server failed"
            _logs.emit("Error: $errorMsg")
            _connectionState.value = ConnectionState.Error(errorMsg)
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
            while (isActive && socket?.isConnected == true && !socket!!.isClosed) {
                val line = reader?.readLine()
                if (line != null) {
                    _incomingMessages.emit(
                        Message(
                            content = line,
                            direction = MessageDirection.RECEIVED
                        )
                    )
                } else {
                    _logs.emit("Remote disconnected")
                    break
                }
            }
        } catch (e: SocketException) {
            if (_connectionState.value is ConnectionState.Connected) {
                _logs.emit("Connection closed")
            }
        } catch (e: Exception) {
            if (_connectionState.value is ConnectionState.Connected) {
                _logs.emit("Error: ${e.message}")
                _connectionState.value = ConnectionState.Error("Connection lost")
            }
        } finally {
            if (_connectionState.value is ConnectionState.Connected) {
                _connectionState.value = ConnectionState.Idle
            }
            cleanup()
        }
    }

    suspend fun sendMessage(message: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            writer?.println(message)
            _logs.emit("Sent: $message")
            true
        } catch (e: Exception) {
            _logs.emit("Send failed: ${e.message}")
            false
        }
    }

    suspend fun disconnect() = withContext(Dispatchers.IO) {
        _logs.emit("Disconnecting...")
        cleanup()
        _connectionState.value = ConnectionState.Idle
    }

    private fun cleanup() {
        try {
            serverSocket?.close()
            socket?.close()
            writer?.close()
            reader?.close()
        } catch (_: Exception) {
        }
        writer = null
        reader = null
        socket = null
        serverSocket = null
    }
}
