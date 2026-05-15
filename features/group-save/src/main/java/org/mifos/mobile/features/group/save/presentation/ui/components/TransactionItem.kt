package org.mifos.mobile.features.group.save.presentation.ui.components

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
import androidx.compose.ui.unit.sp
import org.mifos.mobile.features.group.save.domain.model.SyncStatus
import org.mifos.mobile.features.group.save.domain.model.SavingsTransaction
import org.mifos.mobile.features.group.save.domain.model.TransactionType
import org.mifos.mobile.features.group.save.R
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItem(
    transaction: SavingsTransaction,
    onClick: () -> Unit,
    searchQuery: String = "",
    showSyncStatus: Boolean = false,
    modifier: Modifier = Modifier
) {
    val icon = when (transaction.transactionType) {
        TransactionType.DEPOSIT -> Icons.Default.AccountBalanceWallet
        TransactionType.WITHDRAWAL -> Icons.Default.Payments
        TransactionType.INTEREST -> Icons.Default.TrendingUp
        TransactionType.FEES -> Icons.Default.ReceiptLong
        else -> Icons.Default.Info
    }

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Even Smaller Icon Box
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                val typeDisplay = when (transaction.transactionType) {
                    TransactionType.DEPOSIT -> "COLLECTION"
                    TransactionType.WITHDRAWAL -> "DISBURSEMENT"
                    TransactionType.INTEREST -> "INTEREST"
                    TransactionType.FEES -> "FEE"
                    else -> "OTHER"
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = typeDisplay,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SyncBadge(transaction.syncStatus)
                }
                
                Text(
                    text = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(transaction.date),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                val isPositive = transaction.transactionType == TransactionType.DEPOSIT || 
                                 transaction.transactionType == TransactionType.INTEREST
                Text(
                    text = "${if (isPositive) "+" else "-"}$${String.format("%.2f", transaction.amount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isPositive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun SyncBadge(status: SyncStatus) {
    if (status == SyncStatus.SYNCED) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(10.dp), tint = Color(0xFF4CAF50))
            Text(text = stringResource(R.string.sync_status_synced), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
        }
        return
    }

    val (text, backgroundColor, contentColor, icon) = when (status) {
        SyncStatus.SAVED_OFFLINE -> TransactionQuadruplet(stringResource(R.string.sync_status_saved_offline), MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, Icons.Default.Save)
        SyncStatus.PENDING_SYNC -> TransactionQuadruplet(stringResource(R.string.sync_status_pending_sync), Color(0xFFFF9800).copy(alpha = 0.2f), Color(0xFFFF9800), Icons.Default.Schedule)
        SyncStatus.SYNCING -> TransactionQuadruplet(stringResource(R.string.sync_status_syncing), MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer, Icons.Default.Sync)
        SyncStatus.FAILED -> TransactionQuadruplet(stringResource(R.string.sync_status_retry_failed), MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, Icons.Default.ErrorOutline)
        else -> TransactionQuadruplet("Unknown", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, Icons.Default.HelpOutline)
    }

    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
        modifier = Modifier.height(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (status == SyncStatus.SYNCING) {
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
                Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp).graphicsLayer { rotationZ = rotation })
            } else {
                Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp))
            }
            Text(text = text, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}

private data class TransactionQuadruplet<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
