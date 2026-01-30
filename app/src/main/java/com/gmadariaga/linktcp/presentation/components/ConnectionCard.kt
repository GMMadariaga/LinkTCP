package com.gmadariaga.linktcp.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gmadariaga.linktcp.domain.model.ConnectionConfig
import com.gmadariaga.linktcp.domain.model.ConnectionMode
import com.gmadariaga.linktcp.domain.model.ConnectionState

@Composable
fun ConnectionCard(
    config: ConnectionConfig,
    connectionState: ConnectionState,
    localIp: String,
    logs: List<String>,
    onHostChange: (String) -> Unit,
    onPortChange: (String) -> Unit,
    onModeChange: (ConnectionMode) -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = connectionState is ConnectionState.Connected
    val isConnecting = connectionState is ConnectionState.Connecting
    val isListening = connectionState is ConnectionState.Listening
    val isWorking = isConnecting || isListening
    val canEdit = connectionState is ConnectionState.Idle || connectionState is ConnectionState.Error

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Connection",
                    style = MaterialTheme.typography.titleMedium
                )
                StatusIndicator(state = connectionState)
            }

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                ConnectionMode.entries.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = config.mode == mode,
                        onClick = { onModeChange(mode) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = ConnectionMode.entries.size
                        ),
                        enabled = canEdit
                    ) {
                        Text(mode.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
            }

            // Show local IP when in server mode
            AnimatedVisibility(visible = config.mode == ConnectionMode.SERVER) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Your IP Address",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = localIp,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            AnimatedVisibility(visible = config.mode == ConnectionMode.CLIENT) {
                OutlinedTextField(
                    value = config.host,
                    onValueChange = onHostChange,
                    label = { Text("Host") },
                    placeholder = { Text("192.168.1.1") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = canEdit
                )
            }

            OutlinedTextField(
                value = config.port.toString(),
                onValueChange = onPortChange,
                label = { Text("Port") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = canEdit
            )

            if (connectionState is ConnectionState.Error) {
                Text(
                    text = connectionState.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Main action button
            if (isWorking) {
                OutlinedButton(
                    onClick = onDisconnect,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isListening) "Stop Server" else "Cancel"
                    )
                }
            } else {
                FilledTonalButton(
                    onClick = if (isConnected) onDisconnect else onConnect,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isConnected) "Disconnect" else "Connect"
                    )
                }
            }

            // Logs section
            if (logs.isNotEmpty()) {
                HorizontalDivider()
                Text(
                    text = "Logs",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LogsView(logs = logs)
            }
        }
    }
}

@Composable
private fun LogsView(logs: List<String>) {
    val scrollState = rememberScrollState()

    LaunchedEffect(logs.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 120.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        logs.forEach { log ->
            Text(
                text = log,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = if (log.startsWith("Error")) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
