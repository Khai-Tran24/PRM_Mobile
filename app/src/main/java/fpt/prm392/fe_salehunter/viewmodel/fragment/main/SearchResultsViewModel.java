package fpt.prm392.fe_salehunter.viewmodel.fragment.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import org.maplibre.android.geometry.LatLng;

import java.util.HashSet;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.product.ProductSearchRequestModel;
import fpt.prm392.fe_salehunter.model.request.PagingRequest;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.product.ProductModel;

import java.util.ArrayList;

import fpt.prm392.fe_salehunter.model.SortAndFilterModel;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Response;

public class SearchResultsViewModel extends AndroidViewModel {
    private final Repository repository;
    private LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> initialLoadedProducts;
    private LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> onlinePaginatedProducts;
    private LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> localPaginatedProducts;

    private final String token;
    @Setter
    @Getter
    private String language;
    @Setter
    @Getter
    private String query;
    @Setter
    @Getter
    private LatLng userLocation;
    @Setter
    @Getter
    private SortAndFilterModel sortAndFilterModel;
    @Getter
    private final HashSet<String> categories;
    @Getter
    private final HashSet<String> brands;
    @Setter
    @Getter
    private Integer cursorLastOnlineItem;
    @Setter
    @Getter
    private Integer cursorLastLocalItem;
    @Setter
    @Getter
    private Integer productsCountPerPage = 10;

    public SearchResultsViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();

        token = UserAccountManager.getToken(application, UserAccountManager.TOKEN_TYPE_BEARER);
        language = "en";
        userLocation = new LatLng(0, 0);
        sortAndFilterModel = new SortAndFilterModel();
        categories = new HashSet<>();
        brands = new HashSet<>();
    }

    public LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> loadResults() {
        // Updated to use new simplified API structure
        var searchRequest = ProductSearchRequestModel.builder()
                .query(query)
                .storeId(null)
                .category(sortAndFilterModel.getCategory())
                .minPrice((double) sortAndFilterModel.getMinPrice())
                .maxPrice((double) sortAndFilterModel.getMaxPrice())
                .sortBy(sortAndFilterModel.getBackendSortBy())
                .brand(sortAndFilterModel.getBrand())
                .build();
        var pagingRequest = PagingRequest.builder()
                .page(0) // Start from the first page
                .size(productsCountPerPage)
                .build();
        initialLoadedProducts = repository.searchProducts(
                token,
                pagingRequest,
                searchRequest
        );
        return initialLoadedProducts;
    }

    public void removeObserverInitialLoadedProducts(LifecycleOwner lifecycleOwner) {
        if (initialLoadedProducts != null) {
            initialLoadedProducts.removeObservers(lifecycleOwner);
        }
    }

    public LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> loadMoreOnlineResults() {
        onlinePaginatedProducts = repository.searchProducts(
                token,
                PagingRequest.builder()
                        .page(cursorLastOnlineItem)
                        .size(productsCountPerPage)
                        .build(),
                ProductSearchRequestModel.builder()
                        .query(query)
                        .storeId(null)
                        .category(sortAndFilterModel.getCategory())
                        .minPrice((double) sortAndFilterModel.getMinPrice())
                        .maxPrice((double) sortAndFilterModel.getMaxPrice())
                        .sortBy(sortAndFilterModel.getBackendSortBy())
                        .brand(sortAndFilterModel.getBrand())
                        .build()
        );
        return onlinePaginatedProducts;
    }

    public void removeObserverOnlineLoadedProducts(LifecycleOwner lifecycleOwner) {
        if (onlinePaginatedProducts != null) {
            onlinePaginatedProducts.removeObservers(lifecycleOwner);
        }
    }

    public LiveData<Response<BaseResponseModel<ArrayList<ProductModel>>>> loadMoreLocalResults() {
        // Updated to use new simplified API structure (treating as same as loadResults for now)
        localPaginatedProducts = repository.searchProducts(
                token,
                PagingRequest.builder()
                        .page(cursorLastLocalItem)
                        .size(productsCountPerPage)
                        .build(),
                ProductSearchRequestModel.builder()
                        .query(query)
                        .storeId(null)
                        .category(sortAndFilterModel.getCategory())
                        .minPrice((double) sortAndFilterModel.getMinPrice())
                        .maxPrice((double) sortAndFilterModel.getMaxPrice())
                        .sortBy(sortAndFilterModel.getBackendSortBy())
                        .brand(sortAndFilterModel.getBrand())
                        .build()
        );
        return localPaginatedProducts;
    }

    public void removeObserverLocalLoadedProducts(LifecycleOwner lifecycleOwner) {
        if (localPaginatedProducts != null) {
            localPaginatedProducts.removeObservers(lifecycleOwner);
        }
    }

    public LiveData<Response<BaseResponseModel<Object>>> addFavourite(long productId) {
        return repository.addFavourite(token, productId);
    }

    public LiveData<Response<BaseResponseModel<Object>>> removeFavourite(long productId) {
        return repository.removeFavourite(token, productId);
    }

    public void clearCategories() {
        categories.clear();
    }

    public void addCategory(String category) {
        categories.add(category);
    }

    public void clearBrands() {
        brands.clear();
    }

    public void addBrand(String brand) {
        brands.add(brand);
    }
}
