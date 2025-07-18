package fpt.prm392.fe_salehunter.view.fragment.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.adapter.ImagesSliderViewPagerAdapter;
import fpt.prm392.fe_salehunter.databinding.FragmentProductPageBinding;
import fpt.prm392.fe_salehunter.model.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.ProductPageModel;
import fpt.prm392.fe_salehunter.util.AppSettingsManager;
import fpt.prm392.fe_salehunter.util.DialogsProvider;
import fpt.prm392.fe_salehunter.view.activity.MainActivity;
import fpt.prm392.fe_salehunter.viewmodel.fragment.main.ProductPageViewModel;

public class ProductPageFragment extends Fragment {
    private FragmentProductPageBinding vb;
    private ProductPageViewModel viewModel;
    private NavController navController;

    private ImagesSliderViewPagerAdapter imageSliderAdapter;
    private CheckBox[] userRatingStars;
    private int userRatingNewValue;

    private GoogleMap googleMap;

    public ProductPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vb = FragmentProductPageBinding.inflate(inflater, container, false);
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
        ((MainActivity) getActivity()).setTitle(getString(R.string.Product));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProductPageViewModel.class);
        if (getArguments() != null) viewModel.setProductId(getArguments().getLong("productId"));

        new Handler().post(() -> {
            navController = ((MainActivity) getActivity()).getAppNavController();
        });

        imageSliderAdapter = new ImagesSliderViewPagerAdapter(getContext());
        vb.productPageImagesSlider.setAdapter(imageSliderAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(vb.productPageImagesSliderIndicator, vb.productPageImagesSlider, (tab, position) -> { });
        tabLayoutMediator.attach();

        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.product_page_map)).getMapAsync(map -> {
            this.googleMap = map;

            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(false);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        });

        vb.productPageSubmitRate.setVisibility(View.INVISIBLE);
        userRatingStars = new CheckBox[]{vb.productPageRateStar1, vb.productPageRateStar2, vb.productPageRateStar3, vb.productPageRateStar4, vb.productPageRateStar5};
        for (int i = 0; i < userRatingStars.length; i++) {
            final int index = i;
            userRatingStars[i].setOnClickListener((star) -> {
                for (int j = 0; j <= index; j++) userRatingStars[j].setChecked(true);
                for (int j = index + 1; j < userRatingStars.length; j++) userRatingStars[j].setChecked(false);
                userRatingNewValue = index + 1;
                showRatingSubmit(userRatingNewValue != viewModel.getProductPageModel().getUserRating());
            });
        }

        vb.productPageFavourite.setOnCheckedChangeListener((button, checked) -> {
            if (checked) vb.productPageFavouriteText.setText(R.string.Remove);
            else vb.productPageFavouriteText.setText(R.string.Add);
        });

        vb.productPageFavourite.setOnClickListener(button -> {
            setFavourite(vb.productPageFavourite.isChecked());
        });

        vb.productPageFavouriteText.setOnClickListener(button -> {
            vb.productPageFavourite.performClick();
        });

        vb.productPageBack.setOnClickListener(button -> {
            getActivity().onBackPressed();
        });

        vb.productPageStore.setOnClickListener(image -> {
            Bundle bundle = new Bundle();
            bundle.putLong("storeId", viewModel.getProductPageModel().getStoreId());
            navController.navigate(R.id.action_productPageFragment_to_storePageFragment, bundle);
        });

        vb.productPageOpenSourcePageButton.setOnClickListener(button -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(viewModel.getProductPageModel().getSourceUrl()));
            startActivity(intent);
        });

        vb.productPageShareProductButton.setOnClickListener(button -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, viewModel.getProductPageModel().getShareableUrl());
            startActivity(Intent.createChooser(intent, viewModel.getProductPageModel().getName()));
        });

        vb.productPageNavigateButton.setOnClickListener(button -> {
            Uri uri = Uri.parse("google.navigation:q=" + viewModel.getProductPageModel().getStoreLatitude() + "," + viewModel.getProductPageModel().getStoreLongitude());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
        });

        vb.productPageSubmitRate.setOnClickListener(button -> {
            rateProduct(userRatingNewValue);
            showRatingSubmit(false);
        });

        loadProductData();
    }

    void loadProductData() {
        vb.productPageLoadingPage.setVisibility(View.VISIBLE);

        viewModel.getProduct().observe(getViewLifecycleOwner(), response -> {
            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    if (response.body() != null && response.body().getProduct() != null) {
                        viewModel.setProductPageModel(response.body().getProduct());
                        renderProductData();
                        vb.productPageLoadingPage.setVisibility(View.GONE);
                        vb.getRoot().startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.lay_on));
                    } else {
                        Toast.makeText(getContext(), "Dữ liệu sản phẩm rỗng", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case BaseResponseModel.FAILED_NOT_FOUND:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Product_Not_Found), getString(R.string.Product_Not_Found_in_Server));
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    Toast.makeText(getContext(), "Tải dữ liệu thất bại", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(getContext(), "Lỗi server | Mã: " + response.code(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean renderDataInLocalLanguage() {
        switch (AppSettingsManager.getLanguageKey(getContext())) {
            case AppSettingsManager.LANGUAGE_ENGLISH:
                return false;
            case AppSettingsManager.LANGUAGE_ARABIC:
                return true;
            default:
                String systemLanguage = Locale.getDefault().getLanguage();
                return systemLanguage.equals(AppSettingsManager.LANGUAGE_ARABIC);
        }
    }

    void renderProductData() {
        ProductPageModel productPageModel = viewModel.getProductPageModel();
        if (productPageModel == null) {
            Toast.makeText(getContext(), "Dữ liệu sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ngôn ngữ Ả Rập không được hỗ trợ trong JSON, sử dụng tên tiếng Anh
        vb.productPageTitle.setText(productPageModel.getName());

        vb.productPageBrand.setText(productPageModel.getBrand());
        vb.productPagePrice.setText(String.format("%.2f", productPageModel.getFinalPrice()) + getString(R.string.currency));
        vb.productPageSalePercent.setText(productPageModel.getSalePercent() + getString(R.string.sale_percent));
        vb.productPageRate.setText(String.format("%.1f", productPageModel.getAverageRating()));
        vb.productPageViews.setText(String.valueOf(0)); // JSON không có views, đặt mặc định là 0
        vb.productPageFavourite.setChecked(productPageModel.isFavorite());
        renderUserRating(productPageModel.getUserRating());

        Glide.with(this)
                .load(productPageModel.getStoreImageUrl())
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .into(vb.productPageStore);

        ArrayList<String> productImagesLinks = new ArrayList<>();
        for (ProductPageModel.ProductImage i : productPageModel.getImages()) {
            productImagesLinks.add(i.getImageUrl());
        }
        imageSliderAdapter.addImages(productImagesLinks);
        if (productImagesLinks.size() == 1) vb.productPageImagesSliderIndicator.setVisibility(View.INVISIBLE);

        // Ẩn biểu đồ giá vì JSON không có prices
        vb.productPageChart.setVisibility(View.GONE);

        // Kiểm tra cửa hàng trực tuyến dựa trên tọa độ
        if (productPageModel.getStoreLatitude() == null || productPageModel.getStoreLongitude() == null) {
            vb.productPageMapSection.setVisibility(View.GONE);
            vb.productPageNavigateButton.setVisibility(View.GONE);
            vb.productPageDescription.setVisibility(View.GONE);
        } else {
            addProductOnMap(productPageModel.getStoreLatitude(), productPageModel.getStoreLongitude(), productPageModel.getStoreName());
            vb.productPageOpenSourcePageButton.setVisibility(View.GONE);

            String fullDescription = productPageModel.getDescription();
            if (fullDescription.length() > 120) {
                String shortDescription = fullDescription.substring(0, 110) + "... ";

                SpannableString readMore = new SpannableString(getString(R.string.Read_More));
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        vb.productPageDescription.animate().alpha(0).setDuration(250).withEndAction(() -> {
                            vb.productPageDescription.setText(fullDescription);
                            vb.productPageDescription.animate().alpha(1f).setDuration(250).start();
                        }).start();
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                };

                readMore.setSpan(clickableSpan, 0, readMore.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                vb.productPageDescription.setText(shortDescription);
                vb.productPageDescription.append(readMore);
                vb.productPageDescription.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                vb.productPageDescription.setText(fullDescription);
            }
        }

        if (productPageModel.getSalePercent() == 0) vb.productPageSalePercent.setVisibility(View.INVISIBLE);
    }

    void renderUserRating(int stars) {
        if (stars > 0) userRatingStars[stars - 1].performClick();
    }

    void showRatingSubmit(boolean show) {
        if (show == (vb.productPageSubmitRate.getVisibility() == View.VISIBLE)) return;

        if (show) {
            vb.productPageSubmitRate.setVisibility(View.VISIBLE);
            vb.productPageSubmitRate.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in));
        } else {
            vb.productPageSubmitRate.setVisibility(View.INVISIBLE);
            vb.productPageSubmitRate.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out));
        }
    }

    private void addProductOnMap(double lat, double lng, String storeName) {
        try {
            LatLng productLocation = new LatLng(lat, lng);
            googleMap.addMarker(new MarkerOptions().position(productLocation).title(storeName).icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_store_mark))).showInfoWindow();

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(productLocation)
                    .zoom(googleMap.getCameraPosition().zoom < 8 ? 8 : googleMap.getCameraPosition().zoom)
                    .build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi đánh dấu trên bản đồ", Toast.LENGTH_SHORT).show();
        }
    }

    void setFavourite(boolean favourite) {
        if (favourite) {
            viewModel.addFavourite().observe(getViewLifecycleOwner(), response -> {
                if (response.code() != BaseResponseModel.SUCCESSFUL_CREATION)
                    Toast.makeText(getContext(), "Lỗi " + response.code(), Toast.LENGTH_SHORT).show();
            });
        } else {
            viewModel.removeFavourite().observe(getViewLifecycleOwner(), response -> {
                if (response.code() != BaseResponseModel.SUCCESSFUL_DELETED)
                    Toast.makeText(getContext(), "Lỗi " + response.code(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    void rateProduct(int rating) {
        viewModel.rateProduct(rating).observe(getViewLifecycleOwner(), response -> {
            if (response.code() != BaseResponseModel.SUCCESSFUL_OPERATION)
                Toast.makeText(getContext(), "Lỗi " + response.code(), Toast.LENGTH_SHORT).show();
            else viewModel.getProductPageModel().setUserRating(userRatingNewValue);
        });
    }
}