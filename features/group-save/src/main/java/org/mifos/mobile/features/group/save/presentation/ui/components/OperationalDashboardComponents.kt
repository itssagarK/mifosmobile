package org.mifos.mobile.features.group.save.presentation.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mifos.mobile.features.group.save.R
import org.mifos.mobile.features.group.save.presentation.viewmodel.OfflineStatus
import org.mifos.mobile.features.group.save.presentation.viewmodel.OperationalMetrics
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OfflineStatusBanner(status: OfflineStatus, lastSync: Long?) {
    val (backgroundColor, textColor, icon, label) = when (status) {
        is OfflineStatus.Online -> {
            val label = lastSync?.let { 
                stringResource(R.string.last_successful_sync, SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it)))
            } ?: stringResource(R.string.online_mode)
            DashboardQuadruplet(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer, Icons.Default.CloudDone, label)
        }
        is OfflineStatus.OfflineActive -> {
            DashboardQuadruplet(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, Icons.Default.CloudOff, stringResource(R.string.offline_mode_active))
        }
        is OfflineStatus.PendingSync -> {
            DashboardQuadruplet(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer, Icons.Default.Sync, stringResource(R.string.pending_sync_ops))
        }
    }

    Surface(
        color = backgroundColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.labelLarge, color = textColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OperationalMetricsGrid(metrics: OperationalMetrics) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.operational_metrics),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OperationalCard(
                title = stringResource(R.string.active_groups),
                value = metrics.activeGroups.toString(),
                icon = Icons.Default.Groups,
                modifier = Modifier.weight(1f)
            )
            OperationalCard(
                title = stringResource(R.string.today_meetings),
                value = metrics.todayMeetings.toString(),
                icon = Icons.Default.Event,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OperationalCard(
                title = stringResource(R.string.pending_collections),
                value = metrics.pendingCollections.toString(),
                icon = Icons.Default.Payments,
                modifier = Modifier.weight(1f)
            )
            OperationalCard(
                title = stringResource(R.string.offline_queue),
                value = metrics.offlineQueueCount.toString(),
                icon = Icons.Default.SdCard,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun OperationalCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun QuickActions(
    onCollectionSheet: () -> Unit,
    onStartMeeting: () -> Unit,
    onRetrySync: () -> Unit,
    onViewGroups: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Group Save Workflow",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionButton(
                label = stringResource(R.string.open_collection_sheet),
                icon = Icons.Default.Assignment,
                onClick = onCollectionSheet,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                label = stringResource(R.string.start_meeting),
                icon = Icons.Default.PlayArrow,
                onClick = onStartMeeting,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionButton(
                label = stringResource(R.string.retry_sync_btn),
                icon = Icons.Default.Replay,
                onClick = onRetrySync,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                label = stringResource(R.string.view_groups_btn),
                icon = Icons.Default.Visibility,
                onClick = onViewGroups,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

private data class DashboardQuadruplet<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
