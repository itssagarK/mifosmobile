package org.mifos.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.mifos.mobile.features.group.save.presentation.ui.*
import org.mifos.mobile.core.common.ui.theme.MifosMobileTheme
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosMobileTheme {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Mifos Group Save",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                            
                            NavigationItem("Field Dashboard", Icons.Default.Dashboard, currentScreen == Screen.Dashboard) {
                                currentScreen = Screen.Dashboard
                                scope.launch { drawerState.close() }
                            }
                            NavigationItem("Group Registry", Icons.Default.Groups, currentScreen == Screen.GroupList) {
                                currentScreen = Screen.GroupList
                                scope.launch { drawerState.close() }
                            }
                            NavigationItem("Collection Sheets", Icons.Default.Assignment, currentScreen == Screen.CollectionSheet) {
                                currentScreen = Screen.CollectionSheet
                                scope.launch { drawerState.close() }
                            }
                            NavigationItem("Meetings", Icons.Default.Event, currentScreen is Screen.MeetingWorkflow) {
                                // Default to a specific group or a meetings list if we had one
                                currentScreen = Screen.GroupList 
                                scope.launch { drawerState.close() }
                            }
                            NavigationItem("Sync Queue", Icons.Default.CloudSync, currentScreen == Screen.SyncStatus) {
                                currentScreen = Screen.SyncStatus
                                scope.launch { drawerState.close() }
                            }
                            NavigationItem("Linked Accounts", Icons.Default.AccountBalanceWallet, currentScreen == Screen.PocketDashboard) {
                                currentScreen = Screen.PocketDashboard
                                scope.launch { drawerState.close() }
                            }
                            
                            Spacer(Modifier.weight(1f))
                            
                            NavigationItem("Settings", Icons.Default.Settings, currentScreen == Screen.Settings) {
                                currentScreen = Screen.Settings
                                scope.launch { drawerState.close() }
                            }
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Crossfade(targetState = currentScreen, label = "navigation") { screen ->
                            when (screen) {
                                Screen.Dashboard -> {
                                    SavingsTransactionScreen(
                                        viewModel = hiltViewModel(),
                                        onCollectionSheetClick = { currentScreen = Screen.CollectionSheet },
                                        onViewGroupsClick = { currentScreen = Screen.GroupList },
                                        onSyncStatusClick = { currentScreen = Screen.SyncStatus },
                                        onLinkedAccountsClick = { currentScreen = Screen.PocketDashboard },
                                        onMenuClick = { scope.launch { drawerState.open() } }
                                    )
                                }
                                Screen.CollectionSheet -> {
                                    CollectionSheetScreen(
                                        viewModel = hiltViewModel(),
                                        onMenuClick = { scope.launch { drawerState.open() } },
                                        onBack = { currentScreen = Screen.Dashboard }
                                    )
                                }
                                Screen.GroupList -> {
                                    GroupListScreen(
                                        viewModel = hiltViewModel(),
                                        onGroupClick = { currentScreen = Screen.GroupDetail(it) },
                                        onStartMeeting = { currentScreen = Screen.MeetingWorkflow(it) },
                                        onOpenCollectionSheet = { currentScreen = Screen.CollectionSheet },
                                        onMenuClick = { scope.launch { drawerState.open() } },
                                        onBack = { currentScreen = Screen.Dashboard }
                                    )
                                }
                                is Screen.GroupDetail -> {
                                    GroupDetailScreen(
                                        viewModel = hiltViewModel(),
                                        onStartMeeting = { currentScreen = Screen.MeetingWorkflow(it) },
                                        onOpenCollectionSheet = { currentScreen = Screen.CollectionSheet },
                                        onAddContribution = { /* Show Add Contribution Dialog */ },
                                        onBack = { currentScreen = Screen.GroupList }
                                    )
                                }
                                is Screen.MeetingWorkflow -> {
                                    MeetingWorkflowScreen(
                                        viewModel = hiltViewModel(),
                                        onBack = { currentScreen = Screen.GroupList }
                                    )
                                }
                                Screen.SyncStatus -> {
                                    SyncStatusScreen(
                                        viewModel = hiltViewModel(),
                                        onBackClick = { currentScreen = Screen.Dashboard }
                                    )
                                }
                                Screen.Settings -> {
                                    SettingsScreen(onBack = { currentScreen = Screen.Dashboard })
                                }
                                Screen.PocketDashboard -> {
                                    // Using mock data for MVP prototype
                                    val mockSummary = remember { 
                                        PocketSummary(12450.50, 3, "Synced Just Now") 
                                    }
                                    val mockAccounts = remember {
                                        mutableStateListOf(
                                            LinkedAccount("1", AccountType.SAVINGS, "**** 4582", 8500.00, "Active"),
                                            LinkedAccount("2", AccountType.LOAN, "**** 1104", 3500.50, "Active"),
                                            LinkedAccount("3", AccountType.SHARES, "**** 9921", 450.00, "Pending")
                                        )
                                    }
                                    
                                    PocketDashboardScreen(
                                        summary = mockSummary,
                                        accounts = mockAccounts,
                                        onBackClick = { currentScreen = Screen.Dashboard },
                                        onAccountDetailsClick = { currentScreen = Screen.AccountDetail(it) },
                                        onLinkAccount = { type, num -> 
                                            mockAccounts.add(LinkedAccount(UUID.randomUUID().toString(), type, num, 0.0, "Active"))
                                        },
                                        onDelinkAccount = { account ->
                                            mockAccounts.remove(account)
                                        }
                                    )
                                }
                                is Screen.AccountDetail -> {
                                    val mockActivities = remember {
                                        listOf(
                                            AccountActivityItem("1", "Deposit from Pocket", "Today, 10:30 AM", 500.00, true),
                                            AccountActivityItem("2", "Monthly Interest", "Yesterday", 12.50, true),
                                            AccountActivityItem("3", "Transfer to Loan", "14 May 2026", 250.00, false),
                                            AccountActivityItem("4", "Maintenance Fee", "01 May 2026", 5.00, false)
                                        )
                                    }
                                    AccountDetailsScreen(
                                        account = screen.account,
                                        activities = mockActivities,
                                        onBackClick = { currentScreen = Screen.PocketDashboard },
                                        onDelinkClick = { 
                                            // Handled via back navigation for MVP
                                            currentScreen = Screen.PocketDashboard 
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(label, fontWeight = if (selected) FontWeight.Bold else null) },
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null) },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

sealed class Screen {
    data object Dashboard : Screen()
    data object CollectionSheet : Screen()
    data object GroupList : Screen()
    data class GroupDetail(val groupId: Long) : Screen()
    data class MeetingWorkflow(val groupId: Long) : Screen()
    data object SyncStatus : Screen()
    data object Settings : Screen()
    data object PocketDashboard : Screen()
    data class AccountDetail(val account: LinkedAccount) : Screen()
}
