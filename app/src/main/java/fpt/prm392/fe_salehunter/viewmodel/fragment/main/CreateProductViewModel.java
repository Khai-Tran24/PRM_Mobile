package fpt.prm392.fe_salehunter.viewmodel.fragment.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.product.ProductModel;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.request.CreateProductRequestModel;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import retrofit2.Response;

public class CreateProductViewModel extends AndroidViewModel {
    private final Repository repository;
    private final String token;
    private LiveData<Response<BaseResponseModel<ProductModel>>> createProductObserver;
    private LiveData<Response<BaseResponseModel<ProductModel>>> updateProductObserver;
    private LiveData<Response<BaseResponseModel<Object>>> deleteProductObserver;

    public CreateProductViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
        token = UserAccountManager.getToken(application, UserAccountManager.TOKEN_TYPE_BEARER);
    }

    public LiveData<Response<BaseResponseModel<ProductModel>>> createProduct(CreateProductRequestModel requestModel) {
        createProductObserver = repository.createProduct(token, requestModel);
        return createProductObserver;
    }

    public void removeObserverCreateProduct(LifecycleOwner lifecycleOwner) {
        createProductObserver.removeObservers(lifecycleOwner);
    }

    public LiveData<Response<BaseResponseModel<ProductModel>>> updateProduct(Long productId, CreateProductRequestModel requestModel) {
        updateProductObserver = repository.updateProduct(token, productId, requestModel);
        return updateProductObserver;
    }

    public void removeObserverUpdateProduct(LifecycleOwner lifecycleOwner) {
        updateProductObserver.removeObservers(lifecycleOwner);
    }

    public LiveData<Response<BaseResponseModel<Object>>> deleteProduct(long storeId, long productId) {
        deleteProductObserver = repository.deleteProduct(token, productId);
        return deleteProductObserver;
    }

    public void removeObserverDeleteProduct(LifecycleOwner lifecycleOwner) {
        deleteProductObserver.removeObservers(lifecycleOwner);
    }

}
