package com.gmadariaga.linktcp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gmadariaga.linktcp.R
import com.gmadariaga.linktcp.ui.theme.StatusConnected
import com.gmadariaga.linktcp.ui.theme.StatusPending

@Composable
fun TrafficIndicator(
    bytesSent: Long,
    bytesReceived: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TrafficItem(
                icon = { Icon(Icons.AutoMirrored.Filled.CallMade, contentDescription = stringResource(R.string.sent), tint = StatusPending) },
                label = stringResource(R.string.sent),
                value = formatBytes(bytesSent)
            )
            TrafficItem(
                icon = { Icon(Icons.AutoMirrored.Filled.CallReceived, contentDescription = stringResource(R.string.received), tint = StatusConnected) },
                label = stringResource(R.string.received),
                value = formatBytes(bytesReceived)
            )
        }
    }
}

@Composable
private fun TrafficItem(
    icon: @Composable () -> Unit,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        icon()
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> String.format("%.1f KB", bytes / 1024.0)
        else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
    }
}
