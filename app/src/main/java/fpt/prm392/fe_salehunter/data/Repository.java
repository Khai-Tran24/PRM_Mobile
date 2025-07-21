package fpt.prm392.fe_salehunter.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import java.util.ArrayList;

import fpt.prm392.fe_salehunter.data.remote.RetrofitClient;
import fpt.prm392.fe_salehunter.data.remote.RetrofitInterface;
import fpt.prm392.fe_salehunter.model.auth.LoginDataModel;
import fpt.prm392.fe_salehunter.model.barcode.BarcodeMonsterResponseModel;
import fpt.prm392.fe_salehunter.model.barcode.OpenFoodFactsResponseModel;
import fpt.prm392.fe_salehunter.model.product.ProductModel;
import fpt.prm392.fe_salehunter.model.product.ProductPageModel;
import fpt.prm392.fe_salehunter.model.product.ProductSearchRequestModel;
import fpt.prm392.fe_salehunter.model.request.PagingRequest;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.request.CreateProductRequestModel;
import fpt.prm392.fe_salehunter.model.request.CreateStoreRequestModel;
import fpt.prm392.fe_salehunter.model.product.ProductRateModel;
import fpt.prm392.fe_salehunter.model.auth.LoginRequestModel;
import fpt.prm392.fe_salehunter.model.auth.RegisterRequestModel;
import fpt.prm392.fe_salehunter.model.auth.RegisterResponseModel;
import fpt.prm392.fe_salehunter.model.auth.ResetPasswordRequestModel;
import fpt.prm392.fe_salehunter.model.auth.RefreshTokenRequestModel;
import fpt.prm392.fe_salehunter.model.auth.ChangePasswordRequestModel;
import fpt.prm392.fe_salehunter.model.auth.ForgotPasswordRequestModel;
import fpt.prm392.fe_salehunter.model.store.StorePageData;
import fpt.prm392.fe_salehunter.model.store.StoreModel;
import fpt.prm392.fe_salehunter.model.barcode.UpcItemDbResponseModel;
import fpt.prm392.fe_salehunter.model.user.UserModel;
import fpt.prm392.fe_salehunter.model.payment.PaymentInitRequestModel;
import fpt.prm392.fe_salehunter.model.payment.PaymentInitResponseModel;
import fpt.prm392.fe_salehunter.model.payment.PaymentVerificationResponseModel;
import fpt.prm392.fe_salehunter.model.chat.ChatConversationModel;
import fpt.prm392.fe_salehunter.model.chat.SendMessageRequestModel;
import fpt.prm392.fe_salehunter.model.chat.SendMessageResponseModel;
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
    private Retrofit openFoodFactsClient;

    //HEADERS
    public static final String AUTH_TOKEN_HEADER = "Authorization";

    public Repository() {
        mainClient = RetrofitClient.getMainInstance();
        upcItemDbClient = RetrofitClient.getUpcItemDbInstance();
        openFoodFactsClient = RetrofitClient.getOpenFoodFactsInstance();
    }

    //Account Sign in/up & password reset
    public LiveData<Response<BaseResponseModel<LoginDataModel>>> signIn(LoginRequestModel signInModel) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .login(signInModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<RegisterResponseModel>> signUp(RegisterRequestModel signUpModel) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .register(signUpModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
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

    public LiveData<Response<BaseResponseModel<Object>>> sendEmailVerification(String email) {
        ForgotPasswordRequestModel forgotPasswordModel = new ForgotPasswordRequestModel();
        forgotPasswordModel.setEmail(email);

        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .forgotPassword(forgotPasswordModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<Object>>> verifyToken(String token) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .verifyToken(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<Object>>> resetPassword(ResetPasswordRequestModel resetPasswordModel) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .resetPassword(resetPasswordModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    //Token refresh for expired access tokens
    public LiveData<Response<BaseResponseModel<LoginDataModel>>> refreshToken(String refreshToken) {
        RefreshTokenRequestModel refreshTokenModel = new RefreshTokenRequestModel();
        refreshTokenModel.setRefreshToken(refreshToken);

        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .refreshToken(refreshTokenModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    //User Data Calls
    public LiveData<Response<BaseResponseModel<UserModel>>> getUser(String token) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getUserProfile(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<UserModel>>> updateUser(String token, UserModel userModel) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .updateUserProfile(token, userModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<Object>>> changePassword(String token, ChangePasswordRequestModel changePasswordModel) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .changePassword(token, changePasswordModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> searchProducts(
            String token, PagingRequest pagingRequest, ProductSearchRequestModel requestModel

    ) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .searchProducts(
                                token,
                                pagingRequest.getPage(),
                                pagingRequest.getSize(),
                                requestModel.getQuery(),
                                requestModel.getStoreId(),
                                requestModel.getCategory(),
                                requestModel.getMinPrice(),
                                requestModel.getMaxPrice(),
                                requestModel.getSortBy(),
                                requestModel.getBrand()
                        )
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );

    }

    public LiveData<Response<UpcItemDbResponseModel>> barcodeLookupUpcItemDb(String barcode) {
        return LiveDataReactiveStreams.fromPublisher(
                upcItemDbClient.create(RetrofitInterface.class)
                        .barcodeLookupUpcItemDb(barcode)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<OpenFoodFactsResponseModel>> barcodeLookupOpenFoodFacts(String barcode) {
        return LiveDataReactiveStreams.fromPublisher(
                openFoodFactsClient.create(RetrofitInterface.class)
                        .barcodeLookupOpenFoodFacts(barcode)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<ProductPageModel>>> getProduct(String token, long productId) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getProduct(token, productId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<Object>>> addFavourite(String token, long productId) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .addToFavorites(token, productId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<Object>>> removeFavourite(String token, long productId) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .removeFromFavorites(token, productId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<Object>>> rateProduct(String token, long productId, ProductRateModel productRateModel) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .rateProduct(token, productId, productRateModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> getRecommendedProducts(String token) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getRecommendedProducts(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> getProductsViewsHistory(String token) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getViewHistory(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> getFavoriteProducts(String token) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getFavoriteProducts(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );

    }

    public LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> getOnSaleProducts(String token) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getOnSaleProducts(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    //Store Calls
    public LiveData<Response<BaseResponseModel<StorePageData>>> getStore(String token, long storeId) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getStore(token, storeId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );

    }

    public LiveData<Response<BaseResponseModel<StoreModel>>> createStore(String token, CreateStoreRequestModel createStoreRequestModel) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .createStore(token, createStoreRequestModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<Object>>> updateStore(String token, long storeId, CreateStoreRequestModel createStoreRequestModel) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .updateStore(token, storeId, createStoreRequestModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    //Dashboard Calls
    public LiveData<Response<BaseResponseModel<ProductModel>>> createProduct(String token, CreateProductRequestModel createProductRequestModel) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .createProduct(token, createProductRequestModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<ProductModel>>> updateProduct(String token, long productId, CreateProductRequestModel createProductRequestModel) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .updateProduct(token, productId, createProductRequestModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<Object>>> deleteProduct(String token, long productId) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .deleteProduct(token, productId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    // ================ PAYMENT METHODS ================
    public LiveData<Response<BaseResponseModel<PaymentInitResponseModel>>> initiatePayment(String token, PaymentInitRequestModel paymentRequest) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .initiatePayment(token, paymentRequest)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<PaymentVerificationResponseModel>>> verifyPayment(String token, String transactionId) {
        return getPaymentTransaction(token, transactionId);
    }

    public LiveData<Response<BaseResponseModel<PaymentVerificationResponseModel>>> getPaymentTransaction(String token, String transactionId) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getPaymentTransaction(token, transactionId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<java.util.List<PaymentVerificationResponseModel>>>> getPaymentHistory(String token, long userId) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getPaymentHistory(token, userId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    // Chat methods
    public LiveData<Response<BaseResponseModel<SendMessageResponseModel>>> sendMessage(String token, SendMessageRequestModel request) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .sendMessage(token, request)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<java.util.List<ChatConversationModel>>>> getConversations(String token) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getConversations(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel<ChatConversationModel>>> getConversation(String token, long conversationId) {
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getConversation(token, conversationId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(exception -> {
                            exception.printStackTrace();

                            if (exception.getClass() == HttpException.class)
                                return Response.error(((HttpException) exception).code(), ResponseBody.create(null, ""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null, ""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }
}
