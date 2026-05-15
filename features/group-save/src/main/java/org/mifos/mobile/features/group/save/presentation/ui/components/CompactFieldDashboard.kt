package org.mifos.mobile.features.group.save.presentation.ui.components

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
import androidx.compose.ui.unit.sp
import org.mifos.mobile.features.group.save.R
import org.mifos.mobile.features.group.save.presentation.viewmodel.OperationalMetrics
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CompactFieldDashboard(
    metrics: OperationalMetrics,
    lastSyncTime: Long?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // High Density Operational Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OperationalMetricMini(
                    label = "ACTIVE GROUPS",
                    value = metrics.activeGroups.toString(),
                    icon = Icons.Default.Groups,
                    modifier = Modifier.weight(1f)
                )
                VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 4.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                OperationalMetricMini(
                    label = "MEETINGS TODAY",
                    value = metrics.todayMeetings.toString(),
                    icon = Icons.Default.Event,
                    modifier = Modifier.weight(1f)
                )
                VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 4.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                OperationalMetricMini(
                    label = "PENDING OPS",
                    value = metrics.pendingCollections.toString(),
                    icon = Icons.Default.Payments,
                    isHighlight = metrics.pendingCollections > 0,
                    modifier = Modifier.weight(1f)
                )
                VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 4.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                OperationalMetricMini(
                    label = "QUEUED SYNC",
                    value = metrics.offlineQueueCount.toString(),
                    icon = Icons.Default.CloudQueue,
                    isHighlight = metrics.offlineQueueCount > 0,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun OperationalMetricMini(
    label: String,
    value: String,
    icon: ImageVector,
    isHighlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if (isHighlight) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = if (isHighlight) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}
