package com.example.subscriptiontracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalyticsActivity extends AppCompatActivity {

    private AboneDatabase db;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private TextView tvAnalyticsIncome, tvAnalyticsExpense;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        db = AboneDatabase.getInstance(this);

        tvAnalyticsIncome = findViewById(R.id.tvAnalyticsIncome);
        tvAnalyticsExpense = findViewById(R.id.tvAnalyticsExpense);
        pieChart = findViewById(R.id.pieChart);

        setupBottomNav();
        loadFinancialData();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_analytics);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_subscriptions) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_income) {
                startActivity(new Intent(this, IncomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_analytics) {
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void loadFinancialData() {
        executor.execute(() -> {
            List<Income> incomes = db.incomeDao().tumGelirleriGetir();
            List<Abonelik> subs = db.aboneDao().tumunuGetir(); // Doğru DAO metodu: tumunuGetir()

            SharedPreferences prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
            String currency = prefs.getString("default_currency", "₺");

            double totalIncome = 0.0;
            for (Income inc : incomes) {
                try {
                    totalIncome += Double.parseDouble(inc.getAmount().replace(",", "."));
                } catch (Exception ignored) {}
            }

            double totalExpense = 0.0;
            float music = 0, movies = 0, software = 0, gaming = 0, other = 0;

            for (Abonelik sub : subs) {
                try {
                    double amount = Double.parseDouble(sub.getAmount().replace(",", "."));
                    totalExpense += amount;

                    // Abonelik sınıfındaki doğru metod: getCategory()
                    String cat = sub.getCategory() != null ? sub.getCategory() : "Other";
                    if (cat.contains("Music")) music += amount;
                    else if (cat.contains("Movie") || cat.contains("TV")) movies += amount;
                    else if (cat.contains("Software") || cat.contains("Cloud")) software += amount;
                    else if (cat.contains("Gaming")) gaming += amount;
                    else other += amount;
                } catch (Exception ignored) {}
            }

            final double finalIncome = totalIncome;
            final double finalExpense = totalExpense;

            List<PieEntry> entries = new ArrayList<>();
            if (music > 0) entries.add(new PieEntry(music, "Music"));
            if (movies > 0) entries.add(new PieEntry(movies, "Movies & TV"));
            if (software > 0) entries.add(new PieEntry(software, "Software"));
            if (gaming > 0) entries.add(new PieEntry(gaming, "Gaming"));
            if (other > 0 || entries.isEmpty()) entries.add(new PieEntry(other > 0 ? other : 1f, "Other / None"));

            runOnUiThread(() -> {
                tvAnalyticsIncome.setText(String.format(Locale.getDefault(), "%s%.2f", currency, finalIncome));
                tvAnalyticsExpense.setText(String.format(Locale.getDefault(), "%s%.2f", currency, finalExpense));
                setupPieChart(entries);
            });
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
    private void setupPieChart(List<PieEntry> entries) {
        PieDataSet dataSet = new PieDataSet(entries, "Categories");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(14f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Expenses");
        pieChart.setCenterTextColor(Color.WHITE); // Doğru metod adı
        pieChart.setHoleColor(Color.parseColor("#16161E"));
        pieChart.setTransparentCircleColor(Color.parseColor("#2D2D3F"));
        pieChart.getLegend().setTextColor(Color.WHITE);
        pieChart.invalidate();
    }
}