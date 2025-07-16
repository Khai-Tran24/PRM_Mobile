package fpt.prm392.fe_salehunter.model.auth;

import com.google.gson.annotations.SerializedName;
import fpt.prm392.fe_salehunter.model.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.UserModel;

public class LoginResponseModel extends BaseResponseModel {
    // Remove duplicate fields that are already in BaseResponseModel
    // BaseResponseModel already has: code, message, isSuccess

    @SerializedName("data")
    private LoginData data;

    public static class LoginData {
        @SerializedName("accessToken")
        private String accessToken;

        @SerializedName("refreshToken")
        private String refreshToken;

        @SerializedName("expiresAt")
        private String expiresAt;

        @SerializedName("user")
        private UserModel user;

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

    // Remove duplicate getters/setters that are already in BaseResponseModel
    // BaseResponseModel already provides: getCode(), setCode(), getMessage(), setMessage(), isSuccess(), setSuccess()

    public LoginData getData() {
        return data;
    }

    public void setData(LoginData data) {
        this.data = data;
    }
}
