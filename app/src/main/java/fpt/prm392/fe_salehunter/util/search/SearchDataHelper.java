package fpt.prm392.fe_salehunter.util.search;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.adapter.ProductsSearchResultsAdapter;
import fpt.prm392.fe_salehunter.model.product.ProductModel;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.util.DialogsProvider;
import fpt.prm392.fe_salehunter.viewmodel.fragment.main.SearchResultsViewModel;
import lombok.Getter;
import lombok.Setter;

/**
 * Helper class for managing search data operations and API interactions
 */
public class SearchDataHelper {
    
    private final Context context;
    private final SearchResultsViewModel viewModel;
    private final LifecycleOwner lifecycleOwner;

    // Getters
    // State tracking
    @Getter
    private boolean endOfOnlineProducts = false;
    @Getter
    private boolean endOfLocalProducts = false;
    
    // Callbacks
    public interface SearchDataCallback {
        void onSearchStarted();
        void onSearchCompleted(ArrayList<ProductModel> products);
        void onSearchError(String error);
        void onMoreProductsLoaded(ArrayList<ProductModel> products, boolean isOnline);
        void onEndOfProducts(boolean isOnline);
    }
    
    @Setter
    private SearchDataCallback callback;
    
    public SearchDataHelper(Context context, SearchResultsViewModel viewModel, 
                           LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
    }
    
