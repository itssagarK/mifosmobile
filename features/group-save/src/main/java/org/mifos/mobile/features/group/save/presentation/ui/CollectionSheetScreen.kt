package org.mifos.mobile.features.group.save.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mifos.mobile.features.group.save.R
import org.mifos.mobile.features.group.save.domain.model.MemberCollection
import org.mifos.mobile.features.group.save.presentation.ui.components.SyncBadge
import org.mifos.mobile.features.group.save.presentation.viewmodel.CollectionSheetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionSheetScreen(
    viewModel: CollectionSheetViewModel,
    onMenuClick: () -> Unit,
    onBack: () -> Unit
) {
    val collections by viewModel.collections.collectAsState()
    val totalCollected by viewModel.totalCollected.collectAsState()
    val processedCount by viewModel.membersProcessed.collectAsState()
    val pendingCount by viewModel.pendingCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.collection_sheet_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::retrySync) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync Now")
                    }
                }
            )
        },
        bottomBar = {
            CollectionSheetBottomBar(
                totalAmount = totalCollected,
                processed = processedCount,
                totalMembers = collections.size,
                onSubmit = viewModel::submitCollections
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Summary Banner
            if (pendingCount > 0) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CloudOff, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "$pendingCount records queued for local sync",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("MEMBER", modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Text("SAVINGS", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Text("LOAN", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Text("SYNC", modifier = Modifier.weight(0.7f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(collections) { member ->
                    CollectionRow(
                        member = member,
                        onUpdate = viewModel::updateCollection
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                }
                item { Spacer(Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
fun CollectionRow(
    member: MemberCollection,
    onUpdate: (Long, Double?, Double?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Member Info
        Column(modifier = Modifier.weight(1.5f)) {
            Text(member.memberName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black)
            Text("ID: ${member.memberId}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
        }

        // Savings Input
        Box(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
            CompactAmountInput(
                value = if (member.savingsDeposit > 0) member.savingsDeposit.toString() else "",
                onValueChange = { onUpdate(member.memberId, it.toDoubleOrNull() ?: 0.0, null) }
            )
        }

        // Loan Input
        Box(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
            CompactAmountInput(
                value = if (member.loanPayment > 0) member.loanPayment.toString() else "",
                onValueChange = { onUpdate(member.memberId, null, it.toDoubleOrNull() ?: 0.0) }
            )
        }

        // Sync Status
        Box(modifier = Modifier.weight(0.7f), contentAlignment = Alignment.CenterEnd) {
            SyncBadge(member.syncStatus)
        }
    }
}

@Composable
fun CompactAmountInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(vertical = 6.dp, horizontal = 4.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        textStyle = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Black
        ),
        decorationBox = { innerTextField ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                innerTextField()
            }
        }
    )
}

@Composable
fun CollectionSheetBottomBar(
    totalAmount: Double,
    processed: Int,
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
                        text = stringResource(R.string.total_collected),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$${String.format("%.2f", totalAmount)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.members_processed),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$processed / $totalMembers",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.CloudSync, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Sync Collections to Server")
            }
        }
    }
}

// BasicTextField replacement if needed or use OutlinedTextField for simpler implementation
@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    textStyle: androidx.compose.ui.text.TextStyle = androidx.compose.ui.text.TextStyle.Default,
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit = @Composable { it() }
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        textStyle = textStyle,
        decorationBox = decorationBox
    )
}
