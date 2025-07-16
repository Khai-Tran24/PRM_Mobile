package fpt.prm392.fe_salehunter.data.remote;

import fpt.prm392.fe_salehunter.model.BarcodeMonsterResponseModel;
import fpt.prm392.fe_salehunter.model.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.CreateProductRequestModel;
import fpt.prm392.fe_salehunter.model.CreateStoreRequestModel;
import fpt.prm392.fe_salehunter.model.CreateStoreResponseModel;
import fpt.prm392.fe_salehunter.model.ProductPageResponseModel;
import fpt.prm392.fe_salehunter.model.ProductRateModel;
import fpt.prm392.fe_salehunter.model.ProductsResponseModel;
import fpt.prm392.fe_salehunter.model.StorePageModel;
import fpt.prm392.fe_salehunter.model.UpcItemDbResponseModel;
import fpt.prm392.fe_salehunter.model.UserModel;
import fpt.prm392.fe_salehunter.model.UserResponseModel;
import fpt.prm392.fe_salehunter.model.auth.ChangePasswordRequestModel;
import fpt.prm392.fe_salehunter.model.auth.ForgotPasswordRequestModel;
import fpt.prm392.fe_salehunter.model.auth.LoginRequestModel;
import fpt.prm392.fe_salehunter.model.auth.LoginResponseModel;
import fpt.prm392.fe_salehunter.model.auth.RefreshTokenRequestModel;
import fpt.prm392.fe_salehunter.model.auth.RegisterRequestModel;
import fpt.prm392.fe_salehunter.model.auth.RegisterResponseModel;
import fpt.prm392.fe_salehunter.model.auth.ResetPasswordRequestModel;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {

    // ================ AUTHENTICATION ENDPOINTS ================
    @Headers({"client: mobile"})
    @POST("api/Auth/login")
    Observable<Response<LoginResponseModel>> login(@Body LoginRequestModel loginRequest);

    @Headers({"client: mobile"})
    @POST("api/Auth/register")
    Observable<Response<RegisterResponseModel>> register(@Body RegisterRequestModel registerRequest);

    @Headers({"client: mobile"})
    @POST("api/Auth/refresh")
    Observable<Response<LoginResponseModel>> refreshToken(@Body RefreshTokenRequestModel refreshRequest);

    @Headers({"client: mobile"})
    @POST("api/Auth/logout")
    Observable<Response<BaseResponseModel>> logout(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @POST("api/Auth/change-password")
    Observable<Response<BaseResponseModel>> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequestModel changePasswordRequest);

    @Headers({"client: mobile"})
    @POST("api/Auth/forgot-password")
    Observable<Response<BaseResponseModel>> forgotPassword(@Body ForgotPasswordRequestModel forgotPasswordRequest);

    @Headers({"client: mobile"})
    @POST("api/Auth/reset-password")
    Observable<Response<BaseResponseModel>> resetPassword(@Body ResetPasswordRequestModel resetPasswordRequest);

    @Headers({"client: mobile"})
    @POST("api/Auth/verify-token")
    Observable<Response<BaseResponseModel>> verifyToken(@Body String token);

    // ================ USER ENDPOINTS ================
    @Headers({"client: mobile"})
    @GET("api/User/profile")
    Observable<Response<UserResponseModel>> getUserProfile(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @GET("api/User/{id}")
    Observable<Response<UserResponseModel>> getUserById(@Header("Authorization") String token, @Path("id") long userId);

    @Headers({"client: mobile"})
    @PUT("api/User/profile")
    Observable<Response<UserResponseModel>> updateUserProfile(@Header("Authorization") String token, @Body UserModel userModel);

    @Headers({"client: mobile"})
    @GET("api/User")
    Observable<Response<BaseResponseModel>> getAllUsers(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @POST("api/User/{id}/deactivate")
    Observable<Response<BaseResponseModel>> deactivateUser(@Header("Authorization") String token, @Path("id") long userId);

    @Headers({"client: mobile"})
    @POST("api/User/{id}/activate")
    Observable<Response<BaseResponseModel>> activateUser(@Header("Authorization") String token, @Path("id") long userId);

    // ================ STORE ENDPOINTS ================
    @Headers({"client: mobile"})
    @POST("api/Store")
    Observable<Response<CreateStoreResponseModel>> createStore(@Header("Authorization") String token, @Body CreateStoreRequestModel createStoreRequest);

    @Headers({"client: mobile"})
    @GET("api/Store/{id}")
    Observable<Response<StorePageModel>> getStore(@Header("Authorization") String token, @Path("id") long storeId);

    @Headers({"client: mobile"})
    @GET("api/Store/my-store")
    Observable<Response<StorePageModel>> getMyStore(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @PUT("api/Store/{id}")
    Observable<Response<BaseResponseModel>> updateStore(@Header("Authorization") String token, @Path("id") long storeId, @Body CreateStoreRequestModel updateStoreRequest);

    @Headers({"client: mobile"})
    @DELETE("api/Store/{id}")
    Observable<Response<BaseResponseModel>> deleteStore(@Header("Authorization") String token, @Path("id") long storeId);

    @Headers({"client: mobile"})
    @GET("api/Store")
    Observable<Response<BaseResponseModel>> getAllStores(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @GET("api/Store/search")
    Observable<Response<BaseResponseModel>> searchStores(@Header("Authorization") String token,
                                                         @Query("query") String query,
                                                         @Query("latitude") Double latitude,
                                                         @Query("longitude") Double longitude,
                                                         @Query("radiusKm") Double radiusKm);

    @Headers({"client: mobile"})
    @GET("api/Store/nearby")
    Observable<Response<BaseResponseModel>> getNearbyStores(@Header("Authorization") String token,
                                                            @Query("latitude") double latitude,
                                                            @Query("longitude") double longitude,
                                                            @Query("radiusKm") double radiusKm);

    // ================ PRODUCT ENDPOINTS ================
    @Headers({"client: mobile"})
    @POST("api/Product")
    Observable<Response<BaseResponseModel>> createProduct(@Header("Authorization") String token, @Body CreateProductRequestModel createProductRequest);

    @Headers({"client: mobile"})
    @GET("api/Product/{id}")
    Observable<Response<ProductPageResponseModel>> getProduct(@Header("Authorization") String token, @Path("id") long productId);

    @Headers({"client: mobile"})
    @PUT("api/Product/{id}")
    Observable<Response<BaseResponseModel>> updateProduct(@Header("Authorization") String token, @Path("id") long productId, @Body CreateProductRequestModel updateProductRequest);

    @Headers({"client: mobile"})
    @DELETE("api/Product/{id}")
    Observable<Response<BaseResponseModel>> deleteProduct(@Header("Authorization") String token, @Path("id") long productId);

    @Headers({"client: mobile"})
    @GET("api/Product/store/{storeId}")
    Observable<Response<ProductsResponseModel>> getProductsByStore(@Header("Authorization") String token, @Path("storeId") long storeId);

    @Headers({"client: mobile"})
    @GET("api/Product/search")
    Observable<Response<ProductsResponseModel>> searchProducts(@Header("Authorization") String token,
                                                               @Query("query") String query,
                                                               @Query("storeId") Long storeId,
                                                               @Query("category") String category,
                                                               @Query("minPrice") Double minPrice,
                                                               @Query("maxPrice") Double maxPrice);

    @Headers({"client: mobile"})
    @GET("api/Product/favorites")
    Observable<Response<ProductsResponseModel>> getFavoriteProducts(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @POST("api/Product/{id}/favorite")
    Observable<Response<BaseResponseModel>> addToFavorites(@Header("Authorization") String token, @Path("id") long productId);

    @Headers({"client: mobile"})
    @DELETE("api/Product/{id}/favorite")
    Observable<Response<BaseResponseModel>> removeFromFavorites(@Header("Authorization") String token, @Path("id") long productId);

    @Headers({"client: mobile"})
    @GET("api/Product/history")
    Observable<Response<ProductsResponseModel>> getViewHistory(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @GET("api/Product/recommended")
    Observable<Response<ProductsResponseModel>> getRecommendedProducts(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @GET("api/Product/on-sale")
    Observable<Response<ProductsResponseModel>> getOnSaleProducts(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @POST("api/Product/{id}/rating")
    Observable<Response<BaseResponseModel>> rateProduct(@Header("Authorization") String token, @Path("id") long productId, @Body ProductRateModel productRateModel);

    @Headers({"client: mobile"})
    @GET("api/Product/{id}/ratings")
    Observable<Response<BaseResponseModel>> getProductRatings(@Header("Authorization") String token, @Path("id") long productId);

    // ================ LEGACY BARCODE ENDPOINTS (Keep as is) ================
    @GET("prod/trial/lookup")
    Observable<Response<UpcItemDbResponseModel>> barcodeLookupUpcItemDb(@Query("upc") String barcode);

    @GET("code/{barcode}")
    Observable<Response<BarcodeMonsterResponseModel>> barcodeLookupBarcodeMonster(@Path("barcode") String barcode);

    // ================ DEPRECATED LEGACY ENDPOINTS ================
    // The following endpoints are deprecated but kept for backward compatibility
    // Replace with new endpoints above when updating your code

    @Headers({"client: mobile"})
    @POST("users/auth/signin")
    @Deprecated
    Observable<Response<UserResponseModel>> signIn(@Body LoginRequestModel signInModel);

    @Headers({"client: mobile"})
    @POST("users/auth/signup")
    @Deprecated
    Observable<Response<UserResponseModel>> signUp(@Body RegisterRequestModel signUpModel);
}
