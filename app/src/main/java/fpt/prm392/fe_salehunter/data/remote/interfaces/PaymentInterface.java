package fpt.prm392.fe_salehunter.data.remote.interfaces;

import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.payment.PaymentInitRequestModel;
import fpt.prm392.fe_salehunter.model.payment.PaymentInitResponseModel;
import fpt.prm392.fe_salehunter.model.payment.PaymentVerificationResponseModel;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PaymentInterface {

    @Headers({"client: mobile"})
    @POST("api/Payment/initiate")
    Observable<Response<BaseResponseModel<PaymentInitResponseModel>>> initiatePayment(@Header("Authorization") String token, @Body PaymentInitRequestModel paymentRequest);

    @Headers({"client: mobile"})
    @GET("api/Payment/transaction/{transactionId}")
    Observable<Response<BaseResponseModel<PaymentVerificationResponseModel>>> getPaymentTransaction(@Header("Authorization") String token, @Path("transactionId") String transactionId);

    @Headers({"client: mobile"})
    @GET("api/Payment/history/{userId}")
    Observable<Response<BaseResponseModel<java.util.List<PaymentVerificationResponseModel>>>> getPaymentHistory(@Header("Authorization") String token, @Path("userId") long userId);
}
