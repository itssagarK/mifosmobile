# PART 1 — REPOSITION THE PROJECT INTRODUCTION

Replace the current introduction and project summary with a stronger Mifos-aligned explanation.

Requirements:
- Remove consumer-finance language
- Remove “traditional finance dashboard” wording
- Emphasize:
    - Group Banking
    - VSLA workflows
    - field officer operations
    - offline-first collection workflows
    - rural/low-connectivity environments
- Explain that the project is an MVP extension aligned with Mifos Group Save direction
- Mention:
    - Centers
    - Groups
    - Collection Sheets
    - Meetings
    - Group Savings
- Keep the tone technical and realistic
- Do not exaggerate capabilities
- Do not use fintech buzzwords
- Mention future KMP alignment briefly

Target outcome:
The introduction should make the project sound like:
“an offline-first operational workflow platform for group-based financial services.”

------------------------------------------------------------

# PART 2 — RENAME FEATURE DIRECTION

Update all terminology across the documentation.

Replace weak/generic names like:
- Savings Transactions
- Balance Summary
- Transaction Entry
- Transaction Details

With stronger operational terminology like:
- Group Collections
- Group Savings Operations
- Collection Summary
- Meeting Collections
- Member Contributions
- Group Save Workflow

Rename feature module references:
FROM:
features/group-save

TO:
features/group-save

OR:
features/group-banking

Update all related architecture/module references consistently.

------------------------------------------------------------

# PART 3 — REWRITE “WHAT THIS APP HAS” SECTION

Completely rewrite the “What This App Has” section.

Requirements:
- Focus on operational workflows instead of banking dashboard features
- Add realistic Group Save MVP features
- Remove consumer-finance wording
- Emphasize:
    - Group management
    - Collection workflows
    - Offline-first operations
    - Meeting workflows
    - Sync queue handling
    - Field officer workflows

New features should include:
- Group Savings Operations
- Collection Sheet Workflow
- Offline Queue Management
- Meeting-based Collections
- Group & Member Management
- Offline-first Persistence
- Pending Sync Tracking

The section should feel aligned with:
Mifos Group Banking + VSLA operations.

------------------------------------------------------------

# PART 4 — ADD GROUP BANKING DOMAIN CONTEXT

Create a new markdown section called:

## Group Banking Context

Explain:
- What VSLA means
- Why group banking workflows matter
- Why offline-first capability is important
- How field officers operate in low-connectivity environments
- Why Collection Sheets and Meetings are important workflows

Keep the explanation concise and realistic.

Do NOT:
- write academic paragraphs
- over-explain microfinance theory
- sound like marketing material

Goal:
Show domain understanding.

------------------------------------------------------------

# PART 5 — ADD COLLECTION SHEET WORKFLOW SECTION

Create a new markdown section called:

## Collection Sheet Workflow

Requirements:
Explain:
- bulk collection entry
- meeting-based collection operations
- member contribution tracking
- loan repayment entry
- offline save behavior
- pending sync queue
- synchronization retry flow

Add a realistic workflow example like:

Meeting Starts
→ Attendance Recorded
→ Member Contributions Entered
→ Data Saved Offline
→ Transactions Queued
→ Internet Restored
→ Sync Completed

Explain why this workflow is important for field officers.

This section is CRITICAL because Collection Sheets are explicitly mentioned in the DMP ticket.

------------------------------------------------------------

# PART 6 — ADD MEETING WORKFLOW SECTION

Create a markdown section:

## Meeting-based Operations

Requirements:
Explain:
- attendance tracking
- group meetings
- member contribution collection
- meeting notes
- collection progress
- operational workflow during field visits

Add example operational table:

| Member | Attendance | Savings | Loan Payment | Sync Status |

Do NOT implement backend logic.
This is documentation and architecture alignment only.

------------------------------------------------------------

# PART 7 — IMPROVE OFFLINE-FIRST SECTION

Enhance the Offline-first Strategy section.

Add:
- Offline queue visibility
- Pending sync tracking
- Retry synchronization
- Local-first transaction recording
- Connectivity-aware workflows

Explain that:
field officers should continue operations even without internet access.

Add a realistic sync flow example:

Offline Mode
→ Local Save
→ Queue Transaction
→ Retry Sync
→ Sync Success

Do not exaggerate architecture complexity.

------------------------------------------------------------

# PART 8 — ADD KMP ALIGNMENT SECTION

Create a markdown section:

## Kotlin Multiplatform Alignment

Explain:
- The project follows modern Mifos mobile architecture direction
- Shared business logic can later support Android and iOS
- Modular architecture enables future KMP expansion
- Current MVP focuses on Android-first implementation planning

Important:
Do NOT pretend full KMP implementation already exists.

Keep this realistic.

------------------------------------------------------------

# PART 9 — UPDATE ARCHITECTURE DESCRIPTION

Improve architecture explanations to sound domain-aware.

Current architecture sounds generic.

Add language emphasizing:
- group collection workflows
- offline-first operations
- meeting-based synchronization
- operational data consistency
- field officer workflow reliability

Keep:
- Clean Architecture
- Repository Pattern
- MVVM
- Use Cases
- Room
- WorkManager

But connect them to:
Group Banking workflows.

------------------------------------------------------------

# PART 10 — UPDATE MODULE STRUCTURE

Update module naming and structure to align with Group Save workflows.

Replace:
features/group-save

With:
features/group-save

Inside the module structure, add conceptual support for:
- meetings
- collection sheets
- group management
- offline queue handling

Keep the structure modular and realistic.

------------------------------------------------------------

# PART 11 — UPDATE SCREEN DESCRIPTIONS

Rewrite the “Screens” section.

Replace generic transaction screens with operational screens.

New screens should include:
1. Field Officer Dashboard
2. Group List Screen
3. Group Detail Screen
4. Collection Sheet Screen
5. Meeting Workflow Screen
6. Sync Status Screen

Each screen description should explain:
- operational purpose
- offline behavior
- field usage context

------------------------------------------------------------

# PART 12 — REDUCE GENERIC FINTECH FEEL

Across the entire markdown:
- remove references that sound like personal banking
- remove finance-dashboard language
- reduce balance-focused terminology
- remove analytics-heavy wording

Replace with:
- collection operations
- meeting workflows
- field operations
- group savings
- offline synchronization
- operational progress

Goal:
The project should feel like:
“field operations software”
NOT:
“consumer finance app.”

------------------------------------------------------------

# PART 13 — FINAL POSITIONING SECTION

Create a final markdown section:

## Project Vision

Write a concise closing summary explaining:

- The project focuses on Group Save support for field officers
- The MVP prioritizes operational reliability
- Offline-first workflows are central
- The architecture is designed for future KMP scalability
- The implementation follows realistic incremental development

Tone:
professional
technical
grounded
OSS-oriented

Do NOT:
- overpromise
- sound corporate
- use hype language
