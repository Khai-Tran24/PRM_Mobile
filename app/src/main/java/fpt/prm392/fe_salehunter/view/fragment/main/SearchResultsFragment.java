package fpt.prm392.fe_salehunter.view.fragment.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.OnMapReadyCallback;

import java.util.ArrayList;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.adapter.ProductsSearchResultsAdapter;
import fpt.prm392.fe_salehunter.databinding.FragmentSearchResultsBinding;
import fpt.prm392.fe_salehunter.model.product.ProductModel;
import fpt.prm392.fe_salehunter.model.SortAndFilterModel;
import fpt.prm392.fe_salehunter.util.MapLibreManager;
import fpt.prm392.fe_salehunter.util.search.SearchDataHelper;
import fpt.prm392.fe_salehunter.util.search.SearchLocationHelper;
import fpt.prm392.fe_salehunter.util.search.SearchMapHelper;
import fpt.prm392.fe_salehunter.util.search.SearchUIHelper;
import fpt.prm392.fe_salehunter.view.fragment.base.BaseFragment;
import fpt.prm392.fe_salehunter.viewmodel.fragment.main.SearchResultsViewModel;

/**
 * Refactored SearchResultsFragment with improved structure and separation of concerns
 * Uses helper classes to manage different aspects of functionality
 */
