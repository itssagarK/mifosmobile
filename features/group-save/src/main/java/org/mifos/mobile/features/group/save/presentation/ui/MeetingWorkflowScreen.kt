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
import org.mifos.mobile.features.group.save.R
import org.mifos.mobile.features.group.save.domain.model.MeetingMember
import org.mifos.mobile.features.group.save.domain.model.MeetingStatus
import org.mifos.mobile.features.group.save.domain.model.MeetingWorkflow
import org.mifos.mobile.features.group.save.presentation.ui.components.OperationalSectionHeader
import org.mifos.mobile.features.group.save.presentation.ui.components.SyncBadge
import org.mifos.mobile.features.group.save.presentation.viewmodel.MeetingWorkflowViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingWorkflowScreen(
    viewModel: MeetingWorkflowViewModel,
    onBack: () -> Unit
) {
    val meetingState by viewModel.meetingState.collectAsState()
    val totalPresent by viewModel.totalMembersPresent.collectAsState()
    val totalContributions by viewModel.totalContributions.collectAsState()
    val pendingSync by viewModel.pendingSyncCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Group Meeting Workflow", fontWeight = FontWeight.Black, fontSize = 18.sp)
                        meetingState?.let {
                            Text(it.groupName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = viewModel::saveMeetingLocally) {
                        Text("Save Draft", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        bottomBar = {
            MeetingBottomBar(
                totalContributions = totalContributions,
                totalPresent = totalPresent,
                totalMembers = meetingState?.members?.size ?: 0,
                onSubmit = viewModel::submitAndSync
            )
        }
    ) { padding ->
        val meeting = meetingState
        if (meeting == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                // Meeting Header Info
                item {
                    MeetingStatusHeader(meeting, pendingSync)
                }

                // Attendance Section
                item {
                    OperationalSectionHeader("Member Attendance & Contributions")
                }

                items(meeting.members) { member ->
                    MeetingMemberRow(
                        member = member,
                        onAttendanceChange = { viewModel.updateAttendance(member.memberId, it) },
                        onContributionUpdate = { s, l -> viewModel.updateContribution(member.memberId, s, l) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                }

                // Notes Section
                item {
                    OperationalSectionHeader("Meeting Minutes / Notes")
                    OutlinedTextField(
                        value = meeting.meetingNotes,
                        onValueChange = viewModel::updateNotes,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(100.dp),
                        placeholder = { Text("Enter meeting decisions, resolutions, or observations...", style = MaterialTheme.typography.bodySmall) },
                        shape = RoundedCornerShape(8.dp),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }
                
                item { Spacer(Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
fun MeetingStatusHeader(meeting: MeetingWorkflow, pendingSync: Int) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(meeting.date).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (meeting.status == MeetingStatus.COMPLETED) Icons.Default.CheckCircle else Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(meeting.status.name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
                if (pendingSync > 0) {
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.width(4.dp))
                            Text("$pendingSync QUEUED", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MeetingMemberRow(
    member: MeetingMember,
    onAttendanceChange: (Boolean) -> Unit,
    onContributionUpdate: (Double?, Double?) -> Unit
) {
    var showContributionDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = member.isPresent,
            onCheckedChange = onAttendanceChange,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = member.memberName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (member.isPresent) FontWeight.Black else FontWeight.Normal,
                color = if (member.isPresent) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (member.savingsContribution > 0 || member.loanPayment > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "COLLECTED: $${String.format("%.2f", member.savingsContribution + member.loanPayment)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2E7D32),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    SyncBadge(member.syncStatus)
                }
            }
        }

        IconButton(onClick = { showContributionDialog = true }, modifier = Modifier.size(32.dp)) {
            Icon(
                Icons.Default.EditNote, 
                contentDescription = "Entry",
                modifier = Modifier.size(20.dp),
                tint = if (member.savingsContribution > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showContributionDialog) {
        ContributionEntryDialog(
            member = member,
            onDismiss = { showContributionDialog = false },
            onConfirm = { s, l ->
                onContributionUpdate(s, l)
                showContributionDialog = false
            }
        )
    }
}

@Composable
fun ContributionEntryDialog(
    member: MeetingMember,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double) -> Unit
) {
    var savings by remember { mutableStateOf(if (member.savingsContribution > 0) member.savingsContribution.toString() else "") }
    var loan by remember { mutableStateOf(if (member.loanPayment > 0) member.loanPayment.toString() else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Entry: ${member.memberName}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = savings,
                    onValueChange = { savings = it },
                    label = { Text("Savings Contribution") },
                    prefix = { Text("$") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = loan,
                    onValueChange = { loan = it },
                    label = { Text("Loan Repayment") },
                    prefix = { Text("$") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(savings.toDoubleOrNull() ?: 0.0, loan.toDoubleOrNull() ?: 0.0)
            }) {
                Text("Confirm Entry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MeetingBottomBar(
    totalContributions: Double,
    totalPresent: Int,
    totalMembers: Int,
    onSubmit: () -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Collected Today",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$${String.format("%.2f", totalContributions)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4CAF50)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Attendance",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$totalPresent / $totalMembers",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.DoneAll, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Complete & Sync Meeting")
            }
        }
    }
}
