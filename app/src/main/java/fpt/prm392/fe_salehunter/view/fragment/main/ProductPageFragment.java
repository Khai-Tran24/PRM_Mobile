package fpt.prm392.fe_salehunter.view.fragment.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import com.google.android.material.tabs.TabLayoutMediator;

import org.maplibre.android.annotations.Marker;
import org.maplibre.android.annotations.MarkerOptions;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.OnMapReadyCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.adapter.ImagesSliderViewPagerAdapter;
import fpt.prm392.fe_salehunter.databinding.FragmentProductPageBinding;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.product.ProductPageModel;
import fpt.prm392.fe_salehunter.util.AppSettingsManager;
import fpt.prm392.fe_salehunter.util.DialogsProvider;
import fpt.prm392.fe_salehunter.util.MapLibreManager;
import fpt.prm392.fe_salehunter.view.activity.MainActivity;
import fpt.prm392.fe_salehunter.viewmodel.fragment.main.ProductPageViewModel;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;


public class ProductPageFragment extends Fragment implements OnMapReadyCallback {
    private FragmentProductPageBinding vb;
    private ProductPageViewModel viewModel;
    private NavController navController;

    private ImagesSliderViewPagerAdapter imageSliderAdapter;
    private CheckBox[] userRatingStars;
    private int userRatingNewValue;

