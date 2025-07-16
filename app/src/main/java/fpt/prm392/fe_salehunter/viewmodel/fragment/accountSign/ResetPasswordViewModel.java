package fpt.prm392.fe_salehunter.viewmodel.fragment.accountSign;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.ResetPasswordModel;
import retrofit2.Response;

public class ResetPasswordViewModel extends AndroidViewModel {
    private Repository repository;

    public ResetPasswordViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository();
    }

    public LiveData<Response<BaseResponseModel>> resetPassword(String token, String email, String newPassword, String newPasswordConfirm){
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel();
        resetPasswordModel.setPassword(newPassword);
        resetPasswordModel.setPasswordConfirm(newPasswordConfirm);

        return repository.resetPassword(resetPasswordModel.toResetPasswordRequestModel(token, email));
    }
}
