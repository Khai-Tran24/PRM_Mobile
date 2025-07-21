package fpt.prm392.fe_salehunter.util.store;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.adapter.ProductsCardAdapter;
import fpt.prm392.fe_salehunter.databinding.FragmentStorePageBinding;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.product.ProductModel;
import fpt.prm392.fe_salehunter.model.user.UserModel;
import fpt.prm392.fe_salehunter.util.DialogsProvider;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import fpt.prm392.fe_salehunter.viewmodel.fragment.main.StorePageViewModel;

/**
 * Helper class for handling store page data operations
 */
public class StorePageDataHelper {

    /**
     * Load store data from API
     */
    public static void loadStoreData(Context context, LifecycleOwner lifecycleOwner,
                                     StorePageViewModel viewModel, FragmentStorePageBinding binding,
                                     StoreDataCallback callback) {
        binding.storePageLoadingPage.setVisibility(View.VISIBLE);

        viewModel.getStore().observe(lifecycleOwner, response -> {
            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    if (response.body() != null) {
                        viewModel.setStorePageData(response.body().getData());
                        binding.storePageLoadingPage.setVisibility(View.GONE);
                        if (callback != null) {
                            callback.onStoreDataLoaded();
                        }
                    }
                    break;

                case BaseResponseModel.FAILED_NOT_FOUND:
                    if (context instanceof android.app.Activity) {
                        DialogsProvider.get((android.app.Activity) context).messageDialog(
                                context.getString(R.string.Store_Not_Found),
                                context.getString(R.string.Store_Not_Found_in_Server));
                    } else {
                        Toast.makeText(context, "Store not found", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    Toast.makeText(context, "Loading Failed", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(context, "Server Error | Code: " + response.code(), Toast.LENGTH_SHORT).show();
            }

            viewModel.removeObserverStoreData(lifecycleOwner);
        });
    }

    /**
     * Load more products for pagination
     */
    public static void loadMoreProducts(Context context, LifecycleOwner lifecycleOwner,
                                        StorePageViewModel viewModel, ProductsCardAdapter adapter,
                                        boolean[] endOfProducts) {
        if (endOfProducts[0]) return;

        adapter.setLoading(true);

        viewModel.getNextPage().observe(lifecycleOwner, response -> {
            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    adapter.setLoading(false);

                    if (response.body().getData() == null || response.body().getData().isEmpty()) {
                        endOfProducts[0] = true;
                        return;
                    }

                    viewModel.removeObserverStoreData(lifecycleOwner);
                    adapter.addProducts(response.body().getData());
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
     * Render initial products to the adapter
     */
    public static void renderInitialProducts(Context context, StorePageViewModel viewModel,
                                             ProductsCardAdapter adapter, FragmentStorePageBinding binding,
                                             boolean[] endOfProducts) {
        ArrayList<ProductModel> products = viewModel.getStorePageData().getProducts();
        if (products.size() == 0) {
            endOfProducts[0] = true;
            binding.storePageNoProducts.setVisibility(View.VISIBLE);

            // Add null safety check for user
            UserModel currentUser = UserAccountManager.getUser(context);
            if (currentUser != null && viewModel.getStoreId() == currentUser.getStoreId()) {
                binding.storePageNoProductsTitle.setText(R.string.You_dont_have_products);
            }
        } else {
            adapter.addProducts(products);
        }
    }

    /**
     * Interface for store data loading callback
     */
    public interface StoreDataCallback {
        void onStoreDataLoaded();
    }
}
