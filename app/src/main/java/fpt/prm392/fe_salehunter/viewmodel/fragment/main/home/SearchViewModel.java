package fpt.prm392.fe_salehunter.viewmodel.fragment.main.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.ProductsResponseModel;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import retrofit2.Response;

public class SearchViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<Response<ProductsResponseModel>> recommendedProducts;
    private String token;

    public SearchViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
        token = UserAccountManager.getToken(application,UserAccountManager.TOKEN_TYPE_BEARER);
    }

    public LiveData<Response<ProductsResponseModel>> getRecommendedProducts(){
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

    public LiveData<Response<BaseResponseModel>> addFavourite(long productId){
        return repository.addFavourite(token,productId);
    }

    public LiveData<Response<BaseResponseModel>> removeFavourite(long productId){
        return repository.removeFavourite(token,productId);
    }
}
