package fpt.prm392.fe_salehunter.viewmodel.activity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.UserResponseModel;
import retrofit2.Response;

public class MainActivityViewModel extends AndroidViewModel {
    private Repository repository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();

    }

    public LiveData<Response<UserResponseModel>> getUser(String token){
        return repository.getUser(token);
    }
}
