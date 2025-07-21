package fpt.prm392.fe_salehunter.model.auth;

import com.google.gson.annotations.SerializedName;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.SocialUserModel;

public class SocialAuthResponseModel extends BaseResponseModel<SocialUserModel> {

    @SerializedName("user")
    private SocialUserModel user;

    public SocialUserModel getUser() {
        return getData() != null ? getData() : user;
    }

    public void setUser(SocialUserModel user) {
        this.user = user;
        setData(user);
    }
}