    /**
     * Load initial search results
     */
    public void loadResults() {
        if (callback != null) {
            callback.onSearchStarted();
        }
        
        // Reset pagination state
        resetPaginationState();
        
        viewModel.loadResults().observe(lifecycleOwner, response -> {
            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    if (response.body() == null || response.body().getData() == null) {
                        handleError(context.getString(R.string.No_Products_Found));
                        return;
                    }

                    viewModel.removeObserverInitialLoadedProducts(lifecycleOwner);
                    handleSuccessfulResults(response.body().getData());
                    break;
                    
                case BaseResponseModel.FAILED_INVALID_DATA:
                    handleError("Invalid Search Query Request");
                    break;
                    
                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    handleError(context.getString(R.string.Please_Check_your_connection));
                    break;
                    
                default:
                    handleError(context.getString(R.string.Server_Error) + " Code: " + response.code());
            }
        });
    }
    
    /**
     * Load more online products
     */
    public void loadMoreOnlineProducts(ProductsSearchResultsAdapter adapter) {
        if (endOfOnlineProducts) return;
        
        adapter.setOnlineProductsLoading(true);
        
        viewModel.loadMoreOnlineResults().observe(lifecycleOwner, response -> {
            adapter.setOnlineProductsLoading(false);

            if (response == null || response.body() == null) {
                Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show();
                return;
            }

            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    viewModel.removeObserverOnlineLoadedProducts(lifecycleOwner);
                    
                    if (response.body().getData() == null || response.body().getData().isEmpty()) {
                        endOfOnlineProducts = true;
                        if (callback != null) {
                            callback.onEndOfProducts(true);
                        }
                        return;
                    }
                    
                    ArrayList<ProductModel> products = response.body().getData();
                    handleMoreProductsLoaded(products, true);
                    break;
                    
                case BaseResponseModel.FAILED_INVALID_DATA:
                    showError("Query Error", "Invalid Search Query Request");
                    break;
                    
                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    Toast.makeText(context, "Loading Failed", Toast.LENGTH_SHORT).show();
                    break;
                    
                default:
                    Toast.makeText(context, "Server Error | Code: " + response.code(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Load more local products
     */
    public void loadMoreLocalProducts(ProductsSearchResultsAdapter adapter) {
        if (endOfLocalProducts) return;
        
        adapter.setLocalProductsLoading(true);
        
        viewModel.loadMoreLocalResults().observe(lifecycleOwner, response -> {
            adapter.setLocalProductsLoading(false);

            if (response == null || response.body() == null) {
                Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show();
                return;
            }
            
            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    viewModel.removeObserverLocalLoadedProducts(lifecycleOwner);
                    
                    if (response.body().getData() == null || response.body().getData().isEmpty()) {
                        endOfLocalProducts = true;
                        if (callback != null) {
                            callback.onEndOfProducts(false);
                        }
                        return;
                    }
                    
                    ArrayList<ProductModel> products = response.body().getData();
                    handleMoreProductsLoaded(products, false);
                    break;
                    
                case BaseResponseModel.FAILED_INVALID_DATA:
                    showError("Load Query Error", "Invalid Search Query Request, Cannot load more products");
                    break;
                    
                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    Toast.makeText(context, "Loading Failed", Toast.LENGTH_SHORT).show();
                    break;
                    
                default:
                    Toast.makeText(context, "Server Error | Code: " + response.code(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Add product to favorites
     */
    public void setFavourite(long productId, boolean favourite) {
        if (favourite) {
            viewModel.addFavourite(productId).observe(lifecycleOwner, response -> {
                if (response.code() != BaseResponseModel.SUCCESSFUL_CREATION) {
                    Toast.makeText(context, "Error " + response.code(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            viewModel.removeFavourite(productId).observe(lifecycleOwner, response -> {
                if (response.code() != BaseResponseModel.SUCCESSFUL_DELETED) {
                    Toast.makeText(context, "Error " + response.code(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    /**
     * Handle successful search results
     */
    private void handleSuccessfulResults(ArrayList<ProductModel> products) {
        if (products == null || products.isEmpty()) {
            if (callback != null) {
                callback.onSearchCompleted(new ArrayList<>());
            }
            return;
        }
        
        // Clear existing categories and brands before processing new results
        viewModel.clearCategories();
        viewModel.clearBrands();
        
        // Process products and update view model state
        for (ProductModel product : products) {
            if (product.getCategory() != null && !product.getCategory().trim().isEmpty()) {
                viewModel.addCategory(product.getCategory().trim());
            }
            if (product.getBrand() != null && !product.getBrand().trim().isEmpty()) {
                viewModel.addBrand(product.getBrand().trim());
            }
        }
        
        android.util.Log.d("SearchDataHelper", "Processed products - Categories: " + viewModel.getCategories().size() + ", Brands: " + viewModel.getBrands().size());
        
        // Set pagination cursors
        ArrayList<ProductModel> onlineProducts = new ArrayList<>();
        ArrayList<ProductModel> localProducts = new ArrayList<>();
        
        for (ProductModel product : products) {
            if (product.getStoreType().equals(ProductModel.ONLINE_STORE)) {
                onlineProducts.add(product);
            } else {
                localProducts.add(product);
            }
        }
        
        if (!onlineProducts.isEmpty()) {
            viewModel.setCursorLastOnlineItem(onlineProducts.size());
            if (onlineProducts.size() < viewModel.getProductsCountPerPage()) {
                endOfOnlineProducts = true;
            }
        }
        
        if (!localProducts.isEmpty()) {
            viewModel.setCursorLastLocalItem(localProducts.size());
            if (localProducts.size() < viewModel.getProductsCountPerPage()) {
                endOfLocalProducts = true;
            }
        }


        if (callback != null) {
            callback.onSearchCompleted(products);
        }
    }
    
    /**
     * Handle more products loaded
     */
    private void handleMoreProductsLoaded(ArrayList<ProductModel> products, boolean isOnline) {
        // Update view model state
        for (ProductModel product : products) {
            if (product.getCategory() != null && !product.getCategory().trim().isEmpty()) {
                viewModel.addCategory(product.getCategory().trim());
            }
            if (product.getBrand() != null && !product.getBrand().trim().isEmpty()) {
                viewModel.addBrand(product.getBrand().trim());
            }
        }
        
        // Update cursor
        if (isOnline) {
            viewModel.setCursorLastOnlineItem(products.size());
            if (products.size() < viewModel.getProductsCountPerPage()) {
                endOfOnlineProducts = true;
                if (callback != null) {
                    callback.onEndOfProducts(true);
                }
            }
        } else {
            viewModel.setCursorLastLocalItem(products.size());
            if (products.size() < viewModel.getProductsCountPerPage()) {
                endOfLocalProducts = true;
                if (callback != null) {
                    callback.onEndOfProducts(false);
                }
            }
        }
        
        if (callback != null) {
            callback.onMoreProductsLoaded(products, isOnline);
        }
    }
    
    /**
     * Handle errors
     */
    private void handleError(String error) {
        if (callback != null) {
            callback.onSearchError(error);
        }
    }
    
    /**
     * Show error dialog
     */
    private void showError(String title, String message) {
        if (context instanceof android.app.Activity) {
            DialogsProvider.get((android.app.Activity) context).messageDialog(title, message);
        }
    }
    
    /**
     * Reset pagination state for new search
     */
    public void resetPaginationState() {
        endOfOnlineProducts = false;
        endOfLocalProducts = false;
    }
    
    /**
     * Refresh search results
     */
    public void refreshResults() {
        resetPaginationState();
        loadResults();
    }
}
