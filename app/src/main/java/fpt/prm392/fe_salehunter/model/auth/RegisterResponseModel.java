package fpt.prm392.fe_salehunter.model.auth;

import com.google.gson.annotations.SerializedName;
import fpt.prm392.fe_salehunter.model.user.UserModel;

// FIXED: Remove double wrapping - backend sends BaseResponseDto<RegisterResponseDto>
// Mobile should expect the RegisterResponseDto directly, not wrapped in another BaseResponseModel
public class RegisterResponseModel {
    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;

    @SerializedName("expiresAt")
    private String expiresAt; // Backend sends DateTime but mobile expects String

    @SerializedName("user")
    private UserModel user;

    public RegisterResponseModel() {}

    public RegisterResponseModel(String accessToken, String refreshToken, String expiresAt, UserModel user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
