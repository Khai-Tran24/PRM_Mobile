package fpt.prm392.fe_salehunter.viewmodel.fragment.dialogs;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.ChangePasswordModel;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import retrofit2.Response;

public class PasswordChangeDialogViewModel extends AndroidViewModel {
    private final Repository repository;

    public PasswordChangeDialogViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
    }

    public LiveData<Response<BaseResponseModel<Object>>> changePassword(String token, String oldPassword, String newPassword, String newPasswordConfirm){
        ChangePasswordModel changePasswordModel = new ChangePasswordModel();
        changePasswordModel.setOldPassword(oldPassword);
        changePasswordModel.setNewPassword(newPassword);
        changePasswordModel.setNewPasswordConfirm(newPasswordConfirm);

        return repository.changePassword(token, changePasswordModel.toChangePasswordRequestModel());
    }
}

