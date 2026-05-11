# Mifos Mobile - Savings Transaction Module

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/android)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack_Compose-orange.svg)](https://developer.android.com/jetpack/compose)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean_Architecture-red.svg)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

A production-quality implementation of a **Savings Transaction** history feature for the Mifos Mobile application. This project demonstrates modern Android development practices, including offline-first architecture, multi-module structure, and reactive UI.

---

## 🚀 Features

- **Offline-First:** Seamless experience with Room local caching and Single Source of Truth (SSOT).
- **Real-time Sync:** Reactive UI updates powered by Room observation and Kotlin Flows.
- **Smart Dashboard:** Multi-tier balance summary (Main Balance, Available Funds, Pending Sync).
- **Transaction Entry:** Add transactions locally with automatic background sync simulation.
- **Manual Synchronization:** "Sync All" capability for batch processing of pending transactions.
- **Search & Filter:** Instantly search through transaction history by description, type, or amount.
- **Modern UI:** 100% Jetpack Compose using Material 3 design principles.

---

## 🏗️ Architecture

The project follows **Clean Architecture** principles and is divided into several modules to ensure scalability and maintainability.

### Module Breakdown
- `:app`: The host application and entry point.
- `:features:savings-transaction`: Self-contained feature module containing Domain, Data, and Presentation layers.
- `:core:database`: Shared Room database configuration and DAOs.
- `:core:network`: Centralized Retrofit and networking logic.
- `:core:common`: Reusable utilities, base classes, and generic UI states.

### Implementation Details
1.  **Domain Layer:** Business logic defined by pure Kotlin data classes and repository interfaces.
2.  **Data Layer:** Handles data orchestration between Network (Retrofit) and Local (Room) using mappers.
3.  **Presentation Layer:** State management via `ViewModel` using `StateFlow` and `combine` operators for reactive filtering.

---

## 🛠️ Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Local Database:** [Room](https://developer.android.com/training/data-storage/room)
- **Networking:** [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
- **Concurrency:** [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
- **Testing:** JUnit 4, MockK, Turbine

---

## 🏁 Getting Started

### Prerequisites
- **JDK 17**
- **Android Studio Iguana+** (or latest version)

### Installation
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/itssagarK/mifosmobile.git
    cd mifosmobile
    ```
2.  **Open in Android Studio:**
    - Select `File > Open` and navigate to the project directory.
    - Wait for Gradle sync to complete.

### Running the App
1.  Select the `app` configuration in the toolbar.
2.  Select your device or emulator.
3.  Click the **Run** (green play) button.

### Running Tests
To verify the business logic and repository implementation:
```bash
./gradlew :features:savings-transaction:test
```

---

## 📂 Project Structure Highlights
- **Screen UI:** `features/savings-transaction/src/main/java/.../presentation/ui/SavingsTransactionScreen.kt`
- **ViewModel:** `features/savings-transaction/src/main/java/.../presentation/viewmodel/SavingsTransactionViewModel.kt`
- **Repository:** `features/savings-transaction/src/main/java/.../data/repository/SavingsTransactionRepositoryImpl.kt`
- **Database:** `core/database/src/main/java/.../core/database/AppDatabase.kt`

---

## 🛣️ Roadmap
- [ ] **WorkManager:** Integrate for more robust background synchronization.
- [ ] **Paging 3:** Implement for efficient handling of large transaction datasets.
- [ ] **Advanced Filtering:** Add date-range and multi-category filters.
- [ ] **UI Polish:** Add micro-interactions and refined animations for transaction entries.

---

## 📜 License
This project is licensed under the Apache License 2.0.
