package org.duas.drjr.namaztimes.data;

import java.util.List;
import androidx.lifecycle.LiveData;

public class CityRepository {
    private static CityRepository instance;
    private CityDao cityDao;

    private CityRepository(CityDao cityNameDao) {
        this.cityDao = cityNameDao;
    }

    public static CityRepository getInstance(CityDao cityNameDao) {
        if (instance == null) {
            synchronized (CityRepository.class) {
                if (instance == null) {
                    instance = new CityRepository(cityNameDao);
                }
            }
        }
        return instance;
    }

    public LiveData<List<City>> getCities() {
        return this.cityDao.getCities();
    }

    public LiveData<List<String >> getProvinces() {
        return this.cityDao.getProvinces();
    }

    public LiveData<City> getPlant(int cityId) {
        return this.cityDao.getCity(cityId);
    }

    public LiveData<List<City>> getCitiesForProvice(String provinceName) {
        return this.cityDao.getCitiesForProvince(provinceName);
    }
}
