package fpt.prm392.fe_salehunter.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;

import fpt.prm392.fe_salehunter.R;

/**
 * Helper class for handling deep link navigation
 */
public class DeepLinkHelper {
    
    /**
     * Handle deep link navigation from intent
     */
    public static void handleDeepLink(Context context, Intent intent, NavController navController) {
        if (intent == null || navController == null) return;
        
        Uri appLinkData = intent.getData();
        if (appLinkData == null) return;
        
        String path = appLinkData.getPath();
        if (path == null) return;
        
        // Clean up path
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        
        // Handle different deep link types
        if (path.contains("pid=")) {
            handleProductDeepLink(path, navController);
        } else if (path.contains("store-profile=")) {
            handleStoreDeepLink(path, navController);
        } else if (path.equals("/profile")) {
            // Handle profile navigation
            // This would need to be implemented based on your menu structure
        } else if (path.equals("/about-us")) {
            // Handle about us navigation
            // This would need to be implemented based on your menu structure
        }
    }
    
    /**
     * Handle product deep link navigation
     */
    private static void handleProductDeepLink(String path, NavController navController) {
        try {
            String productId = path.substring(path.indexOf("=") + 1);
            Bundle bundle = new Bundle();
            bundle.putLong("productId", Long.parseLong(productId));
            navController.navigate(R.id.productPageFragment, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handle store deep link navigation
     */
    private static void handleStoreDeepLink(String path, NavController navController) {
        try {
            String storeId = path.substring(path.indexOf("=") + 1);
            Bundle bundle = new Bundle();
            bundle.putLong("storeId", Long.parseLong(storeId));
            navController.navigate(R.id.storePageFragment, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Navigate to fragment with animation
     */
    public static void navigateToFragment(NavController navController, int fragmentId, long animationDuration) {
        new Handler().postDelayed(() -> {
            navController.popBackStack(fragmentId, true);
            navController.navigate(fragmentId, null, 
                new NavOptions.Builder()
                    .setEnterAnim(R.anim.fragment_in)
                    .setExitAnim(R.anim.fragment_out)
                    .build());
        }, animationDuration);
    }
}
