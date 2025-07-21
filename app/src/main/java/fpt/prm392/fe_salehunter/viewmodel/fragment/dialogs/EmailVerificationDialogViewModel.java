package fpt.prm392.fe_salehunter.viewmodel.fragment.dialogs;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import retrofit2.Response;

public class EmailVerificationDialogViewModel extends AndroidViewModel {
    private final Repository repository;

    public EmailVerificationDialogViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
    }

    public LiveData<Response<BaseResponseModel<Object>>> verifyToken(String token){
        return repository.verifyToken(token);
    }

    public LiveData<Response<BaseResponseModel<Object>>> sendEmailVerification(String email){
        return repository.sendEmailVerification(email);
    }
}
