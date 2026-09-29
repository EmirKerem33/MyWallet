package com.example.subscriptiontracker;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.card.MaterialCardView;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView tvReminderDaysSub;
    private TextView tvReminderTimeSub;
    private TextView tvDefaultCurrencySub;
    private TextView tvBudgetLimitSub;
    private TextView tvAppLockSub;

    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_REMINDER_DAYS = "reminder_days";
    private static final String KEY_REMINDER_HOUR = "reminder_hour";
    private static final String KEY_REMINDER_MINUTE = "reminder_minute";
    private static final String KEY_DEFAULT_CURRENCY = "default_currency";
    public static final String KEY_BUDGET_LIMIT = "budget_limit";
    public static final String KEY_APP_LOCK_ENABLED = "app_lock_enabled";

    private ExecutorService executor = Executors.newSingleThreadExecutor();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        ImageButton btnBack = findViewById(R.id.btnBack);
        MaterialCardView btnNotificationSettings = findViewById(R.id.btnNotificationSettings);
        MaterialCardView btnReminderDays = findViewById(R.id.btnReminderDays);
        MaterialCardView btnReminderTime = findViewById(R.id.btnReminderTime);
        MaterialCardView btnDefaultCurrency = findViewById(R.id.btnDefaultCurrency);
        MaterialCardView btnBudgetLimit = findViewById(R.id.btnBudgetLimit);
        MaterialCardView btnAppLock = findViewById(R.id.btnAppLock);
        MaterialCardView btnThemeSettings = findViewById(R.id.btnThemeSettings);
        MaterialCardView btnGithubRepo = findViewById(R.id.btnGithubRepo);
        MaterialCardView btnSendFeedback = findViewById(R.id.btnSendFeedback);
        MaterialCardView btnResetData = findViewById(R.id.btnResetData);
        MaterialCardView btnAboutSettings = findViewById(R.id.btnAboutSettings);

        tvReminderDaysSub = findViewById(R.id.tvReminderDaysSub);
        tvReminderTimeSub = findViewById(R.id.tvReminderTimeSub);
        tvDefaultCurrencySub = findViewById(R.id.tvDefaultCurrencySub);
        tvBudgetLimitSub = findViewById(R.id.tvBudgetLimitSub);
        tvAppLockSub = findViewById(R.id.tvAppLockSub);

        loadSavedSettings();

        btnBack.setOnClickListener(v -> finish());

        // 1. Sistem Bildirim Ayarları
        btnNotificationSettings.setOnClickListener(v -> {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            } else {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
            }
            startActivity(intent);
        });

        // 2. Hatırlatma Günü Seçimi
        btnReminderDays.setOnClickListener(v -> {
            String[] options = {"Same Day", "1 Day Before", "2 Days Before", "3 Days Before", "1 Week Before"};
            new AlertDialog.Builder(this)
                    .setTitle("Select Default Reminder Day")
                    .setItems(options, (dialog, which) -> {
                        sharedPreferences.edit().putString(KEY_REMINDER_DAYS, options[which]).apply();
                        tvReminderDaysSub.setText(options[which]);
                    })
                    .show();
        });

        // 3. Hatırlatma Saati Seçimi
        btnReminderTime.setOnClickListener(v -> {
            int currentHour = sharedPreferences.getInt(KEY_REMINDER_HOUR, 9);
            int currentMinute = sharedPreferences.getInt(KEY_REMINDER_MINUTE, 0);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        sharedPreferences.edit()
                                .putInt(KEY_REMINDER_HOUR, hourOfDay)
                                .putInt(KEY_REMINDER_MINUTE, minute)
                                .apply();
                        updateTimeSubText(hourOfDay, minute);
                    },
                    currentHour,
                    currentMinute,
                    true
            );
            timePickerDialog.show();
        });



        // 4. Varsayılan Para Birimi Seçimi
        btnDefaultCurrency.setOnClickListener(v -> {
            String[] currencyLabels = {"₺ (TRY)", "$ (USD)", "€ (EUR)", "£ (GBP)"};
            String[] currencySymbols = {"₺", "$", "€", "£"};

            new AlertDialog.Builder(this)
                    .setTitle("Select Default Currency")
                    .setItems(currencyLabels, (dialog, which) -> {
                        String selectedSymbol = currencySymbols[which];
                        sharedPreferences.edit().putString(KEY_DEFAULT_CURRENCY, selectedSymbol).apply();
                        tvDefaultCurrencySub.setText(currencyLabels[which]);
                    })
                    .show();
        });


        // 5. Aylık Bütçe Limiti
        btnBudgetLimit.setOnClickListener(v -> {
            String currSymbol = sharedPreferences.getString(KEY_DEFAULT_CURRENCY, "₺");
            float currentLimit = sharedPreferences.getFloat(KEY_BUDGET_LIMIT, 0f);

            EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setHint("e.g. 1500 (Set 0 to remove)");
            if (currentLimit > 0) {
                input.setText(String.valueOf(currentLimit));
            }

            new AlertDialog.Builder(this)
                    .setTitle("Set Monthly Budget Limit")
                    .setMessage("Enter your target monthly spending limit in " + currSymbol + ":")
                    .setView(input)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String val = input.getText().toString().trim();
                        float limit = val.isEmpty() ? 0f : Float.parseFloat(val);
                        sharedPreferences.edit().putFloat(KEY_BUDGET_LIMIT, limit).apply();

                        if (limit > 0) {
                            tvBudgetLimitSub.setText(String.format(Locale.getDefault(), "%s%.2f", currSymbol, limit));
                        } else {
                            tvBudgetLimitSub.setText("Not set (Unlimited)");
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // 6. Biyometrik / Uygulama Kilidi Aç-Kapa
        btnAppLock.setOnClickListener(v -> {
            boolean isEnabled = sharedPreferences.getBoolean(KEY_APP_LOCK_ENABLED, false);
            boolean newState = !isEnabled;

            sharedPreferences.edit().putBoolean(KEY_APP_LOCK_ENABLED, newState).apply();
            tvAppLockSub.setText(newState ? "Enabled (Active on launch)" : "Disabled");
            Toast.makeText(this, newState ? "App Lock Enabled" : "App Lock Disabled", Toast.LENGTH_SHORT).show();
        });

        // 7. Tema Ayarları
        btnThemeSettings.setOnClickListener(v -> {
            String[] themes = {"System Default", "Light Theme", "Dark Theme"};
            new AlertDialog.Builder(this)
                    .setTitle("Select Theme")
                    .setItems(themes, (dialog, which) -> {
                        if (which == 0) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        } else if (which == 1) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        } else if (which == 2) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }
                    })
                    .show();
        });

        // 8. GitHub Kaynak Kodu
        btnGithubRepo.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/EmirKerem33/Subscription-Tracker"));
            startActivity(browserIntent);
        });

        // 9. Geri Bildirim / E-posta Gönder
        btnSendFeedback.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"emirk.turken@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subscription Tracker - Feedback");
            try {
                startActivity(Intent.createChooser(emailIntent, "Send Feedback via Email"));
            } catch (Exception e) {
                Toast.makeText(this, "No email client found", Toast.LENGTH_SHORT).show();
            }
        });

        // 10. TÜM VERİLERİ SIFIRLA (DANGER ZONE)
        btnResetData.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("⚠️ Reset All Data")
                    .setMessage("Are you sure you want to delete ALL subscription records? This action cannot be undone.")
                    .setPositiveButton("Delete All", (dialog, which) -> {
                        executor.execute(() -> {
                            AboneDatabase.getInstance(this).aboneDao().tumunuSil();
                            runOnUiThread(() -> {
                                Toast.makeText(this, "All subscription data has been reset.", Toast.LENGTH_LONG).show();
                            });
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // 11. Hakkında Penceresi
        btnAboutSettings.setOnClickListener(v -> {
            String versionInfo = "1.0";
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                versionInfo = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            String message = "Subscription Tracker\n" +
                    "Version: " + versionInfo + "\n\n" +
                    "Developer: Emir Kerem Türken\n\n" +
                    "Open Source & License Notice\n" +
                    "This application is open-source software under the GNU GPL v2.0.";

            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setMessage(message)
                    .setPositiveButton("Close", null)
                    .show();
        });
    }

    private void loadSavedSettings() {
        String days = sharedPreferences.getString(KEY_REMINDER_DAYS, "1 Day Before");
        int hour = sharedPreferences.getInt(KEY_REMINDER_HOUR, 9);
        int minute = sharedPreferences.getInt(KEY_REMINDER_MINUTE, 0);
        String currency = sharedPreferences.getString(KEY_DEFAULT_CURRENCY, "₺");
        float budgetLimit = sharedPreferences.getFloat(KEY_BUDGET_LIMIT, 0f);
        boolean appLock = sharedPreferences.getBoolean(KEY_APP_LOCK_ENABLED, false);

        tvReminderDaysSub.setText(days);
        updateTimeSubText(hour, minute);

        if (currency.equals("$")) {
            tvDefaultCurrencySub.setText("$ (USD)");
        } else if (currency.equals("€")) {
            tvDefaultCurrencySub.setText("€ (EUR)");
        } else if (currency.equals("£")) {
            tvDefaultCurrencySub.setText("£ (GBP)");
        } else {
            tvDefaultCurrencySub.setText("₺ (TRY)");
        }

        if (budgetLimit > 0) {
            tvBudgetLimitSub.setText(String.format(Locale.getDefault(), "%s%.2f", currency, budgetLimit));
        } else {
            tvBudgetLimitSub.setText("Not set (Unlimited)");
        }

        tvAppLockSub.setText(appLock ? "Enabled (Active on launch)" : "Disabled");
    }

    private void updateTimeSubText(int hour, int minute) {
        tvReminderTimeSub.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}