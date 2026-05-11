# Savings Transaction Feature Module - Mifos Mobile

This module provides a production-quality implementation of a Savings Transaction history feature for the Mifos Mobile application.

## Features

*   **Offline-First:** Uses Room for local caching and Single Source of Truth.
*   **Real-time Sync:** Observes local database changes and provides a seamless UI experience.
*   **Balance Summary:** Multi-tier dashboard showing Main Account Balance, Available Funds, and Pending Sync totals.
*   **Manual Synchronization:** "Sync All" action to trigger batch processing of locally created transactions.
*   **Transaction Entry:** Floating Action Button (FAB) and dialog for adding transactions with automatic background sync simulation.
*   **Search & Filter:** Local search capability for transactions by description, type, or amount.
*   **Modern UI:** Built entirely with Jetpack Compose and Material 3.
*   **Clean Architecture:** Strictly follows Domain, Data, and Presentation layer separation.
*   **Dependency Injection:** Powered by Hilt for easy testing and modularity.

## Architecture

The module follows **Clean Architecture** principles:

### 1. Domain Layer
*   **Models:** `SavingsTransaction` - Pure Kotlin data classes representing the business logic.
*   **Repository Interface:** `SavingsTransactionRepository` - Defines the contract for data operations.

### 2. Data Layer
*   **Mappers:** Converts between DTOs (Network), Entities (Database), and Domain models.
*   **Local:** Room Entity and DAO for persistent storage.
*   **Remote:** Retrofit API service for network calls.
*   **Repository Implementation:** `SavingsTransactionRepositoryImpl` - Orchestrates the offline-first logic:
    *   Fetches from Network.
    *   Clears old local data (for the specific account).
    *   Inserts new data into Room.
    *   Exposes a `Flow` of local data to the UI.

### 3. Presentation Layer
*   **ViewModel:** `SavingsTransactionViewModel` - Manages UI state and business interactions using `StateFlow`.
*   **UI:** Jetpack Compose screens and components.

## Tech Stack

*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose
*   **Concurrency:** Coroutines & Flow
*   **Dependency Injection:** Hilt
*   **Local Database:** Room
*   **Networking:** Retrofit & OkHttp
*   **State Management:** StateFlow
*   **Testing:** JUnit 4, MockK, Turbine

## Getting Started

Since this is your first time, here is exactly what you need to do to see the project in action:

### 1. Install the Tools
To run this project, you need two main things on your computer:
1. **Java (JDK 17)**: Ensure you have this installed.
2. **Android Studio**: This is the IDE where Android apps are built. 
   * Download it here: [Android Studio Iguana+](https://developer.android.com/studio)
   * Install it and follow the setup wizard (it will install the Android SDK for you).

### 2. Open the Project
1. Open Android Studio.
2. Click **Open** and select the folder: `C:\Users\Home\Desktop\projects\mifos`.
3. Wait for the **Gradle Sync** to finish. You'll see a progress bar at the bottom. This downloads the libraries (like Jetpack Compose, Room, Hilt) that the app needs.

### 3. Run the App
To see the "Savings Transaction" screen:
1. In Android Studio, look at the top toolbar. You'll see a dropdown that says `app`.
2. Next to it, you'll see a **Device** dropdown. If you don't have a phone connected, click it and select **Device Manager** to create a **Virtual Device** (an emulator).
3. Click the green **Play** button (Run). The emulator will start, and the app will open!

### 4. Run the Tests
Since I've written Unit Tests to prove the logic is correct, you can run them even without an emulator:
1. Open the **Terminal** at the bottom of Android Studio.
2. Type this command and press Enter:
   ```bash
   ./gradlew :features:savings-transaction:test
   ```
3. If it says `BUILD SUCCESSFUL`, it means all the "math" and "logic" for the transactions (including search and offline sync) are working perfectly.

### 5. Where is the code?
If you want to look at what I built:
* **The Screen:** `features/savings-transaction/src/main/java/org/mifos/mobile/features/savings/transaction/presentation/ui/SavingsTransactionScreen.kt`
* **The Logic:** `features/savings-transaction/src/main/java/org/mifos/mobile/features/savings/transaction/presentation/viewmodel/SavingsTransactionViewModel.kt`
* **The Database:** `core/database/src/main/java/org/mifos/mobile/core/database/AppDatabase.kt`

## Future Improvements
*   Implement WorkManager for robust background synchronization.
*   Add more granular filters (by date, transaction type).
*   Add pagination (Paging 3) for large transaction histories.
