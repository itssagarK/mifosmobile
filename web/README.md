# Web Version - Savings Transactions

This is a standalone web version of the Savings Transaction feature from the Mifos Mobile Android app.

## How to Run

Simply open `index.html` in any web browser:

### Option 1: Double-click
Navigate to this folder and double-click on `index.html` to open it in your default browser.

### Option 2: Using a local server
If you have Python installed:
```bash
# Navigate to the web folder
cd web

# Start a simple HTTP server
python -m http.server 8080

# Open in browser: http://localhost:8080
```

### Option 3: VS Code Live Server
If using VS Code, install the "Live Server" extension and click "Go Live" at the bottom right.

## Features

- Displays a list of savings transactions with:
  - Transaction type (Deposit, Withdrawal, Interest, Fees)
  - Date and time
  - Description
  - Amount (green for deposits/interest, red for withdrawals/fees)
  - Sync status indicator
- Search functionality to filter transactions
- Loading state with spinner
- Empty state when no results match search

## Mock Data

The app includes sample mock data demonstrating various transaction types. The data simulates:
- Initial loading delay (1 second)
- Search/filter by transaction type or description
- Visual indicators for pending sync status