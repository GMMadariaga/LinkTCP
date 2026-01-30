package com.gmadariaga.linktcp.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gmadariaga.linktcp.domain.model.ConnectionState
import com.gmadariaga.linktcp.presentation.components.ConnectionCard
import com.gmadariaga.linktcp.presentation.components.MessageBubble
import com.gmadariaga.linktcp.presentation.components.MessageInput
import com.gmadariaga.linktcp.presentation.components.TrafficIndicator
import com.gmadariaga.linktcp.presentation.viewmodel.ConnectionViewModel

@Composable
fun HomeScreen(
    viewModel: ConnectionViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isConnected = uiState.connectionState is ConnectionState.Connected
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ConnectionCard(
                    config = uiState.config,
                    connectionState = uiState.connectionState,
                    localIp = uiState.localIp,
                    logs = uiState.logs,
                    onHostChange = viewModel::updateHost,
                    onPortChange = viewModel::updatePort,
                    onModeChange = viewModel::updateMode,
                    onConnect = viewModel::connect,
                    onDisconnect = viewModel::disconnect,
                    modifier = Modifier.padding(16.dp)
                )
            }

            item {
                AnimatedVisibility(
                    visible = isConnected || uiState.totalBytesSent > 0 || uiState.totalBytesReceived > 0,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    TrafficIndicator(
                        bytesSent = uiState.totalBytesSent,
                        bytesReceived = uiState.totalBytesReceived,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            if (uiState.messages.isNotEmpty()) {
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "Messages",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(uiState.messages, key = { it.timestamp }) { message ->
                    MessageBubble(
                        message = message,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isConnected,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            MessageInput(
                value = uiState.currentMessage,
                onValueChange = viewModel::updateCurrentMessage,
                onSend = viewModel::sendMessage,
                enabled = isConnected
            )
        }
    }
}
