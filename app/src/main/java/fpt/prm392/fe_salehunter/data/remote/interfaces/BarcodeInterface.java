package fpt.prm392.fe_salehunter.data.remote.interfaces;

import fpt.prm392.fe_salehunter.model.barcode.BarcodeMonsterResponseModel;
import fpt.prm392.fe_salehunter.model.barcode.OpenFoodFactsResponseModel;
import fpt.prm392.fe_salehunter.model.barcode.UpcItemDbResponseModel;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BarcodeInterface {

    @GET("prod/trial/lookup")
    Observable<Response<UpcItemDbResponseModel>> barcodeLookupUpcItemDb(@Query("upc") String barcode);

    @Headers("User-Agent: SaleHunter-Android/1.0")
    @GET("api/v2/product/{barcode}.json")
    Observable<Response<OpenFoodFactsResponseModel>> barcodeLookupOpenFoodFacts(@Path("barcode") String barcode);
}
