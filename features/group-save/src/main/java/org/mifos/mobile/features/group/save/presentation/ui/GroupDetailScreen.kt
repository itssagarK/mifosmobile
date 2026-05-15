package org.mifos.mobile.features.group.save.presentation.ui

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mifos.mobile.core.common.UiState
import org.mifos.mobile.features.group.save.R
import org.mifos.mobile.features.group.save.domain.model.CollectionStatus
import org.mifos.mobile.features.group.save.domain.model.Group
import org.mifos.mobile.features.group.save.domain.model.MemberCollection
import org.mifos.mobile.features.group.save.presentation.ui.components.OperationalMetricMini
import org.mifos.mobile.features.group.save.presentation.ui.components.OperationalSectionHeader
import org.mifos.mobile.features.group.save.presentation.ui.components.SyncBadge
import org.mifos.mobile.features.group.save.presentation.viewmodel.GroupDetailViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    viewModel: GroupDetailViewModel,
    onStartMeeting: (Long) -> Unit,
    onOpenCollectionSheet: (Long) -> Unit,
    onAddContribution: (Long) -> Unit,
    onBack: () -> Unit
) {
    val groupState by viewModel.groupState.collectAsState()
    val membersState by viewModel.membersState.collectAsState()
    val progress by viewModel.collectionProgress.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group Operational Detail", fontWeight = FontWeight.Black, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::retrySync) {
                        Icon(Icons.Default.Sync, contentDescription = "Retry Sync")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = groupState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            }
            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is UiState.Success -> {
                val group = state.data
                LazyColumn(
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        GroupOperationalHeader(
                            group = group,
                            progress = progress
                        )
                    }

                    item {
                        OperationalActionsRow(
                            onStartMeeting = { onStartMeeting(group.id) },
                            onOpenCollectionSheet = { onOpenCollectionSheet(group.id) }
                        )
                    }

                    item {
                        OperationalSectionHeader("MEMBER CONTRIBUTION STATUS")
                    }

                    when (val mState = membersState) {
                        is UiState.Success -> {
                            items(mState.data) { member ->
                                MemberContributionRow(member)
                            }
                        }
                        is UiState.Empty -> {
                            item {
                                Text(
                                    "No member entries found", 
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        else -> {}
                    }

                    item {
                        OperationalSectionHeader("COLLECTION MEETING HISTORY")
                    }

                    item {
                        MeetingActivityItem(
                            date = "12 May 2026",
                            status = "COMPLETED",
                            collected = "$450.00"
                        )
                        MeetingActivityItem(
                            date = "05 May 2026",
                            status = "COMPLETED",
                            collected = "$420.00"
                        )
                    }

                    item {
                        OperationalSectionHeader("PENDING SYNC OPERATIONS")
                    }

                    item {
                        if (group.pendingSyncCount > 0) {
                            PendingOperationItem(
                                count = group.pendingSyncCount,
                                type = "Meeting Collection Records"
                            )
                        } else {
                            Text(
                                "ALL OPERATIONS SYNCHRONIZED",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun GroupOperationalHeader(
    group: Group,
    progress: Float
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = group.name.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Text(
                text = group.centerName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OperationalMetricMini("MEMBERS", group.memberCount.toString())
                OperationalMetricMini("QUEUED", group.pendingSyncCount.toString(), isHighlight = group.pendingSyncCount > 0)
                OperationalMetricMini("PROGRESS", "${(progress * 100).toInt()}%")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

@Composable
fun OperationalActionsRow(
    onStartMeeting: () -> Unit,
    onOpenCollectionSheet: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onStartMeeting,
            modifier = Modifier.weight(1f).height(40.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("MEETING", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
            onClick = onOpenCollectionSheet,
            modifier = Modifier.weight(1f).height(40.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("COLLECT", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MemberContributionRow(member: MemberCollection) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(member.memberName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text("ID: ${member.memberId}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        
        Column(horizontalAlignment = Alignment.End) {
            val total = member.savingsDeposit + member.loanPayment
            Text(
                text = if (total > 0) "$${String.format("%.2f", total)}" else "---",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Black,
                color = if (total > 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline
            )
            SyncBadge(member.syncStatus)
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

@Composable
fun MeetingActivityItem(date: String, status: String, collected: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(8.dp))
            Text(date, style = MaterialTheme.typography.bodySmall)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(status, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            Spacer(modifier = Modifier.width(12.dp))
            Text(collected, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Black)
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    )
}

@Composable
fun PendingOperationItem(count: Int, type: String) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CloudOff, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("$count RECORDS QUEUED", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.error)
                Text(type.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
            }
        }
    }
}
