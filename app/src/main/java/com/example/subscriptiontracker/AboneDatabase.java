package com.example.subscriptiontracker;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Abonelik.class}, version = 2, exportSchema = false)
public abstract class AboneDatabase extends RoomDatabase {

    public abstract AboneDao aboneDao();

    private static volatile AboneDatabase INSTANCE;

    public static AboneDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AboneDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AboneDatabase.class,
                                    "abone_database"
                            )
                            .fallbackToDestructiveMigration() // Şema değiştiğinde eski tabloyu sıfırlayıp yenisini oluşturur
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}