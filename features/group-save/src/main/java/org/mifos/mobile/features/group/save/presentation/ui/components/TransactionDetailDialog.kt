package org.mifos.mobile.features.group.save.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mifos.mobile.features.group.save.R
import org.mifos.mobile.features.group.save.domain.model.SavingsTransaction
import org.mifos.mobile.features.group.save.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionDetailDialog(
    transaction: SavingsTransaction,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val isPositive = transaction.transactionType == TransactionType.DEPOSIT || 
                     transaction.transactionType == TransactionType.INTEREST
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.transaction_details)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow(stringResource(R.string.type), transaction.transactionType.name)
                DetailRow(
                    label = stringResource(R.string.amount),
                    value = "${if (isPositive) "+" else "-"}$${String.format("%.2f", transaction.amount)}",
                    valueColor = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                DetailRow(
                    label = "Date",
                    value = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault()).format(transaction.date)
                )
                DetailRow(stringResource(R.string.description), transaction.description ?: "N/A")
                DetailRow("Account ID", transaction.accountId.toString())
                DetailRow(
                    label = stringResource(R.string.status),
                    value = if (transaction.isPendingSync) stringResource(R.string.pending) else stringResource(R.string.synced),
                    valueColor = if (transaction.isPendingSync) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.delete))
            }
        }
    )
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
