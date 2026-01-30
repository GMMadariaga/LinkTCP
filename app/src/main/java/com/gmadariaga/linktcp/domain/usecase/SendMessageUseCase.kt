package com.gmadariaga.linktcp.domain.usecase

import com.gmadariaga.linktcp.domain.repository.TcpRepository

class SendMessageUseCase(
    private val tcpRepository: TcpRepository
) {
    suspend operator fun invoke(message: String) {
        tcpRepository.sendMessage(message)
    }
}
