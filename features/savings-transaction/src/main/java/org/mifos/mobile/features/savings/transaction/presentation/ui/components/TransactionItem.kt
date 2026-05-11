package org.mifos.mobile.features.savings.transaction.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.mifos.mobile.features.savings.transaction.R
import org.mifos.mobile.features.savings.transaction.domain.model.SavingsTransaction
import org.mifos.mobile.features.savings.transaction.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItem(
    transaction: SavingsTransaction,
    onClick: () -> Unit,
    searchQuery: String = "",
    modifier: Modifier = Modifier
) {
    val icon = when (transaction.transactionType) {
        TransactionType.DEPOSIT -> Icons.Default.AccountBalanceWallet
        TransactionType.WITHDRAWAL -> Icons.Default.Payments
        TransactionType.INTEREST -> Icons.Default.TrendingUp
        TransactionType.FEES -> Icons.Default.ReceiptLong
        else -> Icons.Default.Info
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.transactionType.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(transaction.date),
                    style = MaterialTheme.typography.bodySmall
                )
                if (!transaction.description.isNullOrEmpty()) {
                    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
                    val highlightedDescription = remember(transaction.description, searchQuery, onSurfaceColor) {
                        buildAnnotatedString {
                            val text = transaction.description
                            val start = if (searchQuery.isEmpty()) -1 else text.indexOf(searchQuery, ignoreCase = true)
                            if (start != -1) {
                                append(text.substring(0, start))
                                withStyle(style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    background = Color.Yellow.copy(alpha = 0.3f),
                                    color = onSurfaceColor
                                )) {
                                    append(text.substring(start, start + searchQuery.length))
                                }
                                append(text.substring(start + searchQuery.length))
                            } else {
                                append(text)
                            }
                        }
                    }
                    Text(
                        text = highlightedDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                val isPositive = transaction.transactionType == TransactionType.DEPOSIT || 
                                 transaction.transactionType == TransactionType.INTEREST
                Text(
                    text = "${if (isPositive) "+" else "-"}$${String.format("%.2f", transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontWeight = FontWeight.Bold
                )
                if (transaction.isPendingSync) {
                    val infiniteTransition = rememberInfiniteTransition(label = "sync")
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1500, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "rotation"
                    )
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = stringResource(R.string.pending),
                        modifier = Modifier
                            .size(16.dp)
                            .graphicsLayer { rotationZ = rotation },
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