public class SearchResultsFragment extends BaseFragment<FragmentSearchResultsBinding, SearchResultsViewModel> 
        implements OnMapReadyCallback {
    
    // Core components
    private ProductsSearchResultsAdapter adapter;
    private MapView mapView;
    private boolean isMapViewActive = false;
    
    // Helper classes for different responsibilities
    private SearchUIHelper uiHelper;
    private SearchMapHelper mapHelper;
    private SearchLocationHelper locationHelper;
    private SearchDataHelper dataHelper;
    
    // Permission launchers
    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    
    public SearchResultsFragment() {
        // Required empty public constructor
    }
    
    @Override
    protected FragmentSearchResultsBinding createViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentSearchResultsBinding.inflate(inflater, container, false);
    }
    
    @Override
    protected Class<SearchResultsViewModel> getViewModelClass() {
        return SearchResultsViewModel.class;
    }
    
    @Override
    protected String getFragmentTitle() {
        return getString(R.string.Results);
    }
    
    @Override
    protected void onFragmentCreate(Bundle savedInstanceState) {
        super.onFragmentCreate(savedInstanceState);
        initializePermissionLaunchers();
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (binding == null) {
            binding = createViewBinding(inflater, container);
            
            // Initialize MapLibre and MapView
            MapLibreManager.initialize(getSafeContext());
            mapView = binding.resultMap;
            mapView.onCreate(savedInstanceState);
        }
        return binding.getRoot();
    }
    
    @Override
    protected void processArguments(Bundle arguments) {
        if (arguments != null && viewModel != null) {
            String query = arguments.getString("keyword");
            if (query != null) {
                viewModel.setQuery(query);
            }
        }
    }
    
    @Override
    protected void setupUI() {
        initializeHelpers();
        setupRecyclerView();
        setupMapView();
        setupViewToggle();
        setupCallbacks();
        
        // Display initial keyword
        if (viewModel != null) {
            uiHelper.updateKeywordDisplay(viewModel.getQuery());
        }
    }
    
    @Override
    protected void loadInitialData() {
        if (dataHelper != null) {
            dataHelper.loadResults();
        }
    }
    
    /**
     * Initialize helper classes
     */
    private void initializeHelpers() {
        // UI Helper
        uiHelper = new SearchUIHelper(getSafeContext(), binding, viewModel, getViewLifecycleOwner());
        uiHelper.setupUI();
        
        // Map Helper
        mapHelper = new SearchMapHelper(getSafeContext());
        
        // Location Helper
        locationHelper = new SearchLocationHelper(this, new SearchLocationHelper.LocationCallback() {
            @Override
            public void onLocationReceived(Location location) {
                handleLocationReceived(location);
            }
            
            @Override
            public void onLocationError(String error) {
                Toast.makeText(getSafeContext(), error, Toast.LENGTH_SHORT).show();
                uiHelper.setLocating(false);
            }
            
            @Override
            public void onPermissionRequired(String[] permissions) {
                locationPermissionLauncher.launch(permissions);
            }
        });
        
        // Data Helper
        dataHelper = new SearchDataHelper(getSafeContext(), viewModel, getViewLifecycleOwner());
        dataHelper.setCallback(new SearchDataHelper.SearchDataCallback() {
            @Override
            public void onSearchStarted() {
                uiHelper.setSearching(true);
            }
            
            @Override
            public void onSearchCompleted(ArrayList<ProductModel> products) {
                uiHelper.setSearching(false);
                handleSearchResults(products);
            }
            
            @Override
            public void onSearchError(String error) {
                uiHelper.setSearching(false);
                Toast.makeText(getSafeContext(), error, Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onMoreProductsLoaded(ArrayList<ProductModel> products, boolean isOnline) {
                handleMoreProductsLoaded(products, isOnline);
            }
            
            @Override
            public void onEndOfProducts(boolean isOnline) {
                // Products exhausted for this type
            }
        });
    }
    
    /**
     * Setup permission launchers
     */
    private void initializePermissionLaunchers() {
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean fineLocationGranted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
                    boolean coarseLocationGranted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION));
                    
                    if (fineLocationGranted && coarseLocationGranted) {
                        locationHelper.requestCurrentLocation();
                    } else {
                        handleLocationPermissionDenied();
                    }
                }
        );
    }
    
    /**
     * Setup RecyclerView and adapter
     */
    private void setupRecyclerView() {
        adapter = new ProductsSearchResultsAdapter(getSafeContext(), binding.resultRecyclerView);
        binding.resultRecyclerView.setLayoutManager(
                new LinearLayoutManager(getSafeContext(), LinearLayoutManager.VERTICAL, false));
        binding.resultRecyclerView.setAdapter(adapter);
        
        // Set up adapter listeners
        adapter.setLastItemReachedListener(new ProductsSearchResultsAdapter.LastItemReachedListener() {
            @Override
            public void onLastOnlineProductReached() {
                dataHelper.loadMoreOnlineProducts(adapter);
            }
            
            @Override
            public void onLastLocalProductReached() {
                dataHelper.loadMoreLocalProducts(adapter);
            }
        });
        
        adapter.setItemInteractionListener(new ProductsSearchResultsAdapter.ItemInteractionListener() {
            @Override
            public void onProductClicked(long productId, String storeType) {
                navigateToProductPage(productId);
            }
            
            @Override
            public void onProductAddedToFav(long productId, boolean favChecked) {
                dataHelper.setFavourite(productId, favChecked);
            }
        });
    }
    
    /**
     * Setup map view
     */
    private void setupMapView() {
        if (mapView != null) {
            mapView.getMapAsync(this);
        }
        
        // Map mode toggle (Standard/Satellite)
        binding.resultMapMode.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (mapHelper.isMapReady()) {
                mapHelper.toggleMapStyle(isChecked, () -> {
                    // Re-add markers after style change
                    mapHelper.addStoreMarkers(adapter);
                });
            }
        });
        
        // My location button
        binding.resultMyLocation.setOnClickListener(v -> {
            uiHelper.setLocating(true);
            locationHelper.requestCurrentLocation();
        });
    }
    
    /**
     * Setup view toggle functionality
     */
    private void setupViewToggle() {
        // Set up toggle button listeners
        binding.resultListView.setOnClickListener(v -> {
            if (!isMapViewActive) return; // Already in list view
            
            isMapViewActive = false;
            binding.resultRecyclerView.setVisibility(View.VISIBLE);
            binding.resultMapBottomSheet.setVisibility(View.GONE);
            
            // Update button states
            binding.resultListView.setChecked(true);
            binding.resultMapView.setChecked(false);
        });
        
        binding.resultMapView.setOnClickListener(v -> {
            if (isMapViewActive) return; // Already in map view
            
            isMapViewActive = true;
            binding.resultRecyclerView.setVisibility(View.GONE);
            binding.resultMapBottomSheet.setVisibility(View.VISIBLE);
            
            // Update button states
            binding.resultListView.setChecked(false);
            binding.resultMapView.setChecked(true);
            
            // Load stores on map if not already loaded
            if (mapHelper != null) {
                mapHelper.addStoreMarkers(adapter);
            }
        });
        
        // Set initial state - list view
        binding.resultListView.setChecked(true);
        binding.resultMapView.setChecked(false);
        binding.resultRecyclerView.setVisibility(View.VISIBLE);
        binding.resultMapBottomSheet.setVisibility(View.GONE);
    }
    
    /**
     * Setup callbacks for helper classes
     */
    private void setupCallbacks() {
        // UI Helper callbacks
        uiHelper.setSearchCallback(keyword -> {
            viewModel.setQuery(keyword);
            dataHelper.refreshResults();
        });
        
        uiHelper.setViewToggleCallback(new SearchUIHelper.ViewToggleCallback() {
            @Override
            public void onListViewSelected() {
                // Additional logic for list view if needed
            }
            
            @Override
            public void onMapViewSelected() {
                if (mapHelper.isMapReady()) {
                    mapHelper.addStoreMarkers(adapter);
                }
                
                // Switch to map view
                binding.resultMapView.performClick();
            }
        });
        
        uiHelper.setSortFilterCallback(new SearchUIHelper.SortFilterCallback() {
            @Override
            public void onSortChanged(SortAndFilterModel model) {
                viewModel.setSortAndFilterModel(model);
                
                if (model.getSortBy().equals(SortAndFilterModel.SORT_NEAREST_STORE)) {
                    uiHelper.setLocating(true);
                    locationHelper.requestCurrentLocation();
                } else {
                    adapter.clearProducts();
                    dataHelper.loadResults();
                }
            }
            
            @Override
            public void onFilterChanged(SortAndFilterModel model) {
                viewModel.setSortAndFilterModel(model);
                adapter.clearProducts();
                dataHelper.loadResults();
            }
        });
        
        // Swipe refresh
        binding.resultSwipeRefresh.setOnRefreshListener(() -> {
            adapter.clearProducts();
            dataHelper.refreshResults();
        });
    }
    
    /**
     * Handle map ready callback
     */
    @Override
    public void onMapReady(@NonNull MapLibreMap mapLibreMap) {
        mapHelper.initializeMap(mapLibreMap);
        
        // Add initial markers if products are already loaded
        if (adapter != null) {
            mapHelper.addStoreMarkers(adapter);
        }
        
        // Try to get user location
        if (hasLocationPermissions()) {
            locationHelper.requestCurrentLocation();
        }
    }
    
    /**
     * Handle search results
     */
    private void handleSearchResults(ArrayList<ProductModel> products) {
        if (products == null || products.isEmpty()) {
            adapter.showNoOnlineResultsFound();
            adapter.showNoLocalResultsFound();
            uiHelper.updateResultsCount(0);
            return;
        }
        
        // Separate online and local products
        ArrayList<ProductModel> onlineProducts = new ArrayList<>();
        ArrayList<ProductModel> localProducts = new ArrayList<>();
        int storeCount = 0;
        
        for (ProductModel product : products) {
            if (product.getStoreType().equals(ProductModel.ONLINE_STORE)) {
                onlineProducts.add(product);
            } else {
                localProducts.add(product);
                storeCount++;
            }
        }
        
        // Update adapter
        if (!onlineProducts.isEmpty()) {
            adapter.addOnlineProducts(onlineProducts);
        } else {
            adapter.showNoOnlineResultsFound();
        }
        
        if (!localProducts.isEmpty()) {
            adapter.addLocalProducts(localProducts);
        } else {
            adapter.showNoLocalResultsFound();
        }
        
        // Update UI
        uiHelper.updateResultsCount(products.size());
        uiHelper.updateStoreCount(storeCount);
        
        // Update map markers
        if (mapHelper.isMapReady()) {
            mapHelper.addStoreMarkers(adapter);
        }
    }
    
    /**
     * Handle more products loaded
     */
    private void handleMoreProductsLoaded(ArrayList<ProductModel> products, boolean isOnline) {
        if (isOnline) {
            adapter.addOnlineProducts(products);
        } else {
            adapter.addLocalProducts(products);
        }
        
        uiHelper.updateResultsCount(uiHelper.getTotalResultsCount() + products.size());
        
        // Update map markers
        if (mapHelper.isMapReady()) {
            mapHelper.addStoreMarkers(adapter);
        }
    }
    
    /**
     * Handle location received
     */
    private void handleLocationReceived(Location location) {
        uiHelper.setLocating(false);
        
        if (mapHelper.isMapReady()) {
            mapHelper.addUserLocationMarker(location);
            mapHelper.centerOnUserLocation(location, 15.0f);
        }
        
        // If sorting by nearest store, reload results
        if (viewModel.getSortAndFilterModel().getSortBy().equals(SortAndFilterModel.SORT_NEAREST_STORE)) {
            adapter.clearProducts();
            dataHelper.loadResults();
        }
        
        Toast.makeText(getSafeContext(), "Location updated", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Handle location permission denied
     */
    private void handleLocationPermissionDenied() {
        uiHelper.setLocating(false);
        
        if (viewModel.getSortAndFilterModel().getSortBy().equals(SortAndFilterModel.SORT_NEAREST_STORE)) {
            // Fallback to popularity sort
            viewModel.getSortAndFilterModel().setSortBy(SortAndFilterModel.SORT_POPULARITY);
            adapter.clearProducts();
            dataHelper.loadResults();
            Toast.makeText(getSafeContext(), "Sort by Nearest Store Canceled\nLocation Permission Denied", 
                    Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Navigate to product page
     */
    private void navigateToProductPage(long productId) {
        if (navController != null) {
            Bundle bundle = new Bundle();
            bundle.putLong("productId", productId);
            navController.navigate(R.id.action_searchResultsFragment_to_productPageFragment, bundle);
        }
    }
    
    /**
     * Check if location permissions are granted
     */
    private boolean hasLocationPermissions() {
        return getSafeContext() != null &&
               ActivityCompat.checkSelfPermission(getSafeContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
                       == PackageManager.PERMISSION_GRANTED &&
               ActivityCompat.checkSelfPermission(getSafeContext(), Manifest.permission.ACCESS_COARSE_LOCATION) 
                       == PackageManager.PERMISSION_GRANTED;
    }
    
    // MapView lifecycle methods
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
    
    @Override
    protected void onFragmentDestroy() {
        super.onFragmentDestroy();
        
        // Cleanup resources
        if (mapView != null) {
            mapView.onDestroy();
        }
        
        if (locationHelper != null) {
            locationHelper.cleanup();
        }
    }
}
