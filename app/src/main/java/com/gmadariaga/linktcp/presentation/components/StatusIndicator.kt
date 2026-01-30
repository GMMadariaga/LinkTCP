package com.gmadariaga.linktcp.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gmadariaga.linktcp.R
import com.gmadariaga.linktcp.domain.model.ConnectionState
import com.gmadariaga.linktcp.ui.theme.StatusConnected
import com.gmadariaga.linktcp.ui.theme.StatusError
import com.gmadariaga.linktcp.ui.theme.StatusIdle
import com.gmadariaga.linktcp.ui.theme.StatusPending

@Composable
fun StatusIndicator(
    state: ConnectionState,
    modifier: Modifier = Modifier
) {
    val isPulsing = state is ConnectionState.Connecting || state is ConnectionState.Listening

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val color by animateColorAsState(
        targetValue = when (state) {
            is ConnectionState.Idle -> StatusIdle
            is ConnectionState.Connecting, is ConnectionState.Listening -> StatusPending
            is ConnectionState.Connected -> StatusConnected
            is ConnectionState.Error -> StatusError
        },
        animationSpec = tween(300),
        label = "statusColor"
    )

    val statusText = when (state) {
        is ConnectionState.Idle -> stringResource(R.string.status_idle)
        is ConnectionState.Connecting -> stringResource(R.string.status_connecting)
        is ConnectionState.Listening -> stringResource(R.string.status_listening)
        is ConnectionState.Connected -> stringResource(R.string.status_connected)
        is ConnectionState.Error -> stringResource(R.string.status_error)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .alpha(if (isPulsing) pulseAlpha else 1f)
                .background(color)
        )
        Text(
            text = statusText,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