    // Map related fields
    private MapView mapView;
    private MapLibreMap mapLibreMap;
    private Marker currentMarker;

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
    public void onStart() {
        super.onStart();
        if (mapView != null) {
            mapView.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setTitle(getString(R.string.Product));
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapView != null) {
            mapView.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
        vb = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProductPageViewModel.class);
        if (getArguments() != null) viewModel.setProductId(getArguments().getLong("productId"));

        new Handler().post(() -> {
            navController = ((MainActivity) requireActivity()).getAppNavController();
        });

        imageSliderAdapter = new ImagesSliderViewPagerAdapter(getContext());
        vb.productPageImagesSlider.setAdapter(imageSliderAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(vb.productPageImagesSliderIndicator, vb.productPageImagesSlider, (tab, position) -> {
        });
        tabLayoutMediator.attach();

        // Initialize MapLibre
        mapView = vb.productPageMap;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        vb.productPageSubmitRate.setVisibility(View.INVISIBLE);
        userRatingStars = new CheckBox[]{vb.productPageRateStar1, vb.productPageRateStar2, vb.productPageRateStar3, vb.productPageRateStar4, vb.productPageRateStar5};
        for (int i = 0; i < userRatingStars.length; i++) {
            final int index = i;
            userRatingStars[i].setOnClickListener((star) -> {
                for (int j = 0; j <= index; j++) userRatingStars[j].setChecked(true);
                for (int j = index + 1; j < userRatingStars.length; j++)
                    userRatingStars[j].setChecked(false);
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
            requireActivity().onBackPressed();
        });

        vb.productPageStore.setOnClickListener(image -> {
            Bundle bundle = new Bundle();
            bundle.putLong("storeId", viewModel.getProductPageModel().getStoreId());
            navController.navigate(R.id.action_productPageFragment_to_storePageFragment, bundle);
        });

        vb.productPageOpenSourcePageButton.setOnClickListener(button -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // Use default URL since backend doesn't provide source URL yet
            intent.setData(Uri.parse("https://sale-hunter.vercel.app/"));
            startActivity(intent);
        });

        vb.productPageShareProductButton.setOnClickListener(button -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            // Create shareable URL from product ID
            String shareableUrl = "https://sale-hunter.vercel.app/pid=" + viewModel.getProductPageModel().getId();
            intent.putExtra(Intent.EXTRA_TEXT, shareableUrl);
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
                    if (response.body() != null) {
                        viewModel.setProductPageModel(response.body().getData());
                        renderProductData();
                        vb.productPageLoadingPage.setVisibility(View.GONE);
                        vb.getRoot().startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.lay_on));
                    }
                    break;

                case BaseResponseModel.FAILED_NOT_FOUND:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Product_Not_Found), getString(R.string.Product_Not_Found_in_Server));
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    Toast.makeText(getContext(), "Loading Failed", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(getContext(), "Server Error | Code: " + response.code(), Toast.LENGTH_SHORT).show();
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

        // Use simplified model structure instead of complex nested calls
        if (renderDataInLocalLanguage())
            vb.productPageTitle.setText(productPageModel.getName()); // Fallback: no Arabic support yet
        else vb.productPageTitle.setText(productPageModel.getName());

        vb.productPageBrand.setText(productPageModel.getBrand() != null ? productPageModel.getBrand() : "");

        // Calculate price with sale percentage - use direct fields instead of complex nesting
        double salePercent = productPageModel.getSalePercent() != null ? productPageModel.getSalePercent() : 0;
        double basePrice = productPageModel.getCurrentPrice();
        Double productPrice = Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", basePrice - (basePrice * salePercent / 100)));

        vb.productPagePrice.setText(String.format(Locale.ENGLISH, "%.2f %s", productPrice, getString(R.string.currency)));
        vb.productPageSalePercent.setText(String.format(Locale.ENGLISH, "%.2f%s", basePrice, getString(R.string.sale_percent)));
        vb.productPageRate.setText(String.format(Locale.ENGLISH, "%.1f", productPageModel.getAverageRating()));
        vb.productPageViews.setText(String.format(Locale.ENGLISH, "%d", productPageModel.getTotalViews()));
        vb.productPageFavourite.setChecked(productPageModel.isFavorite());
        renderUserRating(0); // Default: user hasn't rated yet

        Glide.with(this)
                .load(productPageModel.getStoreImageUrl()) // Use direct field instead of nested getStore().getStoreLogo()
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .into(vb.productPageStore);

        ArrayList<String> productImagesLinks = new ArrayList<>();
        if (productPageModel.getImages() != null) {
            for (ProductPageModel.ProductImageDto i : productPageModel.getImages()) {
                String imageUrl = i.getImageUrl();
                if (imageUrl != null) {
                    productImagesLinks.add(imageUrl.replace("http://", "https://"));
                }
            }
        }
        imageSliderAdapter.addImages(productImagesLinks);
        if (productImagesLinks.size() == 1)
            vb.productPageImagesSliderIndicator.setVisibility(View.INVISIBLE);

        drawPriceTrackerChart(productPageModel.getPrices());

        // For now, treat all stores as physical stores since backend doesn't specify type
        // TODO: Add store type to backend response
        double storeLatitude = productPageModel.getStoreLatitude();
        double storeLongitude = productPageModel.getStoreLongitude();
        String storeName = productPageModel.getStoreName() != null ? productPageModel.getStoreName() : "";

        if (storeLatitude != 0 && storeLongitude != 0) {
            addProductOnMap(storeLatitude, storeLongitude, storeName);
            vb.productPageOpenSourcePageButton.setVisibility(View.GONE);

            String fullDescription;
            if (renderDataInLocalLanguage())
                fullDescription = productPageModel.getDescription();
            else
                fullDescription = productPageModel.getDescription() != null ? productPageModel.getDescription() : "";

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

            } else vb.productPageDescription.setText(fullDescription);

        } else {
            // Online store or no location data
            vb.productPageMapSection.setVisibility(View.GONE);
            vb.productPageNavigateButton.setVisibility(View.GONE);
            vb.productPageDescription.setVisibility(View.GONE);
        }

        if (salePercent == 0) vb.productPageSalePercent.setVisibility(View.INVISIBLE);

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

    @Override
    public void onMapReady(@NonNull MapLibreMap mapLibreMap) {
        this.mapLibreMap = mapLibreMap;
        
        // Configure map settings
        mapLibreMap.getUiSettings().setCompassEnabled(true);
        mapLibreMap.getUiSettings().setAllGesturesEnabled(false);
        
        // Set basic map style - using default OpenStreetMap style
        mapLibreMap.setStyle("https://demotiles.maplibre.org/style.json");
    }

    private void addProductOnMap(double lat, double lng, String storeName) {
        if (mapLibreMap == null) return;
        
        try {
            LatLng productLocation = new LatLng(lat, lng);
            
            // Remove existing marker
            if (currentMarker != null) {
                mapLibreMap.removeMarker(currentMarker);
            }
            
            // Add new marker
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(productLocation)
                    .title(storeName);
                    
            currentMarker = mapLibreMap.addMarker(markerOptions);

            // Move camera to location
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(productLocation)
                    .zoom(Math.max(mapLibreMap.getCameraPosition().zoom, 12.0))
                    .build();

            mapLibreMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Map Marker Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void drawPriceTrackerChart(List<ProductPageModel.ProductPriceHistoryDto> prices) {
        LineChartView lineChartView = vb.productPageChart;

        List<AxisValue> xValues = new ArrayList<>();
        List<PointValue> points = new ArrayList<>();

        xValues.add(new AxisValue(0).setLabel("untracked"));
        points.add(new PointValue(0, 0));

        for (int i = 1; i <= prices.size(); i++) {
            xValues.add(new AxisValue(i).setLabel(dateTimeConvert(prices.get(i - 1).getCreatedDate())));
            points.add(new PointValue(i, prices.get(i - 1).getPrice()));
        }

        Resources.Theme theme = requireContext().getTheme();
        Line line = new Line();
        line.setValues(points);
        line.setColor(getResources().getColor(R.color.lightModeprimary, theme));
        line.setStrokeWidth(3);
        line.setHasPoints(true);
        line.setShape(ValueShape.CIRCLE);
        line.setPointColor(getResources().getColor(R.color.lightModeprimary, theme));
        line.setPointRadius(5);
        line.setHasLabels(true);
        line.setHasLines(true);
        line.setCubic(true);
        line.setFilled(true);
        line.setAreaTransparency(50);

        Axis axisX = new Axis();
        axisX.setValues(xValues);
        axisX.setName("Date");
        axisX.setLineColor(Color.GRAY);
        axisX.setTextColor(Color.GRAY);
        axisX.setTextSize(14);
        axisX.setTypeface(Typeface.DEFAULT);
        axisX.setHasLines(true);
        axisX.setMaxLabelChars(10);
        axisX.setHasTiltedLabels(true);

        Axis axisY = new Axis();
        axisY.setLineColor(Color.GRAY);
        axisY.setTextColor(Color.GRAY);
        axisY.setTextSize(10);
        axisY.setHasLines(true);

        //setting chart data
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData chartData = new LineChartData();
        chartData.setLines(lines);
        chartData.setAxisXBottom(axisX);
        chartData.setAxisYLeft(axisY);
        chartData.finish();

        //send chart data to view
        lineChartView.setLineChartData(chartData);
        //set chart view settings
        lineChartView.setInteractive(true);
        lineChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        lineChartView.setMaxZoom(10);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

    }

    public String dateTimeConvert(String dateTime) {
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss";
        String outputPattern = "dd/MM/yyyy";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);
        outputFormat.setTimeZone(TimeZone.getDefault());
        try {
            Date date = inputFormat.parse(dateTime);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (Exception e) {
            android.util.Log.e("DateTimeConversion", "Error converting date: " + e.getMessage());
        }

        return "-";
    }

    void setFavourite(boolean favourite) {
        if (favourite) {
            viewModel.addFavourite().observe(getViewLifecycleOwner(), response -> {
                if (response.code() != BaseResponseModel.SUCCESSFUL_CREATION && response.code() != BaseResponseModel.SUCCESSFUL_OPERATION)
                    Toast.makeText(getContext(), "Error " + response.code(), Toast.LENGTH_SHORT).show();
            });
        } else {
            viewModel.removeFavourite().observe(getViewLifecycleOwner(), response -> {
                if (response.code() != BaseResponseModel.SUCCESSFUL_DELETED && response.code() != BaseResponseModel.SUCCESSFUL_OPERATION)
                    Toast.makeText(getContext(), "Error " + response.code(), Toast.LENGTH_SHORT).show();
            });
        }

    }

    void rateProduct(int rating) {
        viewModel.rateProduct(rating).observe(getViewLifecycleOwner(), response -> {
            if (response.code() != BaseResponseModel.SUCCESSFUL_OPERATION)
                Toast.makeText(getContext(), "Error " + response.code(), Toast.LENGTH_SHORT).show();
            else viewModel.getProductPageModel().setUserRating(userRatingNewValue);
        });
    }

}