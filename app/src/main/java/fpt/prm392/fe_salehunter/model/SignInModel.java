package fpt.prm392.fe_salehunter.model;

import com.google.gson.annotations.SerializedName;

public class SignInModel {

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public fpt.prm392.fe_salehunter.model.auth.LoginRequestModel toLoginRequestModel() {
        return new fpt.prm392.fe_salehunter.model.auth.LoginRequestModel(this.email, this.password);
    }
}
