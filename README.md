# Mifos Mobile - Group Save & Collection Module

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/android)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack_Compose-orange.svg)](https://developer.android.com/jetpack/compose)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean_Architecture-red.svg)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

An offline-first operational workflow platform for group-based financial services. This module is an MVP extension aligned with the **Mifos Group Save** direction, specifically designed to support field officer operations in rural and low-connectivity environments.

The project focuses on streamlining **Group Banking** and **VSLA (Village Savings and Loan Associations)** workflows, providing robust tools for managing **Centers**, **Groups**, and **Meeting-based** collection operations. It prioritizes reliable data entry via **Collection Sheets** even when offline, ensuring that field activities remain uninterrupted.

While currently implemented as a modular Android project, the architecture is designed for future **Kotlin Multiplatform (KMP)** alignment, enabling shared business logic across mobile platforms.

---

## 🌍 Group Banking Context

This module addresses the unique operational challenges of **Village Savings and Loan Associations (VSLA)** and group-based microfinance.

*   **VSLA Workflows:** Small, community-managed savings groups that require precise record-keeping for member contributions, loans, and social funds.
*   **Field Officer Operations:** Officers often visit remote locations with minimal or no internet connectivity. The ability to record data reliably while offline is a critical requirement, not a secondary feature.
*   **Meetings & Collection Sheets:** Operations are centered around periodic group meetings. The **Collection Sheet** is the primary tool used to record bulk data (attendance, savings, repayments) for all group members simultaneously.
*   **Operational Integrity:** Maintaining a local "Single Source of Truth" ensures that data entered in the field is never lost and is synchronized accurately once connectivity is restored.

---

---

## 🚀 Operational Features

- **Group Savings Operations:** Record and manage member contributions during group meetings with a focus on operational reliability.
- **Collection Sheet Workflow:** Optimized bulk entry system for recording attendance and contributions across an entire group in a single view.
- **Meeting-based Collections:** Support for structured meeting workflows, including attendance tracking and real-time operational progress.
- **Offline-first Persistence:** Robust local storage ensuring that field officers can continue all operations without an active internet connection.
- **Offline Queue Management:** Visibility into pending transactions with a dedicated synchronization queue for reliable data uploads.
- **Group & Member Management:** Maintain local records of Centers, Groups, and Members to facilitate field-based financial services.
- **Pending Sync Tracking:** Clear status indicators for all recorded data, providing transparency into what has been successfully uploaded to the server.
- **Modern Operational UI:** 100% Jetpack Compose using Material 3, designed for clarity and efficiency in high-pressure field environments.

---

## 📝 Collection Sheet Workflow

The **Collection Sheet** is the operational heart of the module, designed to facilitate rapid, accurate data entry during group meetings.

### Operational Mechanics
*   **Bulk Entry:** Officers can enter data for all members in a single, scrollable interface, minimizing navigation overhead.
*   **Meeting-based Operations:** Tracks attendance and records various transaction types (Savings, Loans, Social Funds) in the context of a specific meeting.
*   **Offline-first Persistence:** Every entry is saved immediately to the local Room database. If connectivity is unavailable, the data is queued for background synchronization.
*   **Sync Queue & Retry:** A dedicated management layer tracks the status of each entry. Failed syncs are automatically or manually retried based on connectivity availability.

### Realistic Field Workflow
1.  **Meeting Starts:** Field officer opens the Group Collection Sheet.
2.  **Attendance recorded:** Bulk toggle for all present members.
3.  **Contributions Entered:** Savings and loan repayments are recorded for each member.
4.  **Local Save:** Data is instantly persisted to the device storage.
5.  **Queueing:** Entries are marked as `PENDING_SYNC` and added to the synchronization queue.
6.  **Internet Restored:** The application detects connectivity and begins batch uploading the queued entries.
7.  **Sync Complete:** Records are updated to `SYNCED` state once the server confirms receipt.

This workflow is critical for field officers because it eliminates the need for paper-based double-entry and ensures data integrity in the challenging environments where Mifos operates.

---

## 👥 Meeting-based Operations

Beyond data entry, the module supports the structured flow of a physical group meeting.

### Operational Scope
*   **Attendance Tracking:** Record member presence as the first step of any meeting operation.
*   **Meeting Notes:** Capture qualitative data and observations during the field visit.
*   **Collection Progress:** Real-time visibility into how much of the expected group total has been collected.
*   **Member Contribution Collection:** Individual recording of savings, social funds, and loan installments.

### Operational Overview Example

| Member | Attendance | Savings | Loan Payment | Sync Status |
| :--- | :---: | :---: | :---: | :---: |
| John Doe | ✅ | 500.00 | 1200.00 | `SYNCED` |
| Jane Smith | ✅ | 500.00 | 0.00 | `PENDING` |
| Robert Brown | ❌ | 0.00 | 0.00 | `-` |

*Note: The above table represents the conceptual UI state during a meeting operation.*

---

## 🛠️ Kotlin Multiplatform Alignment

The module is designed with future portability in mind, adhering to the modern **Mifos Mobile** architecture direction.

*   **Modular Foundation:** The current Android-first implementation uses a clean separation of concerns (Domain, Data, Presentation) that facilitates future migration.
*   **Shared Logic Potential:** Business logic defined in the Domain layer and Repository interfaces is positioned for potential extraction into a KMP shared module.
*   **Cross-Platform Vision:** This architecture enables the eventual support of group banking workflows on both Android and iOS with minimal redesign.
*   **Incremental MVP:** The current focus remains on a robust Android implementation to serve as the reference for future KMP expansion.

---

## 📱 Operational Screens

The module provides a specialized set of interfaces designed for high-efficiency field operations.

1.  **Field Officer Dashboard:** Provides an overview of the day's scheduled meetings, collection targets, and synchronization health. Fully functional offline using cached data.
2.  **Group List Screen:** A searchable index of all Centers and Groups assigned to the officer, enabling quick navigation to specific group operations.
3.  **Group Detail Screen:** Displays critical group information, member lists, and historical collection progress to provide context before a meeting starts.
4.  **Collection Sheet Screen:** The primary workspace for bulk data entry. Supports rapid recording of attendance, savings, and loan repayments with immediate local persistence.
5.  **Meeting Workflow Screen:** Guides the officer through the structured steps of a group meeting, from opening the session to final review and local "closing."
6.  **Sync Status Screen:** A dedicated management view for tracking the synchronization queue, allowing officers to manually trigger retries or verify that all field data has been successfully uploaded.

---

## 🏗️ Architecture

The project follows **Clean Architecture** principles, specifically tailored to support high-reliability **field operations** and **group-based** financial services.

### Domain-Driven Module Breakdown
- `:app`: The host application and entry point.
- `:features:group-save`: The core operational module containing Group Save workflows, VSLA logic, and Collection Sheet interfaces.
- `:core:database`: Shared Room database optimized for local-first persistence and offline queue management.
- `:core:network`: Centralized networking logic with support for batch synchronization.
- `:core:common`: Reusable utilities and generic UI states used across the operational platform.

### Architectural Pillars
1.  **Domain Layer:** Business logic for group collections defined by pure Kotlin models and Use Cases, ensuring portability and future KMP alignment.
2.  **Data Layer (Repository Pattern):** Orchestrates data between remote APIs and local Room storage. Implements a "Single Source of Truth" that prioritizes local recording to ensure field officer workflow reliability.
3.  **Presentation Layer (MVVM):** Reactive state management using `ViewModel` and `StateFlow`. Designed to handle complex operational states like bulk entries, synchronization progress, and connectivity-aware UI updates.

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
./gradlew :features:group-save:test
```

---

## 📂 Project Structure Highlights
- **Collection Sheet UI:** `features/group-save/src/main/java/.../presentation/ui/CollectionSheetScreen.kt`
- **Meeting Workflow:** `features/group-save/src/main/java/.../presentation/ui/MeetingWorkflowScreen.kt`
- **Operational ViewModel:** `features/group-save/src/main/java/.../presentation/viewmodel/GroupSaveViewModel.kt`
- **Group Repository:** `features/group-save/src/main/java/.../data/repository/GroupSaveRepositoryImpl.kt`
- **Offline Queue Logic:** `features/group-save/src/main/java/.../data/worker/SyncCollectionsWorker.kt`
- **Database Schema:** `core/database/src/main/java/.../core/database/AppDatabase.kt`

---

## 🛣️ Roadmap
- [ ] **WorkManager:** Integrate for more robust background synchronization.
- [ ] **Paging 3:** Implement for efficient handling of large transaction datasets.
- [ ] **Advanced Filtering:** Add date-range and multi-category filters.
- [ ] **UI Polish:** Add micro-interactions and refined animations for operational entries.

---

## 🚀 Project Vision

The **Group Save & Collection Module** is a focused MVP designed to demonstrate a high-reliability operational path for Mifos group-based services. By prioritizing the field officer's workflow and the necessity of offline-first capability, the project addresses the core technical and operational challenges of microfinance in low-connectivity environments.

The architecture is intentionally modular and grounded in modern Android practices, serving as a stable reference for future **Kotlin Multiplatform (KMP)** expansion. The implementation follows a realistic, incremental development strategy, focusing on delivering a functional and verifiable operational prototype that aligns with the broader Mifos ecosystem goals.

---

## 📜 License
This project is licensed under the Apache License 2.0.
