package fpt.prm392.fe_salehunter.data.remote.interfaces;

import fpt.prm392.fe_salehunter.model.auth.LoginDataModel;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.auth.ChangePasswordRequestModel;
import fpt.prm392.fe_salehunter.model.auth.ForgotPasswordRequestModel;
import fpt.prm392.fe_salehunter.model.auth.LoginRequestModel;
import fpt.prm392.fe_salehunter.model.auth.RefreshTokenRequestModel;
import fpt.prm392.fe_salehunter.model.auth.RegisterRequestModel;
import fpt.prm392.fe_salehunter.model.auth.RegisterResponseModel;
import fpt.prm392.fe_salehunter.model.auth.ResetPasswordRequestModel;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthInterface {

    @Headers({"client: mobile"})
    @POST("api/Auth/login")
    Observable<Response<BaseResponseModel<LoginDataModel>>> login(@Body LoginRequestModel loginRequest);

    @Headers({"client: mobile"})
    @POST("api/Auth/register")
    Observable<Response<RegisterResponseModel>> register(@Body RegisterRequestModel registerRequest);

    @Headers({"client: mobile"})
    @POST("api/Auth/refresh")
    Observable<Response<BaseResponseModel<LoginDataModel>>> refreshToken(@Body RefreshTokenRequestModel refreshRequest);

    @Headers({"client: mobile"})
    @POST("api/Auth/logout")
    Observable<Response<BaseResponseModel<Object>>> logout(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @POST("api/Auth/change-password")
    Observable<Response<BaseResponseModel<Object>>> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequestModel changePasswordRequest);

    @Headers({"client: mobile"})
    @POST("api/Auth/forgot-password")
    Observable<Response<BaseResponseModel<Object>>> forgotPassword(@Body ForgotPasswordRequestModel forgotPasswordRequest);

    @Headers({"client: mobile"})
    @POST("api/Auth/reset-password")
    Observable<Response<BaseResponseModel<Object>>> resetPassword(@Body ResetPasswordRequestModel resetPasswordRequest);

    @Headers({"client: mobile"})
    @POST("api/Auth/verify-token")
    Observable<Response<BaseResponseModel<Object>>> verifyToken(@Body String token);
}
