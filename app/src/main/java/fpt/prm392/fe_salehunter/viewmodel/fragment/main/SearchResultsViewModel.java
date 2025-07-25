package fpt.prm392.fe_salehunter.viewmodel.fragment.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashSet;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.ProductsResponseModel;
import fpt.prm392.fe_salehunter.model.SortAndFilterModel;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import retrofit2.Response;

public class SearchResultsViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<Response<ProductsResponseModel>> initialLoadedProducts;
    private LiveData<Response<ProductsResponseModel>> onlinePaginatedProducts;
    private LiveData<Response<ProductsResponseModel>> localPaginatedProducts;

    private String token;
    private String language;
    private String keyword;
    private LatLng userLocation;
    private SortAndFilterModel sortAndFilterModel;
    private HashSet<String> categories, brands;
    private long cursorLastOnlineItem, cursorLastLocalItem;
    private int productsCountPerPage = 10; //Min 10

    public SearchResultsViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();

        token = UserAccountManager.getToken(application,UserAccountManager.TOKEN_TYPE_BEARER);
        language = "en";
        userLocation = new LatLng(0,0);
        sortAndFilterModel = new SortAndFilterModel();
        categories = new HashSet<>();
        brands = new HashSet<>();
    }

    public LiveData<Response<ProductsResponseModel>> loadResults(){
        // Updated to use new simplified API structure
        initialLoadedProducts = repository.searchProducts(
                token,
                keyword,
                null, // storeId - null for all stores
                sortAndFilterModel.getCategory(),
                (double) sortAndFilterModel.getMinPrice(),
                (double) sortAndFilterModel.getMaxPrice()
        );
        return initialLoadedProducts;
    }

    public void removeObserverInitialLoadedProducts(LifecycleOwner lifecycleOwner){
        if (initialLoadedProducts != null) {
            initialLoadedProducts.removeObservers(lifecycleOwner);
        }
    }

    public LiveData<Response<ProductsResponseModel>> loadMoreOnlineResults(){
        // Updated to use new simplified API structure (treating as same as loadResults for now)
        onlinePaginatedProducts = repository.searchProducts(
                token,
                keyword,
                null, // storeId - null for all stores
                sortAndFilterModel.getCategory(),
                (double) sortAndFilterModel.getMinPrice(),
                (double) sortAndFilterModel.getMaxPrice()
        );
        return onlinePaginatedProducts;
    }

    public void removeObserverOnlineLoadedProducts(LifecycleOwner lifecycleOwner){
        if (onlinePaginatedProducts != null) {
            onlinePaginatedProducts.removeObservers(lifecycleOwner);
        }
    }

    public LiveData<Response<ProductsResponseModel>> loadMoreLocalResults(){
        // Updated to use new simplified API structure (treating as same as loadResults for now)
        localPaginatedProducts = repository.searchProducts(
                token,
                keyword,
                null, // storeId - null for all stores
                sortAndFilterModel.getCategory(),
                (double) sortAndFilterModel.getMinPrice(),
                (double) sortAndFilterModel.getMaxPrice()
        );
        return localPaginatedProducts;
    }

    public void removeObserverLocalLoadedProducts(LifecycleOwner lifecycleOwner){
        if (localPaginatedProducts != null) {
            localPaginatedProducts.removeObservers(lifecycleOwner);
        }
    }

    public LiveData<Response<BaseResponseModel>> addFavourite(long productId){
        return repository.addFavourite(token,productId);
    }

    public LiveData<Response<BaseResponseModel>> removeFavourite(long productId){
        return repository.removeFavourite(token,productId);
    }

    public int getProductsCountPerPage() {
        return productsCountPerPage;
    }

    public void setProductsCountPerPage(int productsCountPerPage) {
        this.productsCountPerPage = productsCountPerPage;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public LatLng getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
    }

    public SortAndFilterModel getSortAndFilterModel() {
        return sortAndFilterModel;
    }

    public void setSortAndFilterModel(SortAndFilterModel sortAndFilterModel) {
        this.sortAndFilterModel = sortAndFilterModel;
    }

    public long getCursorLastOnlineItem() {
        return cursorLastOnlineItem;
    }

    public void setCursorLastOnlineItem(long cursorLastOnlineItem) {
        this.cursorLastOnlineItem = cursorLastOnlineItem;
    }

    public long getCursorLastLocalItem() {
        return cursorLastLocalItem;
    }

    public void setCursorLastLocalItem(long cursorLastLocalItem) {
        this.cursorLastLocalItem = cursorLastLocalItem;
    }

    public HashSet<String> getCategories() {
        return categories;
    }

    public void clearCategories() {
        categories.clear();
    }

    public void addCategory(String category) {
        categories.add(category);
    }

    public HashSet<String> getBrands() {
        return brands;
    }

    public void clearBrands() {
        brands.clear();
    }

    public void addBrand(String brand) {
        brands.add(brand);
    }
}
