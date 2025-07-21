package fpt.prm392.fe_salehunter.view.fragment.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.adapter.ProductsCardAdapter;
import fpt.prm392.fe_salehunter.databinding.FragmentStorePageBinding;
import fpt.prm392.fe_salehunter.model.product.ProductModel;
import fpt.prm392.fe_salehunter.model.store.StoreModel;
import fpt.prm392.fe_salehunter.model.user.UserModel;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import fpt.prm392.fe_salehunter.view.activity.MainActivity;
import fpt.prm392.fe_salehunter.viewmodel.fragment.main.StorePageViewModel;
import fpt.prm392.fe_salehunter.helper.StorePageDataHelper;
import fpt.prm392.fe_salehunter.helper.StorePageUIHelper;

public class StorePageFragment extends Fragment {
    private FragmentStorePageBinding vb;
    private StorePageViewModel viewModel;
    private NavController navController;
    private ProductsCardAdapter adapter;
    private boolean endOfProducts = false;
    private int toolbarOffset = -1000;

    public StorePageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (vb == null) vb = FragmentStorePageBinding.inflate(inflater, container, false);
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
        ((MainActivity) requireActivity()).setTitle(getString(R.string.Store));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (viewModel != null) return;

        viewModel = new ViewModelProvider(this).get(StorePageViewModel.class);
        if (getArguments() != null) viewModel.setStoreId(getArguments().getLong("storeId"));

        new Handler().post(() -> {
            navController = ((MainActivity) getActivity()).getAppNavController();
        });

        setupAppBarAnimation();
        setupRecyclerView();
        loadStoreData();
    }

    private void setupAppBarAnimation() {
        vb.storePageAppbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset > toolbarOffset) {
                vb.storePageExpandedLabelToolbar.animate().alpha(1.0f).setDuration(150).withStartAction(() -> {
                    vb.storePageExpandedLabelToolbar.setVisibility(View.VISIBLE);
                }).start();
            } else if (verticalOffset < toolbarOffset) {
                vb.storePageExpandedLabelToolbar.animate().alpha(0).setDuration(150).withEndAction(() -> {
                    vb.storePageExpandedLabelToolbar.setVisibility(View.GONE);
                }).start();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ProductsCardAdapter(getContext(), vb.storePageRecyclerView);
        vb.storePageRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        vb.storePageRecyclerView.setAdapter(adapter);

        adapter.setItemInteractionListener(new ProductsCardAdapter.ItemInteractionListener() {
            @Override
            public void onProductClicked(long productId, String storeType) {
                Bundle bundle = new Bundle();
                bundle.putLong("productId", productId);
                navController.navigate(R.id.action_storePageFragment_to_productPageFragment, bundle);
            }

            @Override
            public void onProductAddedToFav(long productId, boolean favChecked) {
                StorePageDataHelper.setFavourite(getContext(), productId, favChecked, getViewLifecycleOwner());
            }
        });

        adapter.setLastItemReachedListener(this::loadMoreProducts);
    }

    void loadStoreData() {
        vb.storePageLoadingPage.setVisibility(View.VISIBLE);

        StorePageDataHelper.loadStoreData(getContext(), getViewLifecycleOwner(), viewModel, vb, () -> {
            StorePageUIHelper.renderStoreData(getContext(), viewModel, vb);
            vb.storePageLoadingPage.setVisibility(View.GONE);
            toolbarOffset = StorePageUIHelper.renderStoreData(getContext(), viewModel, vb);
            
            // Load products using search API
            boolean[] endOfProductsArray = {false};
            StorePageDataHelper.renderInitialProducts(getContext(), getViewLifecycleOwner(), viewModel, adapter, vb, endOfProductsArray);
            endOfProducts = endOfProductsArray[0];
        });
    }

    void loadMoreProducts() {
        if (endOfProducts) return;

        boolean[] endOfProductsArray = {endOfProducts};
        StorePageDataHelper.loadMoreProducts(getContext(), getViewLifecycleOwner(), viewModel, adapter, endOfProductsArray);
        endOfProducts = endOfProductsArray[0];
    }
}