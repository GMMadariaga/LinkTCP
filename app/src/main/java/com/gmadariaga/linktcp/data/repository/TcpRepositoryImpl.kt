package com.gmadariaga.linktcp.data.repository

import com.gmadariaga.linktcp.data.source.TcpDataSource
import com.gmadariaga.linktcp.domain.model.ConnectionState
import com.gmadariaga.linktcp.domain.model.Message
import com.gmadariaga.linktcp.domain.repository.TcpRepository
import kotlinx.coroutines.flow.Flow

class TcpRepositoryImpl(
    private val dataSource: TcpDataSource
) : TcpRepository {

    override val connectionState: Flow<ConnectionState> = dataSource.connectionState
    override val incomingMessages: Flow<Message> = dataSource.incomingMessages
    override val logs: Flow<String> = dataSource.logs

    override fun getLocalIpAddress(): String = dataSource.getLocalIpAddress()

    override suspend fun connectAsClient(host: String, port: Int) {
        dataSource.connectAsClient(host, port)
    }

    override suspend fun startServer(port: Int) {
        dataSource.startServer(port)
    }

    override suspend fun sendMessage(message: String) {
        dataSource.sendMessage(message)
    }

    override suspend fun disconnect() {
        dataSource.disconnect()
    }
}
