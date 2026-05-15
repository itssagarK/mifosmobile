package org.mifos.mobile.features.group.save.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mifos.mobile.features.group.save.R
import org.mifos.mobile.features.group.save.domain.model.SavingsTransaction
import org.mifos.mobile.features.group.save.domain.model.SyncStatus
import org.mifos.mobile.features.group.save.presentation.ui.components.TransactionItem
import org.mifos.mobile.features.group.save.presentation.ui.components.OperationalSectionHeader
import org.mifos.mobile.features.group.save.presentation.viewmodel.SyncStatusUiState
import org.mifos.mobile.features.group.save.presentation.viewmodel.SyncStatusViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SyncStatusScreen(
    viewModel: SyncStatusViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.sync_status_screen_title), fontWeight = FontWeight.Black, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!uiState.isSyncing) {
                        IconButton(onClick = viewModel::syncNow) {
                            Icon(Icons.Default.Sync, contentDescription = "Sync Now")
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (uiState.isSyncing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            }
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    ConnectivityStatusHeader(
                        isOnline = uiState.isOnline,
                        isSyncing = uiState.isSyncing
                    )
                }

                item {
                    SyncSummarySection(
                        lastSyncTimestamp = uiState.lastSyncTimestamp,
                        pendingCount = uiState.pendingQueue.size,
                        failedCount = uiState.failedOperations.size
                    )
                }

                // Pending Queue Section
                stickyHeader {
                    OperationalSectionHeader(
                        title = stringResource(R.string.pending_queue_section).uppercase(),
                        count = uiState.pendingQueue.size,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                if (uiState.pendingQueue.isEmpty()) {
                    item { EmptySectionPlaceholder(stringResource(R.string.no_pending_items)) }
                } else {
                    items(uiState.pendingQueue) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onClick = { },
                            showSyncStatus = true
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }

                // Failed Operations Section
                stickyHeader {
                    OperationalSectionHeader(
                        title = stringResource(R.string.failed_ops_section).uppercase(),
                        count = uiState.failedOperations.size,
                        color = MaterialTheme.colorScheme.error,
                        action = {
                            if (uiState.failedOperations.isNotEmpty()) {
                                TextButton(onClick = viewModel::syncNow, contentPadding = PaddingValues(0.dp)) {
                                    Text(stringResource(R.string.retry_failed_btn), fontSize = 12.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    )
                }

                if (uiState.failedOperations.isEmpty()) {
                    item { EmptySectionPlaceholder(stringResource(R.string.no_failed_items)) }
                } else {
                    items(uiState.failedOperations) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onClick = { viewModel.retryFailed(transaction) },
                            showSyncStatus = true
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }

                // Synced Operations Section
                stickyHeader {
                    OperationalSectionHeader(
                        title = stringResource(R.string.synced_ops_section).uppercase(),
                        count = uiState.syncedOperations.size,
                        color = Color(0xFF4CAF50)
                    )
                }

                if (uiState.syncedOperations.isEmpty()) {
                    item { EmptySectionPlaceholder(stringResource(R.string.no_synced_items)) }
                } else {
                    items(uiState.syncedOperations) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onClick = { },
                            showSyncStatus = true
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectivityStatusHeader(isOnline: Boolean, isSyncing: Boolean) {
    val backgroundColor = if (isOnline) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val contentColor = if (isOnline) Color(0xFF2E7D32) else Color(0xFFC62828)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (isSyncing) "SYNCHRONIZING..." else if (isOnline) "SYSTEM ONLINE - BI-DIRECTIONAL SYNC ACTIVE" else "SYSTEM OFFLINE - OPERATIONS QUEUED LOCALLY",
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun SyncSummarySection(
    lastSyncTimestamp: Long,
    pendingCount: Int,
    failedCount: Int
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SyncMetricMini(
                label = "QUEUED",
                value = pendingCount.toString(),
                color = MaterialTheme.colorScheme.tertiary
            )
            SyncMetricMini(
                label = "FAILED",
                value = failedCount.toString(),
                color = MaterialTheme.colorScheme.error
            )
            SyncMetricMini(
                label = "LAST SYNC",
                value = if (lastSyncTimestamp > 0) {
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(lastSyncTimestamp))
                } else "NEVER",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SyncMetricMini(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun EmptySectionPlaceholder(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
