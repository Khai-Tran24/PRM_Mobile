package fpt.prm392.fe_salehunter.viewmodel.fragment.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.ProductPageModel;
import fpt.prm392.fe_salehunter.model.ProductPageResponseModel;
import fpt.prm392.fe_salehunter.model.ProductRateModel;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import retrofit2.Response;

public class ProductPageViewModel extends AndroidViewModel {
    private Repository repository;

    private long productId;
    private String token;
    private ProductPageModel productPageModel;

    public ProductPageViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();

        token = UserAccountManager.getToken(application,UserAccountManager.TOKEN_TYPE_BEARER);
    }

    public LiveData<Response<ProductPageResponseModel>> getProduct(){
        return repository.getProduct(token,productId);
    }

    public LiveData<Response<BaseResponseModel>> addFavourite(){
        return repository.addFavourite(token,productId);
    }

    public LiveData<Response<BaseResponseModel>> removeFavourite(){
        return repository.removeFavourite(token,productId);
    }

    public LiveData<Response<BaseResponseModel>> rateProduct(int rating){
        ProductRateModel productRateModel = new ProductRateModel();
        productRateModel.setRating(rating);
        return repository.rateProduct(token,productId,productRateModel);
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public ProductPageModel getProductPageModel() {
        return productPageModel;
    }

    public void setProductPageModel(ProductPageModel productPageModel) {
        this.productPageModel = productPageModel;
    }
}
