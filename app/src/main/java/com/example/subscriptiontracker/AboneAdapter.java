package com.example.subscriptiontracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

public class AboneAdapter extends RecyclerView.Adapter<AboneAdapter.AboneViewHolder> {
    private final List<Abonelik> subscriptionList;
    private final AboneDatabase db;
    private final ExecutorService executor;
    private final Runnable onDataChangedListener;

    public AboneAdapter(List<Abonelik> subscriptionList, AboneDatabase db, ExecutorService executor, Runnable onDataChangedListener) {
        this.subscriptionList = subscriptionList;
        this.db = db;
        this.executor = executor;
        this.onDataChangedListener = onDataChangedListener;
    }

    @NonNull
    @Override
    public AboneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_abonelik, parent, false);
        return new AboneViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AboneViewHolder holder, int position) {
        Abonelik subscription = subscriptionList.get(position);
        holder.tvIsim.setText(subscription.getName());

        // Dinamik Para Birimi Okuma
        String currency = (subscription.getCurrency() != null && !subscription.getCurrency().isEmpty())
                ? subscription.getCurrency() : "₺";

        // "Para Birimi + Tutar - Tarih (Periyot)" formatı
        String cycleStr = (subscription.getBillingCycle() != null && !subscription.getBillingCycle().isEmpty())
                ? " (" + subscription.getBillingCycle() + ")" : "";
        String detailText = currency + subscription.getAmount() + " - " + subscription.getDate() + cycleStr;
        holder.tvTutar.setText(detailText);

        // Tıklayınca açılan detay diyaloğu
        holder.itemView.setOnClickListener(v -> {
            String notesDisplay = (subscription.getNotes() == null || subscription.getNotes().isEmpty()) ? "-" : subscription.getNotes();
            String cycleDisplay = (subscription.getBillingCycle() == null || subscription.getBillingCycle().isEmpty()) ? "-" : subscription.getBillingCycle();

            new AlertDialog.Builder(v.getContext())
                    .setTitle(subscription.getName())
                    .setMessage(
                            "Amount: " + currency + subscription.getAmount() + "\n" +
                                    "Date: " + subscription.getDate() + "\n" +
                                    "Category: " + subscription.getCategory() + "\n" +
                                    "Billing Cycle: " + cycleDisplay + "\n" +
                                    "Note: " + notesDisplay
                    )
                    .setPositiveButton("Edit", (dialog, which) -> {
                        openEditForm(v.getContext(), subscription, holder.getAdapterPosition());
                    })
                    .setNegativeButton("Close", null)
                    .show();
        });

        // Uzun basarak silme diyaloğu
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Subscription")
                    .setMessage("Are you sure you want to delete " + subscription.getName() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        executor.execute(() -> {
                            db.aboneDao().sil(subscription);
                            ((Activity) v.getContext()).runOnUiThread(() -> {
                                int positionToRemove = holder.getAdapterPosition();
                                if (positionToRemove != RecyclerView.NO_POSITION) {
                                    subscriptionList.remove(positionToRemove);
                                    notifyItemRemoved(positionToRemove);

                                    if (onDataChangedListener != null) {
                                        onDataChangedListener.run();
                                    }
                                }
                            });
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });
    }

    private void openEditForm(Context context, Abonelik subscription, int position) {
        ScrollView scrollView = new ScrollView(context);
        LinearLayout dialogLayout = new LinearLayout(context);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(50, 40, 50, 10);
        scrollView.addView(dialogLayout);

        EditText etName = new EditText(context);
        etName.setHint("Subscription Name");
        etName.setText(subscription.getName());
        dialogLayout.addView(etName);

        EditText etAmount = new EditText(context);
        etAmount.setHint("Amount");
        etAmount.setText(subscription.getAmount());
        dialogLayout.addView(etAmount);

        // Para Birimi Alanı (Seçilebilir ve Düzenlenebilir)
        EditText etCurrency = new EditText(context);
        etCurrency.setHint("Select Currency");
        String currentCurrency = (subscription.getCurrency() != null && !subscription.getCurrency().isEmpty())
                ? subscription.getCurrency() : "₺";
        etCurrency.setText(currentCurrency);
        etCurrency.setFocusable(false);
        etCurrency.setClickable(true);

        String[] currencyOptions = {"₺", "$", "€", "£"};
        etCurrency.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Select Currency")
                    .setItems(currencyOptions, (dialog, which) -> etCurrency.setText(currencyOptions[which]))
                    .show();
        });
        dialogLayout.addView(etCurrency);

        // Tarih Seçimi
        EditText etDate = new EditText(context);
        etDate.setHint("Select Date");
        etDate.setText(subscription.getDate());
        etDate.setFocusable(false);
        etDate.setClickable(true);
        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(
                    context,
                    (dpView, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        etDate.setText(selectedDate);
                    },
                    year, month, day
            );
            datePicker.show();
        });
        dialogLayout.addView(etDate);

        // Kategori Seçimi
        EditText etCategory = new EditText(context);
        etCategory.setHint("Select Category");
        etCategory.setText(subscription.getCategory());
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

        etCategory.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Select Category")
                    .setItems(categoryOptions, (dialog, which) -> {
                        etCategory.setText(categoryOptions[which]);
                    })
                    .show();
        });
        dialogLayout.addView(etCategory);

        // Ödeme Periyodu Seçimi
        EditText etBillingCycle = new EditText(context);
        etBillingCycle.setHint("Select Billing Cycle");
        etBillingCycle.setText(subscription.getBillingCycle());
        etBillingCycle.setFocusable(false);
        etBillingCycle.setClickable(true);

        String[] cycleOptions = {"7 Days", "14 Days", "1 Month", "3 Months", "6 Months", "Yearly"};

        etBillingCycle.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Select Billing Cycle")
                    .setItems(cycleOptions, (dialog, which) -> {
                        etBillingCycle.setText(cycleOptions[which]);
                    })
                    .show();
        });
        dialogLayout.addView(etBillingCycle);

        EditText etNotes = new EditText(context);
        etNotes.setHint("Add note (optional)");
        etNotes.setText(subscription.getNotes());
        dialogLayout.addView(etNotes);

        new AlertDialog.Builder(context)
                .setTitle("Edit Subscription")
                .setView(scrollView)
                .setPositiveButton("Save", (dialog, which) -> {
                    subscription.setName(etName.getText().toString());
                    subscription.setAmount(etAmount.getText().toString());
                    subscription.setCurrency(etCurrency.getText().toString());
                    subscription.setDate(etDate.getText().toString());
                    subscription.setCategory(etCategory.getText().toString());
                    subscription.setBillingCycle(etBillingCycle.getText().toString());
                    subscription.setNotes(etNotes.getText().toString());

                    executor.execute(() -> {
                        db.aboneDao().guncelle(subscription);
                        ((Activity) context).runOnUiThread(() -> {
                            notifyItemChanged(position);

                            if (onDataChangedListener != null) {
                                onDataChangedListener.run();
                            }
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return subscriptionList.size();
    }

    public static class AboneViewHolder extends RecyclerView.ViewHolder {
        TextView tvIsim;
        TextView tvTutar;

        public AboneViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIsim = itemView.findViewById(R.id.tvIsim);
            tvTutar = itemView.findViewById(R.id.tvTutar);
        }
    }
}