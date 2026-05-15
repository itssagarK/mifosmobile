# Implementation Plan: Group Save & Collection Module

## Background & Motivation
The goal is to build a production-quality Android feature module for the Mifos Mobile application focused on "Group Save & Collections". It needs to follow offline-first principles, utilize modern Android development practices (Kotlin, MVVM, Jetpack Compose, Coroutines, Flow, Hilt, Room, Retrofit), and integrate seamlessly into a clean architecture. Since the current workspace is empty, we must also scaffold a minimal host app and core dependencies to demonstrate and build the module (yielding an APK).

## Scope & Impact
1.  **Project Scaffolding:** Create a minimal Android project setup (Gradle wrapper, build scripts, host `app` module, and `core` modules for network/database base).
2.  **Feature Module:** Create a `feature-group-save` module.
    *   **Domain Layer:** Use cases, Models, Repository interfaces.
    *   **Data Layer:** Room DAOs and Entities, Retrofit API services, Repository implementations with offline-first synchronization logic.
    *   **UI Layer:** Jetpack Compose screens, ViewModels using `StateFlow`, handling loading/empty/error states.
3.  **Specific Features:**
    *   Display collection history for groups.
    *   Offline caching via Room.
    *   Sync pending collections on network reconnect.
    *   Search and filter capabilities for member contributions.
    *   Retry mechanisms for failed network calls.
4.  **Testing:** Unit tests for ViewModels and Repositories.

## Proposed Solution & Architecture
We will use a multi-module Clean Architecture approach:
*   `app`: The entry point, containing the Application class and Hilt setup.
*   `core-network`: Retrofit setup, interceptors.
*   `core-database`: Room database setup.
*   `feature-group-save`: The main deliverable.
    *   `data/`: Room entities, DAOs, API interfaces, RepositoryImpl.
    *   `domain/`: Models, Repository interfaces.
    *   `presentation/`: ViewModels, Compose UI, UI State models.

**State Management:**
We will use a generic `UiState<T>` sealed interface for the UI, exposed via `StateFlow` from the ViewModel.
The offline-first logic will follow the "Single Source of Truth" pattern:
1.  Fetch from Network -> Save to Room.
2.  Observe Room as a `Flow` -> Expose to UI.
3.  For pending (offline) collections: Save to Room with a `pending_sync` flag, start a WorkManager task (or observe network state) to sync when online.

## Implementation Steps

### Phase 1: Project Setup & Core Modules
*   Initialize Gradle wrapper.
*   Configure root `build.gradle.kts` and `settings.gradle.kts`.
*   Create `app` module with `Hilt` application class.
*   Create `core` modules for Retrofit and Room to simulate a real app environment.

### Phase 2: Domain & Data Layers (Feature Module)
*   Define `GroupCollection` domain model.
*   Create `GroupCollectionEntity` (Room) and `GroupCollectionDto` (Network).
*   Implement `GroupCollectionDao`.
*   Implement `GroupSaveApi` with Retrofit.
*   Create `GroupSaveRepository` that handles offline caching, fetching, and syncing.

### Phase 3: Presentation Layer (Feature Module)
*   Create `GroupSaveViewModel` managing the state.
*   Implement Material 3 Compose screens:
    *   List screen with search/filter.
    *   Item rows for member contributions.
    *   Loading, Empty, and Error composables.
*   Implement Retry logic.

### Phase 4: Testing & Polish
*   Write JUnit tests for the Repository and ViewModel.
*   Integrate the feature into the `app` module's navigation.
*   Build the APK.

## Verification
*   Verify successful compilation and APK generation.
*   Run unit tests.
*   Manual testing: Turn off network, record a member contribution, verify it saves locally, turn on network, verify it syncs.
