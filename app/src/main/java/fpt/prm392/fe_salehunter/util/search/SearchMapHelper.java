package fpt.prm392.fe_salehunter.util.search;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import org.maplibre.android.annotations.Marker;
import org.maplibre.android.annotations.MarkerOptions;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.Style;

import java.util.ArrayList;

import fpt.prm392.fe_salehunter.adapter.ProductsSearchResultsAdapter;
import fpt.prm392.fe_salehunter.model.product.ProductModel;
import fpt.prm392.fe_salehunter.model.user.UserModel;
import fpt.prm392.fe_salehunter.util.MapLibreManager;
import fpt.prm392.fe_salehunter.util.UserAccountManager;

/**
 * Helper class for managing map functionality in search results
 */
public class SearchMapHelper {
    
    private MapLibreMap mapLibreMap;
    private Marker userMarker;
    private Context context;
    
    public SearchMapHelper(Context context) {
        this.context = context;
    }
    
    /**
     * Initialize map with default settings
     */
    public void initializeMap(@NonNull MapLibreMap mapLibreMap) {
        this.mapLibreMap = mapLibreMap;
        setupMapFeatures();
    }
    
    /**
     * Configure map settings and default position
     */
    private void setupMapFeatures() {
        if (mapLibreMap == null) return;

        // Configure UI settings
        mapLibreMap.getUiSettings().setCompassEnabled(true);
        mapLibreMap.getUiSettings().setZoomGesturesEnabled(true);
        mapLibreMap.getUiSettings().setRotateGesturesEnabled(true);
        mapLibreMap.getUiSettings().setTiltGesturesEnabled(true);

        // Set default camera position to Vietnam
        LatLng defaultPosition = new LatLng(21.0285, 105.8542); // Hanoi, Vietnam
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(defaultPosition)
                .zoom(10.0)
                .build();
        mapLibreMap.setCameraPosition(cameraPosition);
    }
    
    /**
     * Switch between standard and satellite map styles
     */
    public void toggleMapStyle(boolean isSatelliteView, Runnable onStyleLoaded) {
        if (mapLibreMap == null) return;
        
        String styleUrl = isSatelliteView 
            ? MapLibreManager.getSatelliteStyleUrl() 
            : MapLibreManager.getDefaultStyleUrl();
            
        mapLibreMap.setStyle(styleUrl, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                if (onStyleLoaded != null) {
                    onStyleLoaded.run();
                }
            }
        });
    }
    
    /**
     * Add store markers from search results
     */
    public void addStoreMarkers(ProductsSearchResultsAdapter adapter) {
        if (mapLibreMap == null || adapter == null) return;

        // Clear existing markers first (except user marker)
        clearStoreMarkers();

        // Add store markers from search results
        ArrayList<ProductModel> products = adapter.getProducts();
        if (products != null) {
            for (ProductModel product : products) {
                if (product.getStoreLatitude() != 0.0 && product.getStoreLongitude() != 0.0) {
                    addStoreMarker(product);
                }
            }
        }
    }
    
    /**
     * Add individual store marker
     */
    private void addStoreMarker(ProductModel product) {
        LatLng storeLocation = new LatLng(
            product.getStoreLatitude(), 
            product.getStoreLongitude()
        );
        
        mapLibreMap.addMarker(new MarkerOptions()
                .position(storeLocation)
                .title(product.getStoreName())
                .snippet(product.getName() + " - $" + product.getCurrentPrice()));
    }
    
    /**
     * Add or update user location marker
     */
    public void addUserLocationMarker(Location location) {
        if (mapLibreMap == null || location == null) return;
        
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        
        // Remove existing user marker
        if (userMarker != null) {
            mapLibreMap.removeMarker(userMarker);
        }
        
        // Get user display name
        UserModel currentUser = UserAccountManager.getUser(context);
        String userTitle = currentUser != null ? currentUser.getFullName() : "Your Location";
        
        // Add new user marker
        userMarker = mapLibreMap.addMarker(new MarkerOptions()
                .position(userLocation)
                .title(userTitle));
    }
    
    /**
     * Center map on user location
     */
    public void centerOnUserLocation(Location location, float zoomLevel) {
        if (mapLibreMap == null || location == null) return;
        
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoomLevel));
    }
    
    /**
     * Clear all store markers (preserve user marker)
     */
    private void clearStoreMarkers() {
        if (mapLibreMap == null) return;
        
        // Store user marker position if exists
        LatLng userPosition = null;
        String userTitle = null;
        if (userMarker != null) {
            userPosition = userMarker.getPosition();
            userTitle = userMarker.getTitle();
        }
        
        // Clear all markers
        mapLibreMap.clear();
        
        // Re-add user marker if it existed
        if (userPosition != null) {
            userMarker = mapLibreMap.addMarker(new MarkerOptions()
                    .position(userPosition)
                    .title(userTitle));
        }
    }
    
    /**
     * Get current map instance
     */
    public MapLibreMap getMapLibreMap() {
        return mapLibreMap;
    }
    
    /**
     * Get user marker
     */
    public Marker getUserMarker() {
        return userMarker;
    }
    
    /**
     * Check if map is ready
     */
    public boolean isMapReady() {
        return mapLibreMap != null;
    }
}
