package org.mifos.mobile.features.group.save.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mifos.mobile.features.group.save.R
import org.mifos.mobile.features.group.save.domain.model.SavingsTransaction
import org.mifos.mobile.features.group.save.domain.model.TransactionType

@Composable
fun OperationalBalanceSummary(
    transactions: List<SavingsTransaction>,
    collectionTarget: Double,
    onTargetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentCollections = transactions.filter { 
        it.transactionType == TransactionType.DEPOSIT || it.transactionType == TransactionType.INTEREST 
    }.sumOf { it.amount }
    
    val targetProgress = if (collectionTarget > 0) (currentCollections / collectionTarget).coerceIn(0.0, 1.0).toFloat() else 0f

    val localAvailable = transactions.filter { !it.isPendingSync }.sumOf {
        if (it.transactionType == TransactionType.DEPOSIT || it.transactionType == TransactionType.INTEREST) it.amount else -it.amount
    }

    val pendingSync = transactions.filter { it.isPendingSync }.sumOf {
        if (it.transactionType == TransactionType.DEPOSIT || it.transactionType == TransactionType.INTEREST) it.amount else -it.amount
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Main Operational Balance Card
        Surface(
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.main_balance),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "$${String.format("%.2f", localAvailable + pendingSync)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                
                // Progress towards collection target
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTargetClick() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.savings_goal),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${(targetProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = targetProgress,
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    Text(
                        text = stringResource(R.string.target, String.format("%.0f", collectionTarget)),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Sub-metrics for local vs pending
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SyncStatusIndicator(
                label = stringResource(R.string.available),
                amount = localAvailable,
                isPending = false,
                modifier = Modifier.weight(1f)
            )
            SyncStatusIndicator(
                label = stringResource(R.string.pending_sync),
                amount = pendingSync,
                isPending = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SyncStatusIndicator(
    label: String,
    amount: Double,
    isPending: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            if (isPending && amount > 0) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f) else Color.Transparent
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$${String.format("%.2f", amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isPending && amount > 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
