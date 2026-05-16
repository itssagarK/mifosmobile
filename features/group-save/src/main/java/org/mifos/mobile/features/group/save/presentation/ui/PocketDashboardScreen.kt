package org.mifos.mobile.features.group.save.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import kotlinx.coroutines.launch
import org.mifos.mobile.features.group.save.R
import org.mifos.mobile.features.group.save.presentation.ui.components.OperationalSectionHeader
import org.mifos.mobile.core.common.ui.theme.MifosMobileTheme

enum class AccountType {
    SAVINGS, LOAN, SHARES
}

data class LinkedAccount(
    val id: String,
    val type: AccountType,
    val accountNumber: String,
    val balance: Double,
    val status: String
)

data class PocketSummary(
    val totalBalance: Double,
    val linkedAccountsCount: Int,
    val lastSyncStatus: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketDashboardScreen(
    summary: PocketSummary,
    accounts: List<LinkedAccount>,
    onBackClick: () -> Unit = {},
    onAccountDetailsClick: (LinkedAccount) -> Unit = {},
    onLinkAccount: (AccountType, String) -> Unit = { _, _ -> },
    onDelinkAccount: (LinkedAccount) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showLinkAccountSheet by remember { mutableStateOf(false) }
    var accountToDelink by remember { mutableStateOf<LinkedAccount?>(null) }
    
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.pocket_dashboard),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showLinkAccountSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.link_account)) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                PocketSummaryCard(summary = summary)
            }

            item {
                OperationalSectionHeader(
                    title = stringResource(R.string.total_linked_accounts),
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )
            }

            items(accounts) { account ->
                LinkedAccountCard(
                    account = account,
                    onClick = { onAccountDetailsClick(account) },
                    onDelinkClick = { accountToDelink = account }
                )
            }
        }

        if (showLinkAccountSheet) {
            LinkAccountBottomSheet(
                onDismiss = { showLinkAccountSheet = false },
                onConfirm = { type, accNum ->
                    onLinkAccount(type, accNum)
                    showLinkAccountSheet = false
                    scope.launch {
                        snackbarHostState.showSnackbar("Account $accNum linked successfully")
                    }
                },
                sheetState = sheetState
            )
        }

        accountToDelink?.let { account ->
            DelinkConfirmationDialog(
                account = account,
                onDismiss = { accountToDelink = null },
                onConfirm = {
                    onDelinkAccount(account)
                    accountToDelink = null
                    scope.launch {
                        snackbarHostState.showSnackbar("Account ${account.accountNumber} delinked")
                    }
                }
            )
        }
    }
}

