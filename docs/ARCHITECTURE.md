# Architecture Deep Dive: Savings Transaction Module

This document explains the architectural decisions made for the Savings Transaction module.

## Module Structure

The project is structured into multiple modules to promote scalability and separation of concerns:

- `:app`: The host application.
- `:core:network`: Shared networking logic (Retrofit setup).
- `:core:database`: Shared database logic (Room setup).
- `:core:common`: Shared utilities and generic models (e.g., `UiState`).
- `:features:savings-transaction`: The self-contained feature module.

## Offline-First Strategy

The module implements a "Single Source of Truth" pattern using Room.

1.  **Observation:** The UI observes a `Flow<List<SavingsTransaction>>` provided by the Repository, which internally observes the Room DAO.
2.  **Fetching:** When the user enters the screen or pulls to refresh, the Repository triggers a network fetch.
3.  **Synchronization:**
    - Remote data is mapped to local entities.
    - Local data that matches the current scope and is already synced is deleted and replaced with fresh remote data.
    - Pending (locally created but not yet synced) data is preserved.
4.  **Reaction:** Room automatically updates the `Flow`, and the UI reacts immediately to the new data.

## State Management with StateFlow

We use a combination of `StateFlow` and `combine` operator in the ViewModel to manage complex states efficiently.

```kotlin
val uiState: StateFlow<UiState<List<SavingsTransaction>>> = combine(
    repository.getTransactions(accountId),
    searchQuery
) { transactions, query ->
    // Filtering logic...
    if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)
}.stateIn(...)
```

This approach ensures that:
- Any change in the database triggers a UI update.
- Any change in the search query triggers a UI update.
- The state is always consistent and easily testable.

## Dependency Injection

Hilt is used to manage dependencies across modules. Each module has its own `di` package and Hilt Module.

- `DatabaseModule`: Provides `AppDatabase` and `SavingsTransactionDao`.
- `NetworkModule`: Provides `Retrofit` and `OkHttpClient`.
- `SavingsTransactionModule`: Provides `SavingsTransactionApi` and binds the repository implementation.

## UI Components

The UI is built with Jetpack Compose using a component-based approach:

- `SavingsTransactionScreen`: The main entry point, handles state switching and dialog visibility.
- `BalanceSummary`: A sophisticated header component providing a financial overview (Main, Available, Pending).
- `TransactionItem`: A reusable card for displaying individual transaction details.
- `StateComposables`: Generic components for Loading, Error, and Empty states, ensuring a consistent UX across the app.

## Manual & Background Sync

The architecture supports both manual and background synchronization:
- **Manual Sync:** Triggered via the "Sync All" action in the UI, which calls `repository.syncPendingTransactions()`.
- **Reactive Observation:** Any changes in the local database (from sync or manual entry) are automatically emitted via the `Flow` provided by the Room DAO, ensuring the UI is always up-to-date.
- **State Tracking:** The ViewModel tracks `lastUpdated` time to provide transparency into data freshness.
