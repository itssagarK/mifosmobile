# Architecture Deep Dive: Group Save & Collection Module

This document explains the architectural decisions made for the Group Save and Collection module.

## Module Structure

The project is structured into multiple modules to promote scalability and separation of concerns:

- `:app`: The host application.
- `:core:network`: Shared networking logic (Retrofit setup).
- `:core:database`: Shared database logic (Room setup).
- `:core:common`: Shared utilities and generic models (e.g., `UiState`).
- `:features:group-save`: The self-contained feature module.

## Offline-First Strategy

The module implements a "Single Source of Truth" pattern using Room, specifically optimized for **connectivity-aware** field operations.

### Local-First Recording
Field officers should never be blocked by network state. Every operational action (attendance, contributions) is recorded locally first.

1.  **Observation:** The UI observes a `Flow<List<GroupCollection>>` provided by the Repository, which internally observes the Room DAO.
2.  **Fetching:** When the user enters the screen or pulls to refresh, the Repository triggers a network fetch.
3.  **Synchronization & Persistence:**
    - Remote data is mapped to local entities.
    - Local data that matches the current scope and is already synced is deleted and replaced with fresh remote data.
    - **Pending Sync Tracking:** Locally created records are flagged as `PENDING_SYNC` and are strictly preserved during remote fetches.
4.  **Reaction:** Room automatically updates the `Flow`, and the UI reacts immediately to the new data, ensuring no lag during field entry.

### Connectivity-Aware Workflows
The architecture utilizes a dedicated synchronization queue to handle intermittent connectivity.

*   **Offline Queue Visibility:** The UI provides transparency by displaying which records are still pending synchronization.
*   **Retry Mechanism:** Failed synchronization attempts (due to timeouts or server errors) are automatically retried using background workers or manual user triggers.

### Realistic Sync Flow
```
Offline Mode (Field Site)
→ Local Save (Instant Persistence)
→ Queue Transaction (Mark as PENDING_SYNC)
→ Connectivity Restored (Detect Network)
→ Retry Sync (Automatic/Manual Trigger)
→ Sync Success (Mark as SYNCED)
```

This strategy ensures that field operations are reliable, data is never lost, and the application remains responsive even in the most remote rural environments.

---

## State Management with StateFlow

We use a combination of `StateFlow` and `combine` operator in the ViewModel to manage complex states efficiently.

```kotlin
val uiState: StateFlow<UiState<List<GroupCollection>>> = combine(
    repository.getCollections(groupId),
    searchQuery
) { collections, query ->
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

- `DatabaseModule`: Provides `AppDatabase` and `GroupCollectionDao`.
- `NetworkModule`: Provides `Retrofit` and `OkHttpClient`.
- `GroupSaveModule`: Provides `GroupSaveApi` and binds the repository implementation.

## UI Components

The UI is built with Jetpack Compose using a component-based approach:

- `GroupSaveScreen`: The main entry point, handles state switching and dialog visibility.
- `CollectionSummary`: A sophisticated header component providing an operational overview (Group Balance, Available Funds, Pending Sync).
- `MemberContributionItem`: A reusable card for displaying individual member contribution details.
- `StateComposables`: Generic components for Loading, Error, and Empty states, ensuring a consistent UX across the app.

## Manual & Background Sync

The architecture supports both manual and background synchronization:
- **Manual Sync:** Triggered via the "Sync All" action in the UI, which calls `repository.syncPendingCollections()`.
- **Reactive Observation:** Any changes in the local database (from sync or manual entry) are automatically emitted via the `Flow` provided by the Room DAO, ensuring the UI is always up-to-date.
- **State Tracking:** The ViewModel tracks `lastUpdated` time to provide transparency into data freshness.

## Kotlin Multiplatform (KMP) Alignment

While currently an Android project, the architecture is designed to support the Mifos Mobile long-term KMP vision:

- **Logic Extraction:** Domain models and Use Cases are written in pure Kotlin, making them ready for extraction into a `shared` module.
- **Data Portability:** Repository interfaces define a platform-agnostic contract for data operations.
- **Modular Boundaries:** Strict module boundaries prevent UI-logic leakage, a prerequisite for KMP sharing.

This alignment ensures that the business logic built today for Android can be reused tomorrow for iOS with minimal friction.
