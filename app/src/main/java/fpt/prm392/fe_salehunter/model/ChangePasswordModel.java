package fpt.prm392.fe_salehunter.model;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordModel {

    @SerializedName("oldPassword")
    private String oldPassword;

    @SerializedName("newPassword")
    private String newPassword;

    @SerializedName("newPasswordConfirm")
    private String newPasswordConfirm;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }

    public void setNewPasswordConfirm(String newPasswordConfirm) {
        this.newPasswordConfirm = newPasswordConfirm;
    }

    public fpt.prm392.fe_salehunter.model.auth.ChangePasswordRequestModel toChangePasswordRequestModel() {
        return new fpt.prm392.fe_salehunter.model.auth.ChangePasswordRequestModel(
            this.oldPassword, 
            this.newPassword, 
            this.newPasswordConfirm
        );
    }
}
