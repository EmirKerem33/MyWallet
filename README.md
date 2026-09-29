# Subscription Tracker

Subscription Tracker is an open-source Android application designed to track and manage recurring subscriptions, monthly expenses, and payment schedules.

> **Note:** This project is currently under active development. Core features, UI components, and architectural implementations are subject to change.

## Features

* **Subscription Management:** Create, view, and delete subscriptions with custom titles, amounts, billing cycles, currency support (₺, $, €, £), categories, and optional notes.
* **Expense Dashboard:** Real-time calculation of total monthly financial obligations and active subscription counts.
* **Local Notifications:** Renewal alerts triggered before payment due dates using the native Android AlarmManager API.
* **Offline-First Storage:** Reliable and persistent local data management powered by AndroidX Room.
* **User Interface:** Dark-themed layouts built with Material Design 3 specifications.

## Tech Stack

* **Language:** Java
* **Database:** AndroidX Room (SQLite)
* **UI Components:** RecyclerView, ConstraintLayout, Material Design Components, AlertDialog, PopupMenu
* **Concurrency:** Java ExecutorService
* **Background Services:** AlarmManager, BroadcastReceiver

## Getting Started

Follow these steps to set up and run the project locally:

1. Clone the repository:
   ```bash
   git clone https://github.com](https://github.com/EmirKerem33/Subscription-Tracker.git
   ```
2. Open the project in **Android Studio**.
3. Sync the project with Gradle files and deploy to an emulator or physical device.
