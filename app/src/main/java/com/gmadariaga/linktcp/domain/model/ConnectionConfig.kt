package com.gmadariaga.linktcp.domain.model

data class ConnectionConfig(
    val host: String = "",
    val port: Int = 8080,
    val mode: ConnectionMode = ConnectionMode.CLIENT
)
