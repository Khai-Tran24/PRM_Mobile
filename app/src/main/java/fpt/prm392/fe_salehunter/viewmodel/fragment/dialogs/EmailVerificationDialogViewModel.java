package fpt.prm392.fe_salehunter.viewmodel.fragment.dialogs;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.EmailVerificationModel;
import retrofit2.Response;

public class EmailVerificationDialogViewModel extends AndroidViewModel {
    private Repository repository;

    public EmailVerificationDialogViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
    }

    public LiveData<Response<BaseResponseModel>> verifyToken(String token){
        return repository.verifyToken(token);
    }

    public LiveData<Response<BaseResponseModel>> sendEmailVerification(String email){
        EmailVerificationModel emailVerificationModel = new EmailVerificationModel();
        emailVerificationModel.setEmail(email);
        return repository.sendEmailVerification(emailVerificationModel);
    }
}
