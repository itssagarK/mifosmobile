package org.mifos.mobile.features.savings.transaction.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.mifos.mobile.features.savings.transaction.domain.model.SavingsTransaction
import org.mifos.mobile.features.savings.transaction.domain.model.TransactionType
import java.util.*

@Composable
fun TrendChart(
    transactions: List<SavingsTransaction>,
    onBarClick: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val last7Days = (0..6).map { i ->
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -i)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }.reversed()

    val dailyTotals = last7Days.map { date ->
        transactions.filter { 
            val cal1 = Calendar.getInstance().apply { time = it.date }
            val cal2 = Calendar.getInstance().apply { time = date }
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        }.sumOf {
            val isPositive = it.transactionType == TransactionType.DEPOSIT || it.transactionType == TransactionType.INTEREST
            if (isPositive) it.amount else -it.amount
        }
    }

    val maxAbs = dailyTotals.map { kotlin.math.abs(it) }.maxOrNull()?.coerceAtLeast(100.0) ?: 100.0
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(28.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "Last 7 Days Trend",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            dailyTotals.forEachIndexed { index, total ->
                val dayLabel = Calendar.getInstance().apply { time = last7Days[index] }
                    .getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())?.take(1) ?: ""
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onBarClick(total) }
                ) {
                    val heightFactor = (kotlin.math.abs(total) / maxAbs).toFloat().coerceAtLeast(0.05f)
                    val color = if (total >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(heightFactor)
                            .width(24.dp)
                            .background(color, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                    )
                    
                    Text(
                        text = dayLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
