package fpt.prm392.fe_salehunter.model;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordModel {

    @SerializedName("password")
    private String password;

    @SerializedName("passwordConfirm")
    private String passwordConfirm;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public fpt.prm392.fe_salehunter.model.auth.ResetPasswordRequestModel toResetPasswordRequestModel(String token, String email) {
        return new fpt.prm392.fe_salehunter.model.auth.ResetPasswordRequestModel(
            token,
            email,
            this.password, 
            this.passwordConfirm
        );
    }
}
