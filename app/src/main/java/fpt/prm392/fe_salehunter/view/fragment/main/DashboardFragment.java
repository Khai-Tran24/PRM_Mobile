package fpt.prm392.fe_salehunter.view.fragment.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.adapter.ProductsListAdapter;
import fpt.prm392.fe_salehunter.databinding.FragmentDashboardBinding;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.product.ProductModel;
import fpt.prm392.fe_salehunter.model.store.StorePageData;
import fpt.prm392.fe_salehunter.util.DialogsProvider;
import fpt.prm392.fe_salehunter.view.activity.MainActivity;
import fpt.prm392.fe_salehunter.view.fragment.main.CreateProductFragment;
import fpt.prm392.fe_salehunter.view.fragment.main.CreateStoreFragment;
import fpt.prm392.fe_salehunter.viewmodel.fragment.main.DashboardViewModel;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding vb;
    private DashboardViewModel viewModel;
    private NavController navController;

    private ProductsListAdapter adapter;
    private boolean endOfProducts = false;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (vb == null) vb = FragmentDashboardBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vb = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setTitle(getString(R.string.Dashboard));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (viewModel != null) return;

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        if (getArguments() != null) viewModel.setStoreId(getArguments().getLong("storeId"));

        new Handler().post(() -> navController = ((MainActivity) requireActivity()).getAppNavController());

        adapter = new ProductsListAdapter(getContext(), vb.dashboardRecyclerView);
        vb.dashboardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        vb.dashboardRecyclerView.setAdapter(adapter);

        adapter.setItemInteractionListener(new ProductsListAdapter.ItemInteractionListener() {
            @Override
            public void onProductClicked(ProductModel product) {
                Bundle bundle = new Bundle();
                bundle.putInt(CreateProductFragment.ACTION_KEY, CreateProductFragment.ACTION_EDIT_PRODUCT);
                bundle.putString(CreateProductFragment.PRODUCT_DATA_KEY, new Gson().toJson(product));
                bundle.putLong(CreateProductFragment.STORE_ID_KEY, viewModel.getStorePageData().getId());
                navController.navigate(R.id.action_dashboardFragment_to_createProductFragment, bundle);
            }

            @Override
            public void onProductAddedToFav(long productId, boolean favChecked) {
                setFavourite(productId, favChecked);
            }
        });

        adapter.setLastItemReachedListener(this::loadMoreProducts);

        vb.dashboardAddProduct.setOnClickListener(button -> {
            Bundle bundle = new Bundle();
            bundle.putLong(CreateProductFragment.STORE_ID_KEY, viewModel.getStorePageData().getId());
            navController.navigate(R.id.action_dashboardFragment_to_createProductFragment, bundle);
        });

        vb.dashboardEditStore.setOnClickListener(button -> {
            Bundle bundle = new Bundle();
            bundle.putInt(CreateStoreFragment.ACTION_KEY, CreateStoreFragment.ACTION_EDIT_STORE);
            // Store data from StorePageData for backward compatibility
            bundle.putString(CreateStoreFragment.STORE_DATA_KEY, new Gson().toJson(viewModel.getStorePageData()));
            navController.navigate(R.id.action_dashboardFragment_to_createStoreFragment2, bundle);
        });

        vb.dashboardStoreCard.setVisibility(View.INVISIBLE);

        loadStoreData();
    }

    void loadStoreData() {
        vb.dashboardLoadingPage.setVisibility(View.VISIBLE);

        viewModel.getStore().observe(getViewLifecycleOwner(), response -> {

            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    if (response.body() != null) {
                        viewModel.setStorePageData(response.body().getData());
                        renderStoreData();
                        renderInitialProducts();
                        vb.dashboardLoadingPage.setVisibility(View.GONE);

                        vb.dashboardStoreCard.setVisibility(View.VISIBLE);
                        vb.dashboardStoreCard.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in));
                        vb.dashboardRecyclerView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_bottom));
                    }
                    break;

                case BaseResponseModel.FAILED_NOT_FOUND:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Store_Not_Found), "Store Not Found in Server.");
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    Toast.makeText(getContext(), getString(R.string.Loading_Failed), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(getContext(), "Server Error | Code: " + response.code(), Toast.LENGTH_SHORT).show();
            }

            viewModel.removeObserverStoreData(getViewLifecycleOwner());
        });

    }

    void renderStoreData() {
        StorePageData storeModel = viewModel.getStorePageData();

        var storeName = storeModel.getName();
        if (storeName == null || storeName.isEmpty())
            storeName = getString(R.string.No_Store_Name);
        vb.dashboardStoreName.setText(storeName);

        Glide.with(this)
                .load(storeModel.getLogoUrl())
                .placeholder(R.drawable.store_placeholder)
                .circleCrop()
                .into(vb.dashboardLogo);

        vb.dashboardStoreCategory.setText(storeModel.getCategory());
    }

    void renderInitialProducts() {
        // Load products using search API instead of from store data
        viewModel.getStoreProducts().observe(getViewLifecycleOwner(), response -> {
            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    if (response.body() != null && response.body().getData() != null) {
                        ArrayList<ProductModel> products = response.body().getData();
                        if (products.isEmpty()) {
                            endOfProducts = true;
                            vb.dashboardNoProducts.setVisibility(View.VISIBLE);
                        } else {
                            adapter.addProducts(products);
                            vb.dashboardNoProducts.setVisibility(View.GONE);
                        }
                    } else {
                        endOfProducts = true;
                        vb.dashboardNoProducts.setVisibility(View.VISIBLE);
                    }
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    Toast.makeText(getContext(), getString(R.string.Loading_Failed), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(getContext(), "Server Error | Code: " + response.code(), Toast.LENGTH_SHORT).show();
            }

            viewModel.removeObserverProductsData(getViewLifecycleOwner());
        });
    }

    void loadMoreProducts() {
        if (endOfProducts) return;

        adapter.setLoading(true);

        viewModel.getNextPage().observe(getViewLifecycleOwner(), response -> {
            adapter.setLoading(false);

            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    if (response.body() == null || response.body().getData() == null || response.body().getData().isEmpty()) {
                        endOfProducts = true;
                        return;
                    }

                    adapter.addProducts(response.body().getData());
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    Toast.makeText(getContext(), getString(R.string.Loading_Failed), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(getContext(), "Server Error | Code: " + response.code(), Toast.LENGTH_SHORT).show();
            }

            viewModel.removeObserverProductsData(getViewLifecycleOwner());
        });
    }

    void setFavourite(long productId, boolean favourite) {
        if (favourite) {
            viewModel.addFavourite(productId).observe(getViewLifecycleOwner(), response -> {
                if (response.code() != BaseResponseModel.SUCCESSFUL_CREATION)
                    Toast.makeText(getContext(), "Error" + response.code(), Toast.LENGTH_SHORT).show();
            });
        } else {
            viewModel.removeFavourite(productId).observe(getViewLifecycleOwner(), response -> {
                if (response.code() != BaseResponseModel.SUCCESSFUL_DELETED)
                    Toast.makeText(getContext(), "Error" + response.code(), Toast.LENGTH_SHORT).show();
            });
        }

    }
}