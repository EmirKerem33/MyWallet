# WalletSIOPM

MyWallet is an open-source Android application designed to track and manage recurring subscriptions, personal income, one-off expenses, and comprehensive payment schedules.

> **Note:** This project is currently under active development. Core features, UI components, and architectural implementations are subject to change.

## Features

* **Subscription & Expense Management:** Create, view, and delete recurring subscriptions and one-off expenses (groceries, bills, bank transfers) with custom titles, amounts, billing cycles, categories, and optional notes.
* **Unified Transaction Ledger:** A chronological account statement view tracking income entries, subscription renewals, and single expenses in one place.
* **Advanced Financial Analytics:** Graphical data visualization powered by `MPAndroidChart`, featuring categorical PieCharts, Income vs. Expense BarCharts, and Monthly Trend LineCharts.
* **Multi-Currency Normalization:** Built-in `CurrencyExchangeManager` supporting fiat currencies (₺, \$, €, £, CAD, AUD) and cryptocurrencies (BTC, ETH, USDT). Automatically fetches live exchange rates and normalizes all financial summaries into your default currency.
* **Payment Method Tracking:** Assign specialized badges (Credit Card, Cash, etc.) to transactions and quickly filter through them on the dashboard using dynamic filter chips.
* **Custom Categories & Limits:** Dynamic creation of user-defined categories (➕ Add Custom Category) that adaptively update budget thresholds in the Settings menu.
* **Data Control & Export:** Features a comprehensive "Reset All Data" option to wipe all database entities, alongside a CSV Export Utility to download and share financial reports.
* **Dynamic Brand Icons:** Automated high-resolution favicon fetching via Google Favicon API for popular services (Netflix, Spotify, YouTube), with a smart initials-avatar fallback for offline states.
* **Background Automation & Notifications:** Secure background task execution using `WorkManager` (via `NotificationWorker`) alongside native `AlarmManager` for precise, reliable payment reminders. Fully compliant with Android 13+ `POST_NOTIFICATIONS` runtime permissions.
* **Biometric Authentication:** Enhanced data privacy securing sensitive financial records via fingerprint or facial recognition using the `BiometricPrompt` API.
* **Offline-First Storage:** Reliable, multi-entity local data persistence powered by AndroidX Room.
* **User Interface:** Dark and Light themed layouts built strictly with Material Design 3 specifications, utilizing explicit single-choice dialogs (`setSingleChoiceItems`) for precise user selection.

## Tech Stack

* **Language:** Java
* **Database:** AndroidX Room (SQLite)
* **UI & Data Visualization:** RecyclerView, ConstraintLayout, Material Design Components, MPAndroidChart, AlertDialog, PopupMenu
* **Security & Device Features:** BiometricPrompt API, Google Favicon API
* **Concurrency & Background Architecture:** Java ExecutorService, WorkManager (`NotificationWorker`), AlarmManager, BroadcastReceiver

## Getting Started

Follow these steps to set up and run the project locally:

1. Clone the repository:
   ```bash
   git clone https://github.com/kremir-dev/WalletSIOPM.git
   ```
2. Open the project in **Android Studio**.
3. Sync the project with Gradle files and deploy to an emulator or physical device.

## Downloading APK

1. Go to the **Releases** section of the repository.
2. Select the version you want to download.
3. Download the APK file.
4. Install it on your Android device.
