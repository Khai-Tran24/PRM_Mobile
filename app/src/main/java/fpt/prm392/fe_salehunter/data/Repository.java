package fpt.prm392.fe_salehunter.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import fpt.prm392.fe_salehunter.data.remote.RetrofitClient;
import fpt.prm392.fe_salehunter.data.remote.RetrofitInterface;
import fpt.prm392.fe_salehunter.model.BarcodeMonsterResponseModel;
import fpt.prm392.fe_salehunter.model.ChangePasswordModel;
import fpt.prm392.fe_salehunter.model.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.CreateProductRequestModel;
import fpt.prm392.fe_salehunter.model.CreateStoreRequestModel;
import fpt.prm392.fe_salehunter.model.CreateStoreResponseModel;
import fpt.prm392.fe_salehunter.model.EmailVerificationModel;
import fpt.prm392.fe_salehunter.model.FacebookSocialAuthModel;
import fpt.prm392.fe_salehunter.model.ProductPageResponseModel;
import fpt.prm392.fe_salehunter.model.ProductRateModel;
import fpt.prm392.fe_salehunter.model.ProductsResponseModel;
import fpt.prm392.fe_salehunter.model.ResetPasswordModel;
import fpt.prm392.fe_salehunter.model.SignInModel;
import fpt.prm392.fe_salehunter.model.auth.LoginRequestModel;
import fpt.prm392.fe_salehunter.model.auth.LoginResponseModel;
import fpt.prm392.fe_salehunter.model.auth.RegisterRequestModel;
import fpt.prm392.fe_salehunter.model.auth.RegisterResponseModel;
import fpt.prm392.fe_salehunter.model.auth.ResetPasswordRequestModel;
import fpt.prm392.fe_salehunter.model.auth.RefreshTokenRequestModel;
import fpt.prm392.fe_salehunter.model.auth.ChangePasswordRequestModel;
import fpt.prm392.fe_salehunter.model.auth.ForgotPasswordRequestModel;
import fpt.prm392.fe_salehunter.model.SignUpModel;
import fpt.prm392.fe_salehunter.model.GoogleSocialAuthModel;
import fpt.prm392.fe_salehunter.model.SocialAuthResponseModel;
import fpt.prm392.fe_salehunter.model.StorePageModel;
import fpt.prm392.fe_salehunter.model.UpcItemDbResponseModel;
import fpt.prm392.fe_salehunter.model.UserModel;
import fpt.prm392.fe_salehunter.model.UserResponseModel;
import io.reactivex.BackpressureStrategy;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Repository {
    private Retrofit mainClient;
    private Retrofit upcItemDbClient;
    private Retrofit barcodeMonsterClient;

    //HEADERS
    public static final String AUTH_TOKEN_HEADER = "Authorization";

    public Repository() {
        mainClient = RetrofitClient.getMainInstance();
        upcItemDbClient = RetrofitClient.getUpcItemDbInstance();
        barcodeMonsterClient = RetrofitClient.getBarcodeMonsterInstance();
    }

    //Account Sign in/up & password reset
    public LiveData<Response<LoginResponseModel>> signIn(LoginRequestModel signInModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .login(signInModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<RegisterResponseModel>> signUp(RegisterRequestModel signUpModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .register(signUpModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    // TODO: Social auth endpoints not implemented in new API yet
    /*
    public LiveData<Response<SocialAuthResponseModel>> googleAuth(GoogleSocialAuthModel socialAuthModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .googleAuth(socialAuthModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );

    }

    public LiveData<Response<SocialAuthResponseModel>> facebookAuth(FacebookSocialAuthModel socialAuthModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .facebookAuth(socialAuthModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );

    }
    */

    public LiveData<Response<BaseResponseModel>> sendEmailVerification(String email){
        ForgotPasswordRequestModel forgotPasswordModel = new ForgotPasswordRequestModel();
        forgotPasswordModel.setEmail(email);
        
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .forgotPassword(forgotPasswordModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> verifyToken(String token){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .verifyToken(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> resetPassword(ResetPasswordRequestModel resetPasswordModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .resetPassword(resetPasswordModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    //Token refresh for expired access tokens
    public LiveData<Response<LoginResponseModel>> refreshToken(String refreshToken){
        RefreshTokenRequestModel refreshTokenModel = new RefreshTokenRequestModel();
        refreshTokenModel.setRefreshToken(refreshToken);
        
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .refreshToken(refreshTokenModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    //User Data Calls
    public LiveData<Response<UserResponseModel>> getUser(String token){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getUserProfile(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<UserResponseModel>> updateUser(String token, UserModel userModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .updateUserProfile(token, userModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> changePassword(String token, ChangePasswordRequestModel changePasswordModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .changePassword(token, changePasswordModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<ProductsResponseModel>> searchProducts(String token, String query, Long storeId, String category, Double minPrice, Double maxPrice){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .searchProducts(token, query, storeId, category, minPrice, maxPrice)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );

    }

    public LiveData<Response<UpcItemDbResponseModel>> barcodeLookupUpcItemDb(String barcode){
        return LiveDataReactiveStreams.fromPublisher(
                upcItemDbClient.create(RetrofitInterface.class)
                        .barcodeLookupUpcItemDb(barcode)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BarcodeMonsterResponseModel>> barcodeLookupBarcodeMonster(String barcode){
        return LiveDataReactiveStreams.fromPublisher(
                barcodeMonsterClient.create(RetrofitInterface.class)
                        .barcodeLookupBarcodeMonster(barcode)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<ProductPageResponseModel>> getProduct(String token, long productId){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getProduct(token, productId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> addFavourite(String token, long productId){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .addToFavorites(token, productId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> removeFavourite(String token, long productId){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .removeFromFavorites(token, productId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> rateProduct(String token, long productId, ProductRateModel productRateModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .rateProduct(token, productId, productRateModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<ProductsResponseModel>> getRecommendedProducts(String token){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getRecommendedProducts(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<ProductsResponseModel>> getProductsViewsHistory(String token){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getViewHistory(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<ProductsResponseModel>> getFavoriteProducts(String token){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getFavoriteProducts(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );

    }

    public LiveData<Response<ProductsResponseModel>> getOnSaleProducts(String token){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getOnSaleProducts(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    //Store Calls
    public LiveData<Response<StorePageModel>> getStore(String token, long storeId){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getStore(token, storeId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );

    }

    public LiveData<Response<CreateStoreResponseModel>> createStore(String token,CreateStoreRequestModel createStoreRequestModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .createStore(token,createStoreRequestModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> updateStore(String token, long storeId,CreateStoreRequestModel createStoreRequestModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .updateStore(token, storeId,createStoreRequestModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    //Dashboard Calls
    public LiveData<Response<BaseResponseModel>> createProduct(String token, CreateProductRequestModel createProductRequestModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .createProduct(token, createProductRequestModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> updateProduct(String token, long productId, CreateProductRequestModel createProductRequestModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .updateProduct(token, productId, createProductRequestModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> deleteProduct(String token, long productId){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .deleteProduct(token, productId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }
}
