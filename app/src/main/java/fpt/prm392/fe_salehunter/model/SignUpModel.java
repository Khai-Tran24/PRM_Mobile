package fpt.prm392.fe_salehunter.model;

import com.google.gson.annotations.SerializedName;

public class SignUpModel {

    @SerializedName("fullname")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("passwordConfirm")
    private String passwordConfirm;

    @SerializedName("profile_img")
    private String profileImage;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

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

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public fpt.prm392.fe_salehunter.model.auth.RegisterRequestModel toRegisterRequestModel() {
        return new fpt.prm392.fe_salehunter.model.auth.RegisterRequestModel(
            this.fullName, 
            this.email,
            "", // phoneNumber - not available in SignUpModel, use empty string
            this.password, 
            this.passwordConfirm
        );
    }
}
