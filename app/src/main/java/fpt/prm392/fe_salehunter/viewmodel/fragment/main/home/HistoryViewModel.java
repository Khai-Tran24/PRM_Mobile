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

public class HistoryViewModel extends AndroidViewModel {
    private final Repository repository;
    private LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> products;
    private final String token;

    public HistoryViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
        token = UserAccountManager.getToken(application,UserAccountManager.TOKEN_TYPE_BEARER);
    }

    public LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> getViewedProducts(){
        if (products == null) {
            products = repository.getProductsViewsHistory(token);
        }
        return products;
    }

    public void removeObserverOfProducts(LifecycleOwner lifecycleOwner){
        if (products != null) {
            products.removeObservers(lifecycleOwner);
        }
    }
}
