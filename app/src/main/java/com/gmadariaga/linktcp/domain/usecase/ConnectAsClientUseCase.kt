package com.gmadariaga.linktcp.domain.usecase

import com.gmadariaga.linktcp.domain.repository.TcpRepository

class ConnectAsClientUseCase(
    private val tcpRepository: TcpRepository
) {
    suspend operator fun invoke(host: String, port: Int) {
        tcpRepository.connectAsClient(host, port)
    }
}
