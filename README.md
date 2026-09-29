# Subscription Tracker

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
