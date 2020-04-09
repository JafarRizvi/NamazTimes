package org.duas.drjr.namaztimes.utilities;

import android.content.Context;

import org.duas.drjr.namaztimes.data.AppDatabase;
import org.duas.drjr.namaztimes.data.CityRepository;
import org.duas.drjr.namaztimes.viewmodels.CityListViewModelFactory;

public class InjectorUtils {
    private static CityRepository getCityRepository(Context context) {
        return CityRepository.getInstance(AppDatabase.getInstance(context.getApplicationContext()).getCityDao());
    }

    public static CityListViewModelFactory provideCityListViewModelFactory(Context context) {
        return new CityListViewModelFactory(getCityRepository(context));
    }
}
