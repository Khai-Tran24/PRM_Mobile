package fpt.prm392.fe_salehunter.util.search;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

/**
 * Helper class for managing location services in search functionality
 */
public class SearchLocationHelper {
    
    private Context context;
    private Fragment fragment;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LocationCallback locationCallback;
    
    public interface LocationCallback {
        void onLocationReceived(Location location);
        void onLocationError(String error);
        void onPermissionRequired(String[] permissions);
    }
    
    public SearchLocationHelper(Fragment fragment, LocationCallback callback) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.locationCallback = callback;
        
        if (context != null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
    }
    
    /**
     * Request current location with permission checks
     */
    public void requestCurrentLocation() {
        if (context == null || locationManager == null) {
            if (locationCallback != null) {
                locationCallback.onLocationError("Location services not available");
            }
            return;
        }
        
        // Check permissions
        if (!hasLocationPermissions()) {
            requestLocationPermissions();
            return;
        }
        
        try {
            // Try to get last known location first
            Location lastKnownLocation = getLastKnownLocation();
            
            if (lastKnownLocation != null) {
                if (locationCallback != null) {
                    locationCallback.onLocationReceived(lastKnownLocation);
                }
            } else {
                // Request fresh location
                requestFreshLocation();
            }
        } catch (SecurityException e) {
            if (locationCallback != null) {
                locationCallback.onLocationError("Location permission denied");
            }
        }
    }
    
    /**
     * Get last known location from available providers
     */
    private Location getLastKnownLocation() throws SecurityException {
        Location lastKnown = null;
        
        // Try GPS first
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        
        // Fallback to Network provider if GPS unavailable
        if (lastKnown == null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lastKnown = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        
        return lastKnown;
    }
    
    /**
     * Request fresh location update
     */
    private void requestFreshLocation() throws SecurityException {
        if (locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
        
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Remove listener after first update
                locationManager.removeUpdates(this);
                locationListener = null;
                
                if (locationCallback != null) {
                    locationCallback.onLocationReceived(location);
                }
            }
        };
        
        // Choose best available provider
        String provider = LocationManager.GPS_PROVIDER;
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        }
        
        locationManager.requestSingleUpdate(provider, locationListener, null);
    }
    
    /**
     * Start continuous location updates
     */
    public void startLocationUpdates(long minTimeMs, float minDistanceMeters) {
        if (!hasLocationPermissions()) {
            requestLocationPermissions();
            return;
        }
        
        try {
            if (locationListener != null) {
                locationManager.removeUpdates(locationListener);
            }
            
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    if (locationCallback != null) {
                        locationCallback.onLocationReceived(location);
                    }
                }
            };
            
            // Use GPS provider if available, otherwise Network
            String provider = LocationManager.GPS_PROVIDER;
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER;
            }
            
            locationManager.requestLocationUpdates(provider, minTimeMs, minDistanceMeters, locationListener);
            
        } catch (SecurityException e) {
            if (locationCallback != null) {
                locationCallback.onLocationError("Location permission denied");
            }
        }
    }
    
    /**
     * Stop location updates
     */
    public void stopLocationUpdates() {
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
            locationListener = null;
        }
    }
    
    /**
     * Check if location permissions are granted
     */
    private boolean hasLocationPermissions() {
        if (context == null) return false;
        
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED &&
               ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * Request location permissions
     */
    private void requestLocationPermissions() {
        if (locationCallback != null) {
            locationCallback.onPermissionRequired(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }
    
    /**
     * Check if any location provider is enabled
     */
    public boolean isLocationServiceEnabled() {
        if (locationManager == null) return false;
        
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
               locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    
    /**
     * Cleanup resources
     */
    public void cleanup() {
        stopLocationUpdates();
        locationCallback = null;
        context = null;
        fragment = null;
    }
}
