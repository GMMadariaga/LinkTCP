package com.gmadariaga.linktcp.domain.repository

import com.gmadariaga.linktcp.domain.model.ConnectionState
import com.gmadariaga.linktcp.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface TcpRepository {
    val connectionState: Flow<ConnectionState>
    val incomingMessages: Flow<Message>

    suspend fun connectAsClient(host: String, port: Int)
    suspend fun startServer(port: Int)
    suspend fun sendMessage(message: String)
    suspend fun disconnect()
}
