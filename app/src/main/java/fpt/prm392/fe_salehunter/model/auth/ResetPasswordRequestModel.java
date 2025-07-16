package fpt.prm392.fe_salehunter.model.auth;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordRequestModel {
    @SerializedName("token")
    private String token;

    @SerializedName("email")
    private String email;

    @SerializedName("newPassword")
    private String newPassword;

    @SerializedName("confirmNewPassword")
    private String confirmNewPassword;

    public ResetPasswordRequestModel() {}

    public ResetPasswordRequestModel(String token, String email, String newPassword, String confirmNewPassword) {
        this.token = token;
        this.email = email;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
