package fpt.prm392.fe_salehunter.helper;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import fpt.prm392.fe_salehunter.data.local.repository.LocalBarcodeRepository;
import fpt.prm392.fe_salehunter.model.barcode.BarcodeMonsterResponseModel;
import fpt.prm392.fe_salehunter.model.barcode.OpenFoodFactsResponseModel;
import fpt.prm392.fe_salehunter.model.barcode.UpcItemDbResponseModel;
import fpt.prm392.fe_salehunter.viewmodel.activity.ScannerViewModel;
import retrofit2.Response;

/**
 * Helper class for performing barcode lookups using multiple data sources
 * Fallback order: UpcItemDb → OpenFoodFacts → BarcodeMonster
 */
public class BarcodeLookupHelper {
    
    public interface BarcodeCallback {
        void onSuccess(String productName);
        void onFailure(String message);
    }
    
    /**
     * Performs barcode lookup using available data sources
     * @param viewModel ScannerViewModel for API calls
     * @param localRepository Local barcode repository for caching
     * @param barcode The barcode to lookup
     * @param callback Callback for results
     */
    public static void performBarcodeLookup(ScannerViewModel viewModel, 
                                          LocalBarcodeRepository localRepository,
                                          String barcode, 
                                          BarcodeCallback callback) {
        
        // First try local cache
        try {
            String cachedResult = getCachedResult(localRepository, barcode);
            if (cachedResult != null && !cachedResult.isEmpty()) {
                callback.onSuccess(cachedResult);
                return;
            }
        } catch (Exception e) {
            // Continue to API lookup
        }
        
        // Try UPC Item DB first
        lookupFromUpcItemDb(viewModel, localRepository, barcode, callback);
    }
    
    private static void lookupFromUpcItemDb(ScannerViewModel viewModel,
                                          LocalBarcodeRepository localRepository,
                                          String barcode,
                                          BarcodeCallback callback) {
        
        viewModel.barcodeLookupUpcItemDb(barcode).observeForever(response -> {
            if (response != null && response.body() != null && response.body().getItems() != null && !response.body().getItems().isEmpty()) {
                String productName = response.body().getItems().get(0).getProductName();
                if (productName != null && !productName.trim().isEmpty()) {
                    cacheResult(localRepository, barcode, productName);
                    callback.onSuccess(productName);
                    return;
                }
            }
            lookupFromOpenFoodFacts(viewModel, localRepository, barcode, callback);
        });
    }

    private static void lookupFromOpenFoodFacts(ScannerViewModel viewModel,
                                              LocalBarcodeRepository localRepository,
                                              String barcode,
                                              BarcodeCallback callback) {
        
        viewModel.barcodeLookupOpenFoodFacts(barcode).observeForever(response -> {
            if (response != null && response.body() != null && response.body().isSuccess()) {
                String productName = response.body().getProductName();
                if (productName != null && !productName.trim().isEmpty()) {
                    cacheResult(localRepository, barcode, productName);
                    callback.onSuccess(productName);
                    return;
                }
            }
            // All sources failed
            callback.onFailure("Product not found in any database");
        });
    }

    private static String getCachedResult(LocalBarcodeRepository localRepository, String barcode) {
        try {
            // Implement cache lookup logic here
            // This is a placeholder - implement according to your local repository structure
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private static void cacheResult(LocalBarcodeRepository localRepository, String barcode, String productName) {
        try {
            // Implement cache saving logic here
            // This is a placeholder - implement according to your local repository structure
        } catch (Exception e) {
            // Ignore cache errors
        }
    }
}
