package org.duas.drjr.namaztimes.data;

import android.content.Context;

import org.duas.drjr.namaztimes.utilities.Constants;
import org.duas.drjr.namaztimes.workers.SeedDatabaseWorker;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
//import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

@Database(entities = {City.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CityDao getCityDao();

    private static volatile  AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                instance = buildDatabase(context);
            }
        }
        return instance;
    }

    private static AppDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, Constants.DATABASE_NAME)
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        WorkManager.getInstance(context).enqueue(OneTimeWorkRequest.from(SeedDatabaseWorker.class));
                    }
                })
                .build();
    }
}
