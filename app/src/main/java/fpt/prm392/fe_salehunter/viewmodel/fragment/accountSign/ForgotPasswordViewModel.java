package fpt.prm392.fe_salehunter.viewmodel.fragment.accountSign;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import retrofit2.Response;

public class ForgotPasswordViewModel extends AndroidViewModel {
    private final Repository repository;

    public ForgotPasswordViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository();
    }

    public LiveData<Response<BaseResponseModel<Object>>> sendEmailVerification(String email) {
        return repository.sendEmailVerification(email);
    }

}
