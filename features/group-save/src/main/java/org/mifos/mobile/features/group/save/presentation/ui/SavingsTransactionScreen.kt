@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package org.mifos.mobile.features.group.save.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import kotlinx.coroutines.launch
import org.mifos.mobile.features.group.save.R
import org.mifos.mobile.core.common.UiState
import org.mifos.mobile.features.group.save.domain.model.SavingsTransaction
import org.mifos.mobile.features.group.save.domain.model.TransactionType
import org.mifos.mobile.features.group.save.domain.usecase.SortOrder
import org.mifos.mobile.features.group.save.presentation.ui.components.*
import org.mifos.mobile.features.group.save.presentation.viewmodel.SavingsTransactionViewModel
import org.mifos.mobile.features.group.save.presentation.viewmodel.TransactionListItem
import org.mifos.mobile.features.group.save.presentation.viewmodel.SyncState
import org.mifos.mobile.features.group.save.presentation.viewmodel.OperationalMetrics
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.lazy.rememberLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsTransactionScreen(
    viewModel: SavingsTransactionViewModel,
    onCollectionSheetClick: () -> Unit,
    onViewGroupsClick: () -> Unit,
    onSyncStatusClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val operationalMetrics by viewModel.operationalMetrics.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val pagedTransactions = viewModel.transactionsPaged.collectAsLazyPagingItems()
    
    val searchQuery by viewModel.searchQuery.collectAsState()
    val lastUpdated by viewModel.lastUpdated.collectAsState()
    val savingsGoal by viewModel.savingsGoal.collectAsState()
    val dateRange by viewModel.dateRange.collectAsState()
    val nonPagedTransactions by viewModel.transactions.collectAsState(initial = emptyList())
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showDateRangePicker by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<SavingsTransaction?>(null) }
    var transactionToDelete by remember { mutableStateOf<SavingsTransaction?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing = pagedTransactions.loadState.refresh is LoadState.Loading && lastUpdated != null

    val exportingMsg = stringResource(R.string.exporting)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val listState = rememberLazyListState()
    val isFabExpanded by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.savings_transactions), 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::syncAll) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync All")
                    }
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Newest First") },
                            onClick = { viewModel.onSortOrderChanged(SortOrder.DATE_DESC); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Oldest First") },
                            onClick = { viewModel.onSortOrderChanged(SortOrder.DATE_ASC); showSortMenu = false }
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_transaction))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search Bar - Simplified
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.search_placeholder), style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium
            )

            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refreshTransactions() },
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // COMPACT OPERATIONAL DASHBOARD
                    item {
                        CompactFieldDashboard(
                            metrics = operationalMetrics,
                            lastSyncTime = lastUpdated
                        )
                    }

                    // OFFLINE QUEUE STATUS - CRITICAL OPERATIONAL INFO
                    item {
                        OfflineQueueStatus(
                            syncState = syncState,
                            isOnline = isOnline,
                            onSyncClick = viewModel::syncAll,
                            onViewDetailsClick = onSyncStatusClick
                        )
                    }

                    // QUICK ACTIONS - UTILITARIAN GRID
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onCollectionSheetClick,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.open_collection_sheet), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = onViewGroupsClick,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.view_groups_btn), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Activity Label
                    item {
                        Text(
                            text = stringResource(R.string.activity_overview).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp),
                            letterSpacing = 1.sp
                        )
                    }

                    // MOCK OPERATIONAL ACTIVITY - FOR VISUAL COMPLETENESS
                    item {
                        OperationalActivityItem(
                            title = "MEETING STARTED: GREEN VALLEY GROUP",
                            subtitle = "Field Location: Sector 4B",
                            time = "10:30 AM",
                            icon = Icons.Default.PlayCircle,
                            tint = Color(0xFF2E7D32)
                        )
                        OperationalActivityItem(
                            title = "OFFLINE COLLECTIONS SAVED",
                            subtitle = "12 Member records queued for sync",
                            time = "09:45 AM",
                            icon = Icons.Default.SdStorage,
                            tint = Color(0xFFF57C00)
                        )
                        OperationalActivityItem(
                            title = "SYNC COMPLETED",
                            subtitle = "All pending operations uploaded",
                            time = "09:00 AM",
                            icon = Icons.Default.CloudDone,
                            tint = Color(0xFF1976D2)
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 8.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }

                    // Paged List
                    items(
                        count = pagedTransactions.itemCount,
                        key = pagedTransactions.itemKey { item ->
                            when (item) {
                                is TransactionListItem.TransactionItem -> item.transaction.id
                                is TransactionListItem.DateHeader -> item.date
                            }
                        },
                        contentType = pagedTransactions.itemContentType { it }
                    ) { index ->
                        val item = pagedTransactions[index]
                        when (item) {
                            is TransactionListItem.DateHeader -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = item.date,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                            is TransactionListItem.TransactionItem -> {
                                TransactionItem(
                                    transaction = item.transaction,
                                    onClick = { selectedTransaction = item.transaction },
                                    searchQuery = searchQuery
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                            null -> { /* Loading */ }
                        }
                    }

                    // Handle Paging Load States
                    pagedTransactions.apply {
                        when {
                            loadState.refresh is LoadState.Error -> {
                                item {
                                    ErrorScreen(
                                        message = (loadState.refresh as LoadState.Error).error.message ?: "Unknown error",
                                        onRetry = { retry() }
                                    )
                                }
                            }
                            loadState.append is LoadState.Loading -> {
                                item {
                                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDateRangePicker) {
        val dateRangePickerState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateDateRange(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis
                    )
                    showDateRangePicker = false
                }) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.updateDateRange(null, null)
                    showDateRangePicker = false
                }) {
                    Text("Clear")
                }
            }
        ) {
            DateRangePicker(state = dateRangePickerState, modifier = Modifier.weight(1f))
        }
    }

    if (showAddDialog) {
        AddTransactionDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { amount, type, description ->
                viewModel.addTransaction(amount, type, description)
                showAddDialog = false
            }
        )
    }

    if (showGoalDialog) {
        SetGoalDialog(
            currentGoal = savingsGoal,
            onDismiss = { showGoalDialog = false },
            onConfirm = { newGoal ->
                viewModel.updateSavingsGoal(newGoal)
                showGoalDialog = false
            }
        )
    }

    selectedTransaction?.let { transaction ->
        TransactionDetailDialog(
            transaction = transaction,
            onDismiss = { selectedTransaction = null },
            onDelete = {
                transactionToDelete = transaction
                selectedTransaction = null
            }
        )
    }

    transactionToDelete?.let { transaction ->
        DeleteConfirmationDialog(
            onDismiss = { transactionToDelete = null },
            onConfirm = {
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Transaction deleted",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.undoDelete()
                    }
                }
                viewModel.deleteTransaction(transaction)
                transactionToDelete = null
            }
        )
    }
}

@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double, TransactionType, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(TransactionType.DEPOSIT) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_transaction)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text(stringResource(R.string.amount)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${stringResource(R.string.type)}: ${selectedType.name}")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        TransactionType.entries.filter { it != TransactionType.UNKNOWN }.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    onConfirm(amountValue, selectedType, description)
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun SetGoalDialog(
    currentGoal: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var goal by remember { mutableStateOf(currentGoal.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.set_goal_title)) },
        text = {
            OutlinedTextField(
                value = goal,
                onValueChange = { goal = it },
                label = { Text(stringResource(R.string.goal_target_label)) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val newGoal = goal.toDoubleOrNull() ?: currentGoal
                    onConfirm(newGoal)
                }
            ) {
                Text(stringResource(R.string.set_goal))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Transaction") },
        text = { Text("Are you sure you want to delete this transaction? This action cannot be undone.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
