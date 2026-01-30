package com.gmadariaga.linktcp.domain.usecase

import com.gmadariaga.linktcp.domain.repository.TcpRepository

class StartServerUseCase(
    private val tcpRepository: TcpRepository
) {
    suspend operator fun invoke(port: Int) {
        tcpRepository.startServer(port)
    }
}
