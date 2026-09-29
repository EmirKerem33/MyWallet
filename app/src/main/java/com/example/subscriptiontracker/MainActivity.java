package com.example.subscriptiontracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private AboneDatabase db;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private AboneAdapter adapter;
    private List<Abonelik> subscriptionList = new ArrayList<>();

    private void loadData() {
        executor.execute(() -> {
            List<Abonelik> fetchedList = db.aboneDao().tumunuGetir();
            runOnUiThread(() -> {
                subscriptionList.clear();
                subscriptionList.addAll(fetchedList);
                adapter.notifyDataSetChanged();

                // Update summary card with live data
                updateSummaryCard(fetchedList);
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) { // <-- action_hakkinda yerine action_about
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        String versionInfo = "Unknown";
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
                "This application is open-source software. It can be freely shared, modified, and compiled in accordance with the terms of the MIT License.\n" +
                "The complete source code and licensing details are publicly available on GitHub.\n\n" +
                "Third-party libraries:\n" +
                "- AndroidX Room (Apache License 2.0)\n" +
                "- AndroidX RecyclerView (Apache License 2.0)\n" +
                "- AndroidX AppCompat (Apache License 2.0)";

        new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }

    private void setAlarm(String name, String amount, String dateStr, String billingCycle) {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AbonelikHatirlatici.class);
        intent.putExtra("isim", name);
        intent.putExtra("tutar", amount);

        int requestId = (int) System.currentTimeMillis();

        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                this,
                requestId,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
        );

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(dateStr);
            if (date != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                switch (billingCycle) {
                    case "7 Days":
                        cal.add(Calendar.DAY_OF_YEAR, 7);
                        break;
                    case "14 Days":
                        cal.add(Calendar.DAY_OF_YEAR, 14);
                        break;
                    case "1 Month":
                        cal.add(Calendar.MONTH, 1);
                        break;
                    case "3 Months":
                        cal.add(Calendar.MONTH, 3);
                        break;
                    case "6 Months":
                        cal.add(Calendar.MONTH, 6);
                        break;
                    case "Yearly":
                        cal.add(Calendar.YEAR, 1);
                        break;
                }

                cal.add(Calendar.DAY_OF_YEAR, -1);
                cal.set(Calendar.HOUR_OF_DAY, 9);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);

                long triggerTime = cal.getTimeInMillis();

                if (alarmManager != null && triggerTime > System.currentTimeMillis()) {
                    alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateSummaryCard(List<Abonelik> list) {
        double totalCost = 0.0;
        String mainCurrency = "$";

        for (Abonelik a : list) {
            try {
                String amountStr = a.getTutar() != null ? a.getTutar().replace(",", ".") : "0";
                double amount = Double.parseDouble(amountStr);
                totalCost += amount;
                if (a.getCurrency() != null) {
                    mainCurrency = a.getCurrency();
                }
            } catch (Exception ignored) {
            }
        }

        TextView tvAylikToplam = findViewById(R.id.tvAylikToplam);
        TextView tvToplamAbonelik = findViewById(R.id.tvToplamAbonelik);
        TextView tvYaklasanOdeme = findViewById(R.id.tvYaklasanOdeme);

        if (tvAylikToplam != null) {
            tvAylikToplam.setText(String.format(Locale.getDefault(), "%s%.2f", mainCurrency, totalCost));
        }
        if (tvToplamAbonelik != null) {
            tvToplamAbonelik.setText(list.size() + " Active Subscriptions");
        }
        if (tvYaklasanOdeme != null) {
            if (!list.isEmpty()) {
                tvYaklasanOdeme.setText("First Added: " + list.get(0).getIsim());
            } else {
                tvYaklasanOdeme.setText("Next: -");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = AboneDatabase.getInstance(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvBaslik = findViewById(R.id.tvBaslik);
        RecyclerView rvAbonelikler = findViewById(R.id.rvAbonelikler);
        Button btnEkle = findViewById(R.id.btnEkle);
        ImageButton btnMenu = findViewById(R.id.btnMenu);

        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_about) { // <-- Burayı action_about yap
                        showAboutDialog();
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }

        adapter = new AboneAdapter(subscriptionList, db, executor, this::loadData);
        rvAbonelikler.setAdapter(adapter);
        rvAbonelikler.setLayoutManager(new LinearLayoutManager(this));

        loadData();

        btnEkle.setOnClickListener(v -> {
            // Alanlar ekrana sığsın ve kaydırılabilsin diye ScrollView ekliyoruz
            ScrollView scrollView = new ScrollView(MainActivity.this);
            LinearLayout dialogLayout = new LinearLayout(MainActivity.this);
            dialogLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLayout.setPadding(50, 40, 50, 10);
            scrollView.addView(dialogLayout);

            EditText etName = new EditText(MainActivity.this);
            etName.setHint("Subscription Name");
            dialogLayout.addView(etName);

            EditText etAmount = new EditText(MainActivity.this);
            etAmount.setHint("Amount");
            dialogLayout.addView(etAmount);

            // Para Birimi Alanı
            EditText etCurrency = new EditText(MainActivity.this);
            etCurrency.setHint("Select Currency (Tap to select)");
            etCurrency.setText("$");
            etCurrency.setFocusable(false);
            etCurrency.setClickable(true);

            String[] currencyOptions = {"$", "₺", "€", "£"};
            etCurrency.setOnClickListener(view -> {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Select Currency")
                        .setItems(currencyOptions, (dialog, which) -> {
                            etCurrency.setText(currencyOptions[which]);
                        })
                        .show();
            });
            dialogLayout.addView(etCurrency);

            EditText etDate = new EditText(MainActivity.this);
            etDate.setHint("Start Date (Tap to select)");
            etDate.setFocusable(false);
            etDate.setClickable(true);

            etDate.setOnClickListener(view -> {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(
                        MainActivity.this,
                        (dpView, selectedYear, selectedMonth, selectedDay) -> {
                            String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                            etDate.setText(selectedDate);
                        },
                        year, month, day
                );
                datePicker.show();
            });
            dialogLayout.addView(etDate);

            EditText etBillingCycle = new EditText(MainActivity.this);
            etBillingCycle.setHint("Select Billing Cycle (Tap to select)");
            etBillingCycle.setFocusable(false);
            etBillingCycle.setClickable(true);

            String[] cycleOptions = {"7 Days", "14 Days", "1 Month", "3 Months", "6 Months", "Yearly"};

            etBillingCycle.setOnClickListener(view -> {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Select Billing Cycle")
                        .setItems(cycleOptions, (dialog, which) -> {
                            etBillingCycle.setText(cycleOptions[which]);
                        })
                        .show();
            });
            dialogLayout.addView(etBillingCycle);

            EditText etCategory = new EditText(MainActivity.this);
            etCategory.setHint("Select Category (Tap to select)");
            etCategory.setFocusable(false);
            etCategory.setClickable(true);

            String[] categoryOptions = {
                    "Music",
                    "Movies & TV",
                    "Software & Cloud",
                    "Gaming",
                    "Education & Books",
                    "Sports & Fitness",
                    "Other"
            };

            etCategory.setOnClickListener(view -> {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Select Category")
                        .setItems(categoryOptions, (dialog, which) -> {
                            etCategory.setText(categoryOptions[which]);
                        })
                        .show();
            });
            dialogLayout.addView(etCategory);

            EditText etNotes = new EditText(MainActivity.this);
            etNotes.setHint("Add note (optional)");
            dialogLayout.addView(etNotes);

            // DİKKAT: .setView(dialogLayout) yerine .setView(scrollView) veriyoruz!
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Add Subscription")
                    .setView(scrollView)
                    .setPositiveButton("Add", (dialog, which) -> {
                        String name = etName.getText().toString();
                        String amount = etAmount.getText().toString();
                        String currency = etCurrency.getText().toString();
                        String date = etDate.getText().toString();
                        String billingCycle = etBillingCycle.getText().toString();
                        String category = etCategory.getText().toString();
                        String notes = etNotes.getText().toString();

                        if (!name.isEmpty() && !amount.isEmpty() && !date.isEmpty()) {
                            executor.execute(() -> {
                                db.aboneDao().ekle(new Abonelik(name, amount, date, category, notes, billingCycle, currency));
                                runOnUiThread(() -> {
                                    loadData();
                                    setAlarm(name, amount, date, billingCycle);
                                });
                            });
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                    .show();
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}