@Composable
fun PocketSummaryCard(
    summary: PocketSummary,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.total_pocket_balance),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$${String.format("%.2f", summary.totalBalance)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryMetric(
                    label = stringResource(R.string.total_linked_accounts),
                    value = summary.linkedAccountsCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                SummaryMetric(
                    label = stringResource(R.string.last_sync_status),
                    value = summary.lastSyncStatus,
                    modifier = Modifier.weight(1f),
                    valueColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SummaryMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
fun LinkedAccountCard(
    account: LinkedAccount,
    onClick: () -> Unit,
    onDelinkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (account.type) {
        AccountType.SAVINGS -> Icons.Default.AccountBalanceWallet
        AccountType.LOAN -> Icons.Default.Payments
        AccountType.SHARES -> Icons.Default.PieChart
    }

    val typeLabel = when (account.type) {
        AccountType.SAVINGS -> stringResource(R.string.savings_account)
        AccountType.LOAN -> stringResource(R.string.loan_account)
        AccountType.SHARES -> stringResource(R.string.shares_account)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        onClick = onClick,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = typeLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = account.accountNumber,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val statusColor = if (account.status == "Active")
                        Color(0xFF2E7D32)
                    else
                        MaterialTheme.colorScheme.error

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = statusColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = account.status.uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = statusColor
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                var showMenu by remember { mutableStateOf(false) }
                
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", modifier = Modifier.size(20.dp))
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.view_details)) },
                            onClick = { onClick(); showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.delink_account), color = MaterialTheme.colorScheme.error) },
                            onClick = { onDelinkClick(); showMenu = false },
                            leadingIcon = { Icon(Icons.Default.LinkOff, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }
                
                Text(
                    text = "$${String.format("%.2f", account.balance)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkAccountBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (AccountType, String) -> Unit,
    sheetState: SheetState
) {
    var step by remember { mutableIntStateOf(1) }
    var selectedType by remember { mutableStateOf<AccountType?>(null) }
    var selectedAccount by remember { mutableStateOf<String?>(null) }

    // Mock available accounts
    val mockAvailableAccounts = mapOf(
        AccountType.SAVINGS to listOf("SA-9021-X", "SA-4412-Y"),
        AccountType.LOAN to listOf("LN-1120-A", "LN-8832-B"),
        AccountType.SHARES to listOf("SH-7721-P")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
        ) {
            Text(
                text = when(step) {
                    1 -> stringResource(R.string.select_account_type)
                    2 -> stringResource(R.string.available_accounts)
                    else -> stringResource(R.string.confirm_link)
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            AnimatedVisibility(visible = step == 1) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AccountTypeOption(
                        title = stringResource(R.string.savings_account),
                        icon = Icons.Default.AccountBalanceWallet,
                        selected = selectedType == AccountType.SAVINGS,
                        onClick = { selectedType = AccountType.SAVINGS; step = 2 }
                    )
                    AccountTypeOption(
                        title = stringResource(R.string.loan_account),
                        icon = Icons.Default.Payments,
                        selected = selectedType == AccountType.LOAN,
                        onClick = { selectedType = AccountType.LOAN; step = 2 }
                    )
                    AccountTypeOption(
                        title = stringResource(R.string.shares_account),
                        icon = Icons.Default.PieChart,
                        selected = selectedType == AccountType.SHARES,
                        onClick = { selectedType = AccountType.SHARES; step = 2 }
                    )
                }
            }

            AnimatedVisibility(visible = step == 2) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val accounts = mockAvailableAccounts[selectedType] ?: emptyList()
                    accounts.forEach { acc ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedAccount = acc; step = 3 },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (selectedAccount == acc) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                            color = if (selectedAccount == acc) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selectedAccount == acc, onClick = { selectedAccount = acc; step = 3 })
                                Spacer(Modifier.width(12.dp))
                                Text(acc, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    TextButton(onClick = { step = 1 }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Back to types")
                    }
                }
            }

            AnimatedVisibility(visible = step == 3) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    ) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(16.dp))
                            Text("Ready to link ${selectedAccount}?", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Text("Linking as ${selectedType?.name?.lowercase()?.capitalize()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    
                    Spacer(Modifier.height(32.dp))
                    
                    Button(
                        onClick = { onConfirm(selectedType!!, selectedAccount!!) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.confirm_link))
                    }
                    
                    TextButton(onClick = { step = 2 }) {
                        Text("Back to selection")
                    }
                }
            }
        }
    }
}

@Composable
fun AccountTypeOption(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
fun DelinkConfirmationDialog(
    account: LinkedAccount,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delink_confirmation_title)) },
        text = { Text(stringResource(R.string.delink_confirmation_desc)) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.delink_account))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PocketDashboardPreview() {
    val mockSummary = PocketSummary(
        totalBalance = 12450.50,
        linkedAccountsCount = 3,
        lastSyncStatus = "Synced Just Now"
    )
    
    val mockAccounts = listOf(
        LinkedAccount("1", AccountType.SAVINGS, "**** 4582", 8500.00, "Active"),
        LinkedAccount("2", AccountType.LOAN, "**** 1104", 3500.50, "Active"),
        LinkedAccount("3", AccountType.SHARES, "**** 9921", 450.00, "Pending")
    )

    MifosMobileTheme(darkTheme = true) {
        PocketDashboardScreen(
            summary = mockSummary,
            accounts = mockAccounts
        )
    }
}
