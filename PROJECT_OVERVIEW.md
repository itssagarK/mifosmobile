# Mifos Save Mobile App — Project Overview

## Table of Contents
1. [Project Introduction](#project-introduction)
2. [What This App Has](#what-this-app-has)
3. [How It Was Built](#how-it-was-built)
4. [Tech Stack](#tech-stack)
5. [Architecture](#architecture)
6. [Module Structure](#module-structure)
7. [Key Features Implemented](#key-features-implemented)
8. [Offline-First Strategy](#offline-first-strategy)
9. [Testing](#testing)
10. [Project Roadmap](#project-roadmap)
11. [Build & Run](#build--run)

---

## 1. Project Introduction

**Mifos Mobile** is an Android application designed to transform traditional finance dashboards into an **offline-first field operations platform** for Mifos/VSLA (Village Savings and Loan Association) field officers.

The app enables field officers to:
- Manage centers, groups, and members
- Collect savings and loan payments
- Track transactions offline
- Sync data when connectivity is restored

### Target Users
- Field officers working in rural areas with limited connectivity
- Microfinance institutions managing VSLA groups
- Community banking agents

---

## 2. What This App Has

### Current Features

| Feature | Description |
|---------|-------------|
| **Savings Transactions** | View transaction history with debit/credit entries |
| **Offline-First Storage** | Local Room database with Single Source of Truth pattern |
| **Background Sync** | WorkManager for automatic sync when online |
| **Manual Sync** | "Sync All" button to manually sync pending transactions |
| **Search & Filter** | Real-time search through transaction history |
| **Balance Summary** | Multi-tier financial overview (Main, Available, Pending) |
| **Transaction Entry** | Add new transactions locally |
| **Transaction Details** | View detailed transaction information in dialogs |
| **Statistics Dashboard** | Grid showing account statistics |

### Screens

1. **Savings Transaction Screen** — Main screen with transaction list, balance summary, and search
2. **Transaction Detail Dialog** — Modal for viewing/editing transaction details
3. **Statistics Grid** — Dashboard component showing account metrics

---

## 3. How It Was Built

The app follows **Clean Architecture** principles with strict separation of concerns:

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                    │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │   Screens   │  │  ViewModels │  │ Components  │    │
│  └─────────────┘  └─────────────┘  └─────────────┘    │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │   Models    │  │  Use Cases  │  │ Repositories│    │
│  │             │  │             │  │ (Interface) │    │
│  └─────────────┘  └─────────────┘  └─────────────┘    │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                       DATA LAYER                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │  Repository │  │   Network   │  │   Local DB  │    │
│  │  (Impl)    │  │    (API)    │  │   (Room)   │    │
│  └─────────────┘  └─────────────┘  └─────────────┘    │
└─────────────────────────────────────────────────────────┘
```

---

## 4. Tech Stack

### Framework & Language
| Technology | Version | Purpose |
|------------|---------|---------|
| **Kotlin** | 1.9.22 | Primary language |
| **Android SDK** | API 34 | Target SDK |
| **Android Gradle Plugin** | 8.3.2 | Build system |

### UI Layer
| Library | Version | Purpose |
|---------|---------|---------|
| **Jetpack Compose** | BOM 2024.09.02 | Modern declarative UI |
| **Material 3** | Latest via BOM | Design system |
| **Hilt Navigation** | 1.1.0 | Navigation with DI |

### Dependency Injection
| Library | Version | Purpose |
|---------|---------|---------|
| **Hilt** | 2.50 | Dependency injection |

### Data Layer
| Library | Version | Purpose |
|---------|---------|---------|
| **Room** | 2.6.1 | Local SQLite database |
| **Retrofit** | 2.9.0 | REST API client |
| **OkHttp** | 4.12.0 | HTTP client |
| **Paging 3** | 3.2.1 | Efficient list loading |
| **DataStore** | 1.0.0 | Preferences storage |

### Async & Background
| Library | Version | Purpose |
|---------|---------|---------|
| **Coroutines** | 1.7.3 | Asynchronous programming |
| **WorkManager** | 2.9.0 | Background sync |
| **Flow** | Built-in | Reactive data streams |

### Testing
| Library | Version | Purpose |
|---------|---------|---------|
| **JUnit 4** | 4.13.2 | Unit testing |
| **MockK** | (included via hilt) | Mocking |
| **Turbine** | (included via hilt) | Flow testing |
| **Espresso** | 3.5.1 | UI testing |

---

## 5. Architecture

### Design Patterns Used

1. **Clean Architecture** — Strict layer separation
2. **Repository Pattern** — Single source of truth
3. **MVVM** — ViewModel for UI state management
4. **Use Cases** — Business logic encapsulation
5. **Dependency Injection** — Hilt for loose coupling
6. **Single Source of Truth (SSOT)** — Room as primary data source

### State Management

The app uses **StateFlow** with the `combine` operator for reactive UI updates:

```kotlin
val uiState: StateFlow<UiState<List<SavingsTransaction>>> = combine(
    repository.getTransactions(accountId),
    searchQuery
) { transactions, query ->
    // Filtering logic
    if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)
}.stateIn(...)
```

### Offline-First Data Flow

```
1. UI observes Flow<List<T>> from Repository
2. Repository internally observes Room DAO
3. On refresh: Repository fetches from Network
4. Remote data mapped to local entities
5. Local data synced - pending data preserved
6. Room auto-emits new data via Flow
7. UI reacts immediately to changes
```

---

## 6. Module Structure

The project uses a **modular architecture** with shared modules:

```
mifosmobile/
├── app/                          # Host application
│   └── src/main/
│       ├── java/org/mifos/mobile/
│       │   ├── MainActivity.kt   # Entry point
│       │   └── MifosApplication.kt # Hilt app + WorkManager
│       └── res/
├── core/
│   ├── database/                # Room database
│   │   └── src/main/java/.../
│   │       ├── AppDatabase.kt
│   │       ├── DateConverter.kt
│   │       └── di/DatabaseModule.kt
│   ├── network/                 # Retrofit setup
│   │   └── src/main/java/.../
│   │       └── di/NetworkModule.kt
│   └── common/                  # Shared utilities
│       └── src/main/java/.../
│           └── UiState.kt
└── features/
    └── savings-transaction/     # Feature module
        └── src/main/java/.../
            ├── data/           # Data layer
            │   ├── local/      # Room entities & DAOs
            │   ├── remote/     # Retrofit API & DTOs
            │   ├── mapper/     # Data mappers
            │   ├── repository/ # Repository implementation
            │   └── worker/     # WorkManager workers
            ├── domain/         # Domain layer
            │   ├── model/      # Business models
            │   ├── repository/ # Repository interfaces
            │   └── usecase/    # Use cases
            ├── presentation/   # Presentation layer
            │   ├── ui/         # Compose screens & components
            │   └── viewmodel/  # ViewModels
            └── di/             # Hilt modules
```

### Module Dependencies

```
app
├── core:database
├── core:network
├── core:common
└── features:savings-transaction
    ├── core:database
    ├── core:network
    └── core:common
```

---

## 7. Key Features Implemented

### 7.1 Offline-First Storage
- **Room Database** with TypeConverters for dates
- **Single Source of Truth** pattern — local data always wins
- **Pending sync tracking** — transactions marked until synced

### 7.2 Background Synchronization
- **WorkManager** integration with Hilt
- **SyncTransactionsWorker** for batch processing
- **Automatic retry** on failure

### 7.3 Manual Sync
- "Sync All" button triggers `SyncPendingTransactionsUseCase`
- Processes all pending local transactions
- Updates UI reactively via Flow

### 7.4 Search & Filter
- Real-time search by description, type, or amount
- `combine` operator merges search query with data stream

### 7.5 Transaction Management
- Add new savings transactions locally
- Delete transactions (local and remote)
- Refresh transactions from server

---

## 8. Offline-First Strategy

The app implements a robust offline-first strategy:

### Data Flow Diagram

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   NETWORK    │────▶│  REPOSITORY  │────▶│     ROOM     │
│   (Remote)   │     │   (Mapper)   │     │   (Local)    │
└──────────────┘     └──────────────┘     └──────────────┘
       ▲                                        │
       │                                        ▼
       │                                  ┌──────────────┐
       └──────────────────────────────────│   UI (Flow)  │
            Sync Pending                  │  (Reactive) │
              Transactions                 └──────────────┘
```

### Sync Logic

1. **Fetch Remote**: `RefreshSavingsTransactionsUseCase`
2. **Map to Entity**: Convert DTOs to Room entities
3. **Preserve Pending**: Keep unsynced local transactions
4. **Replace Synced**: Delete old synced data, insert fresh
5. **Emit to UI**: Room Flow notifies observers

---

## 9. Testing

The project includes comprehensive tests:

### Unit Tests
- **Use Cases**: All 7 use cases have unit tests
- **Repository**: Implementation tests with mocked dependencies
- **ViewModel**: StateFlow and UI state tests

### Instrumented Tests
- **AndroidTest**: UI tests with Compose testing utilities

### Test Coverage

| Layer | Tests |
|-------|-------|
| Domain Use Cases | 7 test files |
| Repository | 1 test file |
| ViewModel | 1 test file |
| UI (AndroidTest) | 1 test file |

Run tests:
```bash
./gradlew :features:savings-transaction:test
```

---

## 10. Project Roadmap

The project follows a phased execution plan. See [EXECUTION_PLAN.md](./EXECUTION_PLAN.md) for details.

### Upcoming Phases

| Phase | Focus |
|-------|-------|
| Phase 1 | Field Officer Dashboard, Offline-First UI, Collection Sheets |
| Phase 2 | Center, Group, Member Management |
| Phase 3 | VSLA Meeting Workflows |
| Phase 4 | Architecture Diagrams, Sync Status Screen |
| Phase 5 | Figma Wireframes, Proposal Screenshots |
| Phase 6 | Documentation |

---

## 11. Build & Run

### Prerequisites
- **JDK 17**
- **Android Studio Iguana+**

### Build Debug APK
```bash
./gradlew assembleDebug
```

### Install on Device/Emulator
```bash
./gradlew installDebug
```

### Run Tests
```bash
./gradlew :features:savings-transaction:test
```

---

## Project Information

| Item | Value |
|------|-------|
| **Package Name** | org.mifos.mobile |
| **Min SDK** | API 24 (Android 7.0) |
| **Target SDK** | API 34 (Android 14) |
| **Compile SDK** | API 34 |
| **Build Tool** | Gradle 8.7 |
| **Kotlin** | 1.9.22 |

---

## License

This project is licensed under the Apache License 2.0.

---

*Last Updated: 2026-05-14*
*For detailed execution plan, see [EXECUTION_PLAN.md](./EXECUTION_PLAN.md)*