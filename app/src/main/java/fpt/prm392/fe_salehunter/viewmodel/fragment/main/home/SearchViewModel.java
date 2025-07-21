package fpt.prm392.fe_salehunter.viewmodel.fragment.main.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.product.ProductModel;

import java.util.ArrayList;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import retrofit2.Response;

public class SearchViewModel extends AndroidViewModel {
    private final Repository repository;
    private final String token;
    private LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> recommendedProducts;

    public SearchViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
        token = UserAccountManager.getToken(application,UserAccountManager.TOKEN_TYPE_BEARER);
    }

    public LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> getRecommendedProducts(){
        if (recommendedProducts == null) {
            recommendedProducts = repository.getRecommendedProducts(token);
        }
        return recommendedProducts;
    }

    public void removeObserverRecommendedProducts(LifecycleOwner lifecycleOwner){
        if (recommendedProducts != null) {
            recommendedProducts.removeObservers(lifecycleOwner);
        }
    }

    public LiveData<Response<BaseResponseModel<Object>>> addFavourite(long productId){
        return repository.addFavourite(token,productId);
    }

    public LiveData<Response<BaseResponseModel<Object>>> removeFavourite(long productId){
        return repository.removeFavourite(token,productId);
    }
}
