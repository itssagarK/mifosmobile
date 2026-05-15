package org.mifos.mobile.features.group.save.presentation.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mifos.mobile.features.group.save.R
import org.mifos.mobile.features.group.save.presentation.viewmodel.SyncState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OfflineQueueStatus(
    syncState: SyncState,
    isOnline: Boolean,
    onSyncClick: () -> Unit,
    onViewDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine if we need to show an "Attention" state (pending items or error)
    val isAttentionNeeded = syncState is SyncState.PendingSync || syncState is SyncState.RetryFailed || syncState is SyncState.Syncing

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isAttentionNeeded) MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isAttentionNeeded) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        onClick = onViewDetailsClick
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isAttentionNeeded) Icons.Default.CloudSync else Icons.Default.CloudDone,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (isAttentionNeeded) MaterialTheme.colorScheme.tertiary else Color(0xFF4CAF50)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isAttentionNeeded) "LOCAL QUEUE ACTIVE" else "ALL DATA SYNCED",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = if (isAttentionNeeded) MaterialTheme.colorScheme.onTertiaryContainer else Color(0xFF2E7D32)
                        )
                        SyncStateLabel(syncState)
                    }
                }
                
                IconButton(
                    onClick = onSyncClick,
                    enabled = syncState !is SyncState.Syncing,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (syncState is SyncState.Syncing) Icons.Default.Refresh else Icons.Default.Sync,
                        contentDescription = "Sync",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Connection Status
                ConnectivityMiniLabel(isOnline = isOnline)

                // Last Sync Time
                val syncTimeText = when (syncState) {
                    is SyncState.SyncedSuccessfully -> syncState.lastSyncTime?.let {
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it))
                    } ?: "Never"
                    is SyncState.Syncing -> "Syncing..."
                    is SyncState.PendingSync -> "Pending"
                    is SyncState.RetryFailed -> "Failed"
                    else -> "Unknown"
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "LAST SYNC: $syncTimeText",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ConnectivityMiniLabel(isOnline: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(if (isOnline) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error, RoundedCornerShape(3.dp))
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = if (isOnline) "ONLINE" else "OFFLINE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isOnline) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun SyncStateLabel(syncState: SyncState) {
    val text = when (syncState) {
        is SyncState.Syncing -> "Uploading ${syncState.count} records..."
        is SyncState.RetryFailed -> "Sync failed for ${syncState.count} items"
        is SyncState.PendingSync -> "${syncState.count} records waiting for sync"
        is SyncState.SyncedSuccessfully -> "System is up to date"
        else -> "Disconnected"
    }
    
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
