package com.gmadariaga.linktcp.domain.usecase

import com.gmadariaga.linktcp.domain.repository.TcpRepository

class DisconnectUseCase(
    private val tcpRepository: TcpRepository
) {
    suspend operator fun invoke() {
        tcpRepository.disconnect()
    }
}
