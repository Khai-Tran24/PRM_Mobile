package fpt.prm392.fe_salehunter.model.auth;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenRequestModel {
    @SerializedName("refreshToken")
    private String refreshToken;

    public RefreshTokenRequestModel() {}

    public RefreshTokenRequestModel(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
