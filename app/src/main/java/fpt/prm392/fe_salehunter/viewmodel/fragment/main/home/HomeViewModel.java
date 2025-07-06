package fpt.prm392.fe_salehunter.viewmodel.fragment.main.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import fpt.prm392.fe_salehunter.data.Repository;

public class HomeViewModel extends AndroidViewModel {
    private Repository repository;

    public HomeViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
    }

}
