package fpt.prm392.fe_salehunter.viewmodel.fragment.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.product.ProductModel;
import fpt.prm392.fe_salehunter.model.product.ProductSearchRequestModel;
import fpt.prm392.fe_salehunter.model.request.PagingRequest;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.store.StorePageData;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Response;

/**
 * Refactored DashboardViewModel using product search API for pagination
 * instead of calling getStore API repeatedly
 */
public class DashboardViewModel extends AndroidViewModel {
    private final Repository repository;
    private LiveData<Response<BaseResponseModel<StorePageData>>> storeData;
    private LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> productsData;

    @Getter
    @Setter
    private long storeId;
    private int currentPage = 0;
    private final int productsCountPerPage = 20;
    private final String token;
    @Setter
    @Getter
    private StorePageData storePageData;

    public DashboardViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
        token = UserAccountManager.getToken(application, UserAccountManager.TOKEN_TYPE_BEARER);
    }

    /**
     * Load store data (basic info only, no products)
     */
    public LiveData<Response<BaseResponseModel<StorePageData>>> getStore() {
        storeData = repository.getStore(token, storeId);
        return storeData;
    }

    /**
     * Load products using search API with store filter
     */
    public LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> getStoreProducts() {
        var searchRequest = ProductSearchRequestModel.builder()
                .query("") // Empty query to get all products
                .storeId(storeId) // Filter by store ID
                .category(null)
                .minPrice(null)
                .maxPrice(null)
                .sortBy("newest") // Sort by newest for dashboard
                .brand(null)
                .build();
        
        var pagingRequest = PagingRequest.builder()
                .page(0) // Start from first page
                .size(productsCountPerPage)
                .build();

        productsData = repository.searchProducts(token, pagingRequest, searchRequest);
        return productsData;
    }

    /**
     * Load more products for pagination
     */
    public LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> getNextPage() {
        currentPage++;
        
        var searchRequest = ProductSearchRequestModel.builder()
                .query("") // Empty query to get all products
                .storeId(storeId) // Filter by store ID
                .category(null)
                .minPrice(null)
                .maxPrice(null)
                .sortBy("newest") // Sort by newest for dashboard
                .brand(null)
                .build();
        
        var pagingRequest = PagingRequest.builder()
                .page(currentPage)
                .size(productsCountPerPage)
                .build();

        productsData = repository.searchProducts(token, pagingRequest, searchRequest);
        return productsData;
    }

    /**
     * Reset pagination when refreshing
     */
    public void resetPagination() {
        currentPage = 0;
    }

    public void removeObserverStoreData(LifecycleOwner lifecycleOwner) {
        if (storeData != null) {
            storeData.removeObservers(lifecycleOwner);
        }
    }

    public void removeObserverProductsData(LifecycleOwner lifecycleOwner) {
        if (productsData != null) {
            productsData.removeObservers(lifecycleOwner);
        }
    }

    public LiveData<Response<BaseResponseModel<Object>>> addFavourite(long productId) {
        return repository.addFavourite(token, productId);
    }

    public LiveData<Response<BaseResponseModel<Object>>> removeFavourite(long productId) {
        return repository.removeFavourite(token, productId);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getProductsCountPerPage() {
        return productsCountPerPage;
    }
}
