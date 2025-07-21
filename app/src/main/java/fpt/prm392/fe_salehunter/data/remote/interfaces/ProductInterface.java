package fpt.prm392.fe_salehunter.data.remote.interfaces;

import java.util.ArrayList;

import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.request.CreateProductRequestModel;
import fpt.prm392.fe_salehunter.model.product.ProductRateModel;
import fpt.prm392.fe_salehunter.model.product.ProductPageModel;
import fpt.prm392.fe_salehunter.model.product.ProductModel;
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

public interface ProductInterface {

    @Headers({"client: mobile"})
    @POST("api/Product")
    Observable<Response<BaseResponseModel<ProductModel>>> createProduct(@Header("Authorization") String token, @Body CreateProductRequestModel createProductRequest);

    @Headers({"client: mobile"})
    @GET("api/Product/{id}")
    Observable<Response<BaseResponseModel<ProductPageModel>>> getProduct(@Header("Authorization") String token, @Path("id") long productId);

    @Headers({"client: mobile"})
    @PUT("api/Product/{id}")
    Observable<Response<BaseResponseModel<ProductModel>>> updateProduct(@Header("Authorization") String token, @Path("id") long productId, @Body CreateProductRequestModel updateProductRequest);

    @Headers({"client: mobile"})
    @DELETE("api/Product/{id}")
    Observable<Response<BaseResponseModel<Object>>> deleteProduct(@Header("Authorization") String token, @Path("id") long productId);

    @Headers({"client: mobile"})
    @GET("api/Product/store/{storeId}")
    Observable<Response<BaseResponseModel<ArrayList<ProductModel>>>> getProductsByStore(@Header("Authorization") String token, @Path("storeId") long storeId);

    @Headers({"client: mobile"})
    @GET("api/Product/search")
    Observable<Response<BaseResponseModel<ArrayList<ProductModel>>>> searchProducts(
            @Header("Authorization") String token,
            @Query("page") Integer page,
            @Query("size") Integer size,
            @Query("query") String query,
            @Query("storeId") Long storeId,
            @Query("category") String category,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice,
            @Query("sortBy") String sortBy,
            @Query("brand") String brand
    );

    @Headers({"client: mobile"})
    @GET("api/Product/favorites")
    Observable<Response<BaseResponseModel<ArrayList<ProductModel>>>> getFavoriteProducts(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @POST("api/Product/{id}/favorite")
    Observable<Response<BaseResponseModel<Object>>> addToFavorites(@Header("Authorization") String token, @Path("id") long productId);

    @Headers({"client: mobile"})
    @DELETE("api/Product/{id}/favorite")
    Observable<Response<BaseResponseModel<Object>>> removeFromFavorites(@Header("Authorization") String token, @Path("id") long productId);

    @Headers({"client: mobile"})
    @GET("api/Product/history")
    Observable<Response<BaseResponseModel<ArrayList<ProductModel>>>> getViewHistory(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @GET("api/Product/recommended")
    Observable<Response<BaseResponseModel<ArrayList<ProductModel>>>> getRecommendedProducts(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @GET("api/Product/on-sale")
    Observable<Response<BaseResponseModel<ArrayList<ProductModel>>>> getOnSaleProducts(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @POST("api/Product/{id}/rating")
    Observable<Response<BaseResponseModel<Object>>> rateProduct(@Header("Authorization") String token, @Path("id") long productId, @Body ProductRateModel productRateModel);

    @Headers({"client: mobile"})
    @GET("api/Product/{id}/ratings")
    Observable<Response<BaseResponseModel<Object>>> getProductRatings(@Header("Authorization") String token, @Path("id") long productId);
}
