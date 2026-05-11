package org.mifos.mobile.features.savings.transaction.presentation.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mifos.mobile.features.savings.transaction.R
import org.mifos.mobile.features.savings.transaction.domain.model.SavingsTransaction
import org.mifos.mobile.features.savings.transaction.domain.model.TransactionType

@Composable
fun StatisticsGrid(
    transactions: List<SavingsTransaction>,
    modifier: Modifier = Modifier
) {
    if (transactions.isEmpty()) return

    val deposits = transactions.filter { it.transactionType == TransactionType.DEPOSIT }
    val withdrawals = transactions.filter { it.transactionType == TransactionType.WITHDRAWAL }
    
    val totalDeposits = deposits.sumOf { it.amount }
    val totalWithdrawals = withdrawals.sumOf { it.amount }
    val avgDeposit = if (deposits.isNotEmpty()) totalDeposits / deposits.size else 0.0
    val maxWithdrawal = withdrawals.maxOfOrNull { it.amount } ?: 0.0

    val stats = listOf(
        StatItem(stringResource(R.string.total_deposits), totalDeposits, Icons.Default.AccountBalanceWallet, Color(0xFF4CAF50)),
        StatItem(stringResource(R.string.total_withdrawals), totalWithdrawals, Icons.Default.Payments, Color(0xFFF44336)),
        StatItem("Average Deposit", avgDeposit, Icons.Default.VerticalAlignTop, Color(0xFF2196F3)),
        StatItem("Max Withdrawal", maxWithdrawal, Icons.Default.VerticalAlignBottom, Color(0xFFFF9800))
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(28.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, start = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.activity_overview),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${transactions.size} Txns",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard(stats[0], modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(12.dp))
            StatCard(stats[1], modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard(stats[2], modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(12.dp))
            StatCard(stats[3], modifier = Modifier.weight(1f))
        }
    }
}

private data class StatItem(
    val label: String,
    val amount: Double,
    val icon: ImageVector,
    val color: androidx.compose.ui.graphics.Color
)

@Composable
private fun StatCard(
    item: StatItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = item.color
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$${String.format("%.2f", item.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun Color(hex: Long): androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(hex or 0xFF000000L)
