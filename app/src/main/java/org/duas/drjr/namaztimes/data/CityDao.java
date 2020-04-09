package org.duas.drjr.namaztimes.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface CityDao {
    @Query("SELECT * FROM cities ORDER BY city")
    LiveData<List<City>> getCities();

    @Query("SELECT DISTINCT province FROM cities")
    LiveData<List<String>> getProvinces();

    @Query("SELECT * FROM cities WHERE province = :province ORDER BY city")
    LiveData<List<City>> getCitiesForProvince(String province);

    @Query("SELECT * FROM cities WHERE id = :cityId")
    LiveData<City> getCity(int cityId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<City> cities);
}
