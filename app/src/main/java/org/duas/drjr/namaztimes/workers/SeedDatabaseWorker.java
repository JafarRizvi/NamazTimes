package org.duas.drjr.namaztimes.workers;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.duas.drjr.namaztimes.data.AppDatabase;
import org.duas.drjr.namaztimes.data.City;
import org.duas.drjr.namaztimes.utilities.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SeedDatabaseWorker extends Worker {
    private static final String TAG = SeedDatabaseWorker.class.getSimpleName();

    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public SeedDatabaseWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            InputStream input = getApplicationContext().getAssets().open(Constants.CITY_DATA_FILENAME);
            JsonReader reader = new JsonReader(new InputStreamReader(input));
            Type cityType = new TypeToken<List<City>>(){}.getType();
            List<City> cityList = new Gson().fromJson(reader, cityType);
            input.close();

            AppDatabase database = AppDatabase.getInstance(getApplicationContext());
            database.getCityDao().insertAll(cityList);

            return Result.success();
        } catch (IOException e) {
            Log.e(TAG, "Error seeding database", e);
            return Result.failure();
        }
    }
}
