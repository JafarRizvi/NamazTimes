package org.duas.drjr.namaztimes.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.duas.drjr.namaztimes.data.CityRepository;

public class CityListViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private CityRepository repository;

    public CityListViewModelFactory(@NonNull CityRepository repository) {
        super();
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public  <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CityListViewModel(repository);
    }
}
