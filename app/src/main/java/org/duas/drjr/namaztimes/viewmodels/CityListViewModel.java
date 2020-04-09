package org.duas.drjr.namaztimes.viewmodels;

import org.duas.drjr.namaztimes.data.City;
import org.duas.drjr.namaztimes.data.CityRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class CityListViewModel extends ViewModel {
    private static final String PROVINCE = "Punjab";

    private CityRepository cityRepository;
    private MutableLiveData<String> provinceName;
    public  LiveData<List<City>> cities;

    CityListViewModel(@NonNull CityRepository cityRepository) {
        super();
        this.cityRepository = cityRepository;
        this.provinceName = new MutableLiveData<>("Punjab");
        this.cities = Transformations.switchMap(provinceName, it -> {
            if (it == PROVINCE) {
                return this.cityRepository.getCitiesForProvice("Punjab");
            } else {
                return this.cityRepository.getCitiesForProvice(it);
            }
        });
    }

    public void setProvince(String province) {
        this.provinceName.setValue(province);
    }

    public void clearProvinceName() {
        this.provinceName.setValue(PROVINCE);
    }

    public boolean isDefault() {
        return this.provinceName.getValue() != PROVINCE;
    }

}
