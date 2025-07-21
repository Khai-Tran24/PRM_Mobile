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

public class OnSaleViewModel extends AndroidViewModel {
    private final Repository repository;
    private LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> products;
    private final String token;

    public OnSaleViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
        token = UserAccountManager.getToken(application,UserAccountManager.TOKEN_TYPE_BEARER);
    }

    public LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> getOnSaleProducts(){
        if (products == null) {
            products = repository.getOnSaleProducts(token);
        }
        return products;
    }

    public void removeObserverOfProducts(LifecycleOwner lifecycleOwner){
        if (products != null) {
            products.removeObservers(lifecycleOwner);
        }
    }

    public LiveData<Response<BaseResponseModel<Object>>> addFavourite(long productId){
        return repository.addFavourite(token,productId);
    }

    public LiveData<Response<BaseResponseModel<Object>>> removeFavourite(long productId){
        return repository.removeFavourite(token,productId);
    }
}
