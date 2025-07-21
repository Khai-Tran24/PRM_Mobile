package fpt.prm392.fe_salehunter.viewmodel.fragment.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.product.ProductPageModel;
import fpt.prm392.fe_salehunter.model.product.ProductRateModel;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Response;

public class ProductPageViewModel extends AndroidViewModel {
    private final Repository repository;

    @Setter
    private long productId;
    private final String token;
    @Setter
    @Getter
    private ProductPageModel productPageModel;

    public ProductPageViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();

        token = UserAccountManager.getToken(application,UserAccountManager.TOKEN_TYPE_BEARER);
    }

    public LiveData<Response<BaseResponseModel<ProductPageModel>>> getProduct(){
        return repository.getProduct(token,productId);
    }

    public LiveData<Response<BaseResponseModel<Object>>> addFavourite(){
        return repository.addFavourite(token,productId);
    }

    public LiveData<Response<BaseResponseModel<Object>>> removeFavourite(){
        return repository.removeFavourite(token,productId);
    }

    public LiveData<Response<BaseResponseModel<Object>>> rateProduct(int rating){
        ProductRateModel productRateModel = new ProductRateModel();
        productRateModel.setRating(rating);
        return repository.rateProduct(token,productId,productRateModel);
    }
}
