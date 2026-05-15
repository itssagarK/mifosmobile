# Web Version - Savings Transactions

This is a standalone web version of the Savings Transaction feature from the Mifos Mobile Android app. It is designed to be feature-complete with the mobile version, providing a consistent experience across platforms.

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

## Advanced Features

- **Responsive Desktop/Tablet Layout**: 
  - **Mobile**: Single-column transaction list with modal details.
  - **Tablet (>=1024px)**: Fixed sidebar for statistics, balance, and goal tracking.
  - **Desktop (>=1440px)**: 3-pane view with a dedicated, animated Detail Pane on the right for seamless transitions.
- **Advanced Multi-Category Filtering**: Select multiple transaction types simultaneously (Deposit, Withdrawal, Interest, Fees) to refine your view.
- **UI Polish & Shimmer**: Modern shimmer effect during loading for better perceived performance, matching the Android app's aesthetic.
- **Trend Visualization**: Interactive 7-day trend chart with tooltips showing daily net change.
- **Goal Tracking**: Set and visualize monthly savings goals with celebration states.
- **Offline-First Experience**: Data persists in `localStorage` and includes sync status indicators for newly added transactions.

## Mock Data

The app includes sample mock data demonstrating various transaction types. The data simulates:
- Shimmer loading state (1.2 second delay)
- Search/filter by transaction type, amount, or description
- Automatic "sync" simulation for new transactions after 3 seconds
- Export capability to CSV format for external tracking
