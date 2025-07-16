package fpt.prm392.fe_salehunter.model.auth;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordRequestModel {
    @SerializedName("email")
    private String email;

    public ForgotPasswordRequestModel() {}

    public ForgotPasswordRequestModel(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
