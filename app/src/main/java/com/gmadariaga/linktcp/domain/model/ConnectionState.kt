package com.gmadariaga.linktcp.domain.model

sealed class ConnectionState {
    data object Idle : ConnectionState()
    data object Connecting : ConnectionState()
    data object Listening : ConnectionState()
    data class Connected(val remoteAddress: String) : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}
