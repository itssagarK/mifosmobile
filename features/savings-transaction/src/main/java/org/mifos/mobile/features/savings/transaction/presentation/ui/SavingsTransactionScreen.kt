@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package org.mifos.mobile.features.savings.transaction.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.mifos.mobile.features.savings.transaction.R
import org.mifos.mobile.core.common.UiState
import org.mifos.mobile.features.savings.transaction.domain.model.SavingsTransaction
import org.mifos.mobile.features.savings.transaction.domain.model.TransactionType
import org.mifos.mobile.features.savings.transaction.domain.usecase.SortOrder
import org.mifos.mobile.features.savings.transaction.presentation.ui.components.*
import org.mifos.mobile.features.savings.transaction.presentation.viewmodel.SavingsTransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsTransactionScreen(
    viewModel: SavingsTransactionViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val lastUpdated by viewModel.lastUpdated.collectAsState()
    val savingsGoal by viewModel.savingsGoal.collectAsState()
    val dateRange by viewModel.dateRange.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showDateRangePicker by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<SavingsTransaction?>(null) }
    var transactionToDelete by remember { mutableStateOf<SavingsTransaction?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing = uiState is UiState.Loading && lastUpdated != null

    val exportingMsg = stringResource(R.string.exporting)
    val dailyNetMsg = stringResource(R.string.daily_net)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(stringResource(R.string.savings_transactions), style = MaterialTheme.typography.titleMedium)
                        lastUpdated?.let {
                            Text(
                                text = stringResource(R.string.last_updated, SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(it))),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showDateRangePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Date Range")
                    }
                    IconButton(onClick = { 
                        scope.launch { snackbarHostState.showSnackbar(exportingMsg) }
                    }) {
                        Icon(Icons.Default.Download, contentDescription = stringResource(R.string.export_csv))
                    }
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Date (Newest)") },
                            onClick = { viewModel.onSortOrderChanged(SortOrder.DATE_DESC); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Date (Oldest)") },
                            onClick = { viewModel.onSortOrderChanged(SortOrder.DATE_ASC); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Amount (Highest)") },
                            onClick = { viewModel.onSortOrderChanged(SortOrder.AMOUNT_DESC); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Amount (Lowest)") },
                            onClick = { viewModel.onSortOrderChanged(SortOrder.AMOUNT_ASC); showSortMenu = false }
                        )
                    }
                    TextButton(onClick = viewModel::syncAll) {
                        Text(stringResource(R.string.sync_all))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_transaction))
            }
        },
        modifier = modifier
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clear_search))
                        }
                    }
                },
                singleLine = true
            )

            val selectedType by viewModel.selectedType.collectAsState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL", "DEPOSIT", "WITHDRAWAL", "INTEREST", "FEES").forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { viewModel.onTypeFilterChanged(type) },
                        label = { Text(type.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            if (dateRange.first != null || dateRange.second != null) {
                val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
                val rangeText = "${dateRange.first?.let { sdf.format(Date(it)) } ?: "..." } - ${dateRange.second?.let { sdf.format(Date(it)) } ?: "..."}"
                InputChip(
                    selected = true,
                    onClick = { viewModel.updateDateRange(null, null) },
                    label = { Text(rangeText) },
                    trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            when (val state = uiState) {
                is UiState.Loading -> if (!isRefreshing) LoadingScreen()
                is UiState.Success -> {
                    val groupedTransactions = state.data.groupBy {
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it.date)
                    }
                    
                    PullToRefreshBox(
                        state = pullToRefreshState,
                        isRefreshing = isRefreshing,
                        onRefresh = { viewModel.refreshTransactions() },
                        modifier = Modifier.weight(1f)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                BalanceSummary(
                                    transactions = state.data,
                                    monthlyGoal = savingsGoal,
                                    onGoalClick = { showGoalDialog = true }
                                )
                            }
                            item {
                                TrendChart(
                                    transactions = state.data,
                                    onBarClick = { amount ->
                                        scope.launch { snackbarHostState.showSnackbar("$dailyNetMsg $${String.format("%.2f", amount)}") }
                                    }
                                )
                            }
                            item {
                                StatisticsGrid(transactions = state.data)
                            }
                            
                            groupedTransactions.entries.forEachIndexed { index, (date, transactions) ->
                                stickyHeader {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.surface
                                    ) {
                                        Text(
                                            text = date,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                                items(transactions, key = { it.id }) { transaction ->
                                    val swipeState = rememberSwipeToDismissBoxState(
                                        confirmValueChange = { value ->
                                            if (value == SwipeToDismissBoxValue.EndToStart) {
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
                                                true
                                            } else false
                                        }
                                    )

                                    var visible by remember { mutableStateOf(false) }
                                    LaunchedEffect(Unit) { visible = true }

                                    Column {
                                        AnimatedVisibility(
                                            visible = visible,
                                            enter = slideInHorizontally(animationSpec = tween(durationMillis = 300, delayMillis = index * 50)) + fadeIn()
                                        ) {
                                            SwipeToDismissBox(
                                            state = swipeState,
                                            enableDismissFromStartToEnd = false,
                                            backgroundContent = {
                                                val color = when (swipeState.dismissDirection) {
                                                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                                                    else -> Color.Transparent
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(color)
                                                        .padding(horizontal = 24.dp),
                                                    contentAlignment = Alignment.CenterEnd
                                                ) {
                                                    Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription = "Delete",
                                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                                    )
                                                }
                                            }
                                        ) {
                                            TransactionItem(
                                                transaction = transaction,
                                                onClick = { selectedTransaction = transaction },
                                                searchQuery = searchQuery
                                            )
                                        }
                                    }
                                    } // Close Column
                                }
                            }
                        }
                    }
                }
                is UiState.Error -> ErrorScreen(
                    message = state.message,
                    onRetry = viewModel::retry
                )
                is UiState.Empty -> EmptyScreen(onAddClick = { showAddDialog = true })
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
