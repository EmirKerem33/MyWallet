# MyWallet

Subscription Tracker is an open-source Android application designed to track and manage recurring subscriptions, personal income, monthly expenses, and payment schedules.

> **Note:** This project is currently under active development. Core features, UI components, and architectural implementations are subject to change.

## Features

* **Subscription Management:** Create, view, and delete subscriptions with custom titles, amounts, billing cycles, currency support (₺, $, €, £), categories, and optional notes.
* **Transaction Ledger:** A unified, chronological account statement view that tracks both income entries and recurring subscription expenses.
* **Financial Analytics:** Visual expense distribution across categories powered by dynamic interactive charts.
* **Expense Dashboard:** Real-time calculation of total monthly financial obligations, active subscription counts, and live net balance tracking.
* **Advanced Filtering:** Multi-dimensional sorting and filtering by category, date range, custom amount range (Min-Max), and transaction type.
* **Budget Limits & Alerts:** Custom monthly expense threshold configurations with automated system warnings when limits are approached.
* **Local Notifications:** Customizable renewal alerts triggered before payment due dates using the native Android AlarmManager API with Android 13+ runtime permission compliance.
* **Biometric Authentication:** Enhanced data privacy securing sensitive financial records via fingerprint or facial recognition.
* **Offline-First Storage:** Reliable, multi-entity local data persistence powered by AndroidX Room.
* **User Interface:** Dark-themed layouts built strictly with Material Design 3 specifications.

## Tech Stack

* **Language:** Java
* **Database:** AndroidX Room (SQLite)
* **UI & Data Visualization:** RecyclerView, ConstraintLayout, Material Design Components, MPAndroidChart, AlertDialog, PopupMenu
* **Security:** BiometricPrompt API
* **Concurrency:** Java ExecutorService
* **Background Services:** AlarmManager, BroadcastReceiver

## Changelog - v2.1.0

This release introduces significant enhancements to financial management capabilities. We've added comprehensive payment method tagging with support for credit card, debit card, cash, and bank transfer badges for both subscriptions and one-off expenses. Real-time currency exchange rate integration via CurrencyExchangeManager provides live rate fetching with fallback offline caching capabilities. A new in-app currency converter utility is now available in Settings for quick conversions. The One-off Expenses module features a dedicated Expenses tab in the bottom navigation for seamless tracking of single transactions and transfers. Analytics have been enriched with a statistical bar chart powered by MPAndroidChart, enabling intuitive Income vs Expense comparisons, alongside smart financial insights offering yearly expense projections and budget analytics. Users can now create and select custom categories dynamically, while brand logos and favicons are displayed through integrated Google Favicon API and local vector brand icons for popular subscription services. Background notifications have been configured using periodic WorkManager for automated payment reminders.

On the technical side, multi-currency normalization has been upgraded across analytics and financial summaries to automatically convert all currencies (USD, EUR, GBP) to the default currency using live exchange rates. Selection dialogs throughout the application have been refined to use radio buttons that clearly display current selections. The theme architecture has been comprehensively refactored to support dynamic Light and Dark modes utilizing Material DayNight color attributes for a more flexible user experience.

Bug fixes in this release resolve the application exit issue that occurred when navigating back from the Settings activity. Additionally, the "Reset All Data" functionality has been expanded to completely wipe subscriptions, incomes, expenses, and app preferences, ensuring thorough data cleanup when needed.

## Getting Started

Follow these steps to set up and run the project locally:

1. Clone the repository:
   ```bash
   git clone https://github.com/EmirKerem33/Subscription-Tracker.git
   ```
2. Open the project in **Android Studio**.
3. Sync the project with Gradle files and deploy to an emulator or physical device.

## Downloading APK

1. Go to the **Releases** section of the repository.
2. Select the version you want to download.
3. Download the APK file.
4. Install it on your Android device.