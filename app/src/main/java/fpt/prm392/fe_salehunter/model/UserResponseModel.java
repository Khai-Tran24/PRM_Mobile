package fpt.prm392.fe_salehunter.model;

import com.google.gson.annotations.SerializedName;

public class UserResponseModel extends BaseResponseModel {

    @SerializedName("user")
    private UserModel user;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

}
