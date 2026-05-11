package org.mifos.mobile.features.savings.transaction.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mifos.mobile.features.savings.transaction.R
import org.mifos.mobile.features.savings.transaction.domain.model.SavingsTransaction
import org.mifos.mobile.features.savings.transaction.domain.model.TransactionType

@Composable
fun BalanceSummary(
    transactions: List<SavingsTransaction>,
    monthlyGoal: Double,
    onGoalClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalBalance = transactions.sumOf { 
        if (it.transactionType == TransactionType.DEPOSIT || it.transactionType == TransactionType.INTEREST) {
            it.amount
        } else {
            -it.amount
        }
    }

    val currentSavings = transactions.filter { 
        it.transactionType == TransactionType.DEPOSIT || it.transactionType == TransactionType.INTEREST 
    }.sumOf { it.amount }
    
    val goalProgress = (currentSavings / monthlyGoal).coerceIn(0.0, 1.0).toFloat()

    val availableBalance = transactions.filter { !it.isPendingSync }.sumOf {
        if (it.transactionType == TransactionType.DEPOSIT || it.transactionType == TransactionType.INTEREST) {
            it.amount
        } else {
            -it.amount
        }
    }

    val pendingBalance = transactions.filter { it.isPendingSync }.sumOf {
        if (it.transactionType == TransactionType.DEPOSIT || it.transactionType == TransactionType.INTEREST) {
            it.amount
        } else {
            -it.amount
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.main_balance),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "$${String.format("%.2f", totalBalance)}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onGoalClick() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(R.string.savings_goal), style = MaterialTheme.typography.labelSmall)
                        Text(text = "${(goalProgress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = goalProgress,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
                    )
                    Text(
                        text = stringResource(R.string.target, String.format("%.0f", monthlyGoal)),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BalanceSubCard(
                label = stringResource(R.string.available),
                amount = availableBalance,
                modifier = Modifier.weight(1f)
            )
            BalanceSubCard(
                label = stringResource(R.string.pending_sync),
                amount = pendingBalance,
                amountColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BalanceSubCard(
    label: String,
    amount: Double,
    modifier: Modifier = Modifier,
    amountColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall)
            Text(
                text = "${if (amount >= 0) "+" else ""}$${String.format("%.2f", amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}
