package fpt.prm392.fe_salehunter.data.remote.interfaces;

import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.user.UserModel;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserInterface {

    @Headers({"client: mobile"})
    @GET("api/User/profile")
    Observable<Response<BaseResponseModel<UserModel>>> getUserProfile(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @GET("api/User/{id}")
    Observable<Response<BaseResponseModel<UserModel>>> getUserById(@Header("Authorization") String token, @Path("id") long userId);

    @Headers({"client: mobile"})
    @PUT("api/User/profile")
    Observable<Response<BaseResponseModel<UserModel>>> updateUserProfile(@Header("Authorization") String token, @Body UserModel userModel);

    @Headers({"client: mobile"})
    @GET("api/User")
    Observable<Response<BaseResponseModel<Object>>> getAllUsers(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @POST("api/User/{id}/deactivate")
    Observable<Response<BaseResponseModel<Object>>> deactivateUser(@Header("Authorization") String token, @Path("id") long userId);

    @Headers({"client: mobile"})
    @POST("api/User/{id}/activate")
    Observable<Response<BaseResponseModel<Object>>> activateUser(@Header("Authorization") String token, @Path("id") long userId);
}
