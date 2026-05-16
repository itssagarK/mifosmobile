package org.mifos.mobile.features.group.save.presentation.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mifos.mobile.features.group.save.R
import org.mifos.mobile.features.group.save.presentation.ui.components.OperationalSectionHeader
import org.mifos.mobile.core.common.ui.theme.MifosMobileTheme

data class AccountActivityItem(
    val id: String,
    val title: String,
    val date: String,
    val amount: Double,
    val isCredit: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    account: LinkedAccount,
    activities: List<AccountActivityItem>,
    onBackClick: () -> Unit = {},
    onViewTransactionsClick: () -> Unit = {},
    onDelinkClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDelinkDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.account_details),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                AccountDetailsHeader(account = account)
            }

            item {
                AccountActions(
                    onViewTransactions = onViewTransactionsClick,
                    onDelink = { showDelinkDialog = true }
                )
            }

            item {
                OperationalSectionHeader(stringResource(R.string.recent_activity))
            }

            if (activities.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No recent activity",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(activities) { activity ->
                    ActivityRow(activity = activity)
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (showDelinkDialog) {
            DelinkConfirmationDialog(
                account = account,
                onDismiss = { showDelinkDialog = false },
                onConfirm = {
                    onDelinkClick()
                    showDelinkDialog = false
                    onBackClick() // Typically navigate back after delinking
                }
            )
        }
    }
}

@Composable
fun AccountDetailsHeader(
    account: LinkedAccount,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when(account.type) {
                            AccountType.SAVINGS -> "Primary Savings"
                            AccountType.LOAN -> "Personal Loan"
                            AccountType.SHARES -> "Global Shares"
                        }.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = account.accountNumber,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF2E7D32).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = account.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.available_balance).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$${String.format("%.2f", account.balance)}",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val typeLabel = when (account.type) {
                    AccountType.SAVINGS -> stringResource(R.string.savings_account)
                    AccountType.LOAN -> stringResource(R.string.loan_account)
                    AccountType.SHARES -> stringResource(R.string.shares_account)
                }
                InfoTag(label = "TYPE", value = typeLabel, icon = Icons.Default.Info)
                InfoTag(label = "SYNCED", value = "YES", icon = Icons.Default.CloudDone, iconColor = Color(0xFF2E7D32))
                InfoTag(label = "LINKED", value = "YES", icon = Icons.Default.Link, iconColor = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun InfoTag(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 9.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = iconColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AccountActions(
    onViewTransactions: () -> Unit,
    onDelink: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onViewTransactions,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.view_transactions), fontSize = 12.sp)
        }
        OutlinedButton(
            onClick = onDelink,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
        ) {
            Icon(Icons.Default.LinkOff, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.delink_account), fontSize = 12.sp)
        }
    }
}

@Composable
fun ActivityRow(activity: AccountActivityItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (activity.isCredit) Color(0xFF2E7D32).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (activity.isCredit) Icons.Default.Add else Icons.Default.Remove,
                    contentDescription = null,
                    tint = if (activity.isCredit) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = activity.date,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "${if (activity.isCredit) "+" else "-"}$${String.format("%.2f", activity.amount)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Black,
            color = if (activity.isCredit) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

@Preview(showBackground = true)
@Composable
fun AccountDetailsPreview() {
    val mockAccount = LinkedAccount(
        id = "1",
        type = AccountType.SAVINGS,
        accountNumber = "SA-9021-X",
        balance = 8500.00,
        status = "Active"
    )

    val mockActivities = listOf(
        AccountActivityItem("1", "Deposit from Pocket", "Today, 10:30 AM", 500.00, true),
        AccountActivityItem("2", "Monthly Interest", "Yesterday", 12.50, true),
        AccountActivityItem("3", "Transfer to Loan", "14 May 2026", 250.00, false),
        AccountActivityItem("4", "Maintenance Fee", "01 May 2026", 5.00, false)
    )

    MifosMobileTheme(darkTheme = true) {
        AccountDetailsScreen(
            account = mockAccount,
            activities = mockActivities
        )
    }
}
