package fpt.prm392.fe_salehunter.data.remote.interfaces;

import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.request.CreateStoreRequestModel;
import fpt.prm392.fe_salehunter.model.store.StoreModel;
import fpt.prm392.fe_salehunter.model.store.StorePageData;
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

public interface StoreInterface {

    @Headers({"client: mobile"})
    @POST("api/Store")
    Observable<Response<BaseResponseModel<StoreModel>>> createStore(@Header("Authorization") String token, @Body CreateStoreRequestModel createStoreRequest);

    @Headers({"client: mobile"})
    @GET("api/Store/{id}")
    Observable<Response<BaseResponseModel<StorePageData>>> getStore(@Header("Authorization") String token, @Path("id") long storeId);

    @Headers({"client: mobile"})
    @GET("api/Store/my-store")
    Observable<Response<BaseResponseModel<StorePageData>>> getMyStore(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @PUT("api/Store/{id}")
    Observable<Response<BaseResponseModel<Object>>> updateStore(@Header("Authorization") String token, @Path("id") long storeId, @Body CreateStoreRequestModel updateStoreRequest);

    @Headers({"client: mobile"})
    @DELETE("api/Store/{id}")
    Observable<Response<BaseResponseModel<Object>>> deleteStore(@Header("Authorization") String token, @Path("id") long storeId);

    @Headers({"client: mobile"})
    @GET("api/Store")
    Observable<Response<BaseResponseModel<Object>>> getAllStores(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @GET("api/Store/search")
    Observable<Response<BaseResponseModel<Object>>> searchStores(@Header("Authorization") String token,
                                                                 @Query("query") String query,
                                                                 @Query("latitude") Double latitude,
                                                                 @Query("longitude") Double longitude,
                                                                 @Query("radiusKm") Double radiusKm);

    @Headers({"client: mobile"})
    @GET("api/Store/nearby")
    Observable<Response<BaseResponseModel<Object>>> getNearbyStores(@Header("Authorization") String token,
                                                                    @Query("latitude") double latitude,
                                                                    @Query("longitude") double longitude,
                                                                    @Query("radiusKm") double radiusKm);
}
