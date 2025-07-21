package fpt.prm392.fe_salehunter.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PaymentRedirectUtil {
    private static final String TAG = "PaymentRedirectUtil";
    
    // MoMo package name
    private static final String MOMO_PACKAGE = "com.mservice.momotransfer";
    
    // VnPay package name  
    private static final String VNPAY_PACKAGE = "com.vnpay.authentication";
    
    // ZaloPay package name
    private static final String ZALOPAY_PACKAGE = "com.vng.zalopay";
    
    // Bank app package names
    private static final String VIETCOMBANK_PACKAGE = "com.VCB";
    private static final String TECHCOMBANK_PACKAGE = "com.techcombank.bb.app";
    private static final String BIDV_PACKAGE = "com.vnpay.bidv";
    private static final String VIETINBANK_PACKAGE = "com.vietinbank.ipay";
    private static final String AGRIBANK_PACKAGE = "com.agribank.mobile";
    private static final String MBBANK_PACKAGE = "com.mbmobile";
    private static final String TPBANK_PACKAGE = "com.tpb.mb.gprsandroid";
    private static final String SACOMBANK_PACKAGE = "com.sacombank.mbanking";
    private static final String ACBBANK_PACKAGE = "mobile.acb.com.vn";
    private static final String VPBANK_PACKAGE = "com.vpbank.neo";
    
    /**
     * Redirect to payment app or browser
     */
    public static boolean redirectToPayment(Context context, String paymentMethod, 
                                          String paymentUrl, String deepLink) {
        if (context == null || paymentUrl == null) {
            return false;
        }
        
        try {
            // Try app-specific redirection first
            boolean appRedirected = redirectToSpecificApp(context, paymentMethod, deepLink);
            
            if (!appRedirected) {
                // Fallback to browser
                redirectToBrowser(context, paymentUrl);
            }
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error redirecting to payment", e);
            return false;
        }
    }
    
    /**
     * Redirect to specific payment app
     */
    private static boolean redirectToSpecificApp(Context context, String paymentMethod, String deepLink) {
        if (deepLink == null || deepLink.isEmpty()) {
            return false;
        }
        
        try {
            String packageName = getPackageNameForPaymentMethod(paymentMethod);
            
            if (packageName != null && isAppInstalled(context, packageName)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink));
                intent.setPackage(packageName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }
            
            // Try generic intent without specific package
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error redirecting to specific app", e);
            return false;
        }
    }
    
    /**
     * Redirect to browser
     */
    private static void redirectToBrowser(Context context, String paymentUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error redirecting to browser", e);
        }
    }
    
    /**
     * Get package name for payment method
     */
    private static String getPackageNameForPaymentMethod(String paymentMethod) {
        if (paymentMethod == null) return null;
        
        switch (paymentMethod.toUpperCase()) {
            case "MOMO":
                return MOMO_PACKAGE;
            case "VNPAY":
                return VNPAY_PACKAGE;
            case "ZALOPAY":
                return ZALOPAY_PACKAGE;
            default:
                return null;
        }
    }
    
    /**
     * Check if app is installed
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        if (context == null || packageName == null) {
            return false;
        }
        
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Check if payment app is installed
     */
    public static boolean isPaymentAppInstalled(Context context, String paymentMethod) {
        String packageName = getPackageNameForPaymentMethod(paymentMethod);
        return packageName != null && isAppInstalled(context, packageName);
    }
    
    /**
     * Get installed banking apps
     */
    public static java.util.List<String> getInstalledBankingApps(Context context) {
        java.util.List<String> installedApps = new java.util.ArrayList<>();
        
        String[] bankPackages = {
            VIETCOMBANK_PACKAGE, TECHCOMBANK_PACKAGE, BIDV_PACKAGE,
            VIETINBANK_PACKAGE, AGRIBANK_PACKAGE, MBBANK_PACKAGE,
            TPBANK_PACKAGE, SACOMBANK_PACKAGE, ACBBANK_PACKAGE, VPBANK_PACKAGE
        };
        
        for (String packageName : bankPackages) {
            if (isAppInstalled(context, packageName)) {
                installedApps.add(packageName);
            }
        }
        
        return installedApps;
    }
    
    /**
     * Get bank name from package
     */
    public static String getBankNameFromPackage(String packageName) {
        if (packageName == null) return "Unknown";
        
        switch (packageName) {
            case VIETCOMBANK_PACKAGE:
                return "Vietcombank";
            case TECHCOMBANK_PACKAGE:
                return "Techcombank";
            case BIDV_PACKAGE:
                return "BIDV";
            case VIETINBANK_PACKAGE:
                return "VietinBank";
            case AGRIBANK_PACKAGE:
                return "Agribank";
            case MBBANK_PACKAGE:
                return "MB Bank";
            case TPBANK_PACKAGE:
                return "TPBank";
            case SACOMBANK_PACKAGE:
                return "Sacombank";
            case ACBBANK_PACKAGE:
                return "ACB Bank";
            case VPBANK_PACKAGE:
                return "VPBank";
            default:
                return "Unknown";
        }
    }
    
    /**
     * WebView client for handling payment redirects
     */
    public static class PaymentWebViewClient extends WebViewClient {
        private final PaymentRedirectListener listener;
        
        public interface PaymentRedirectListener {
            void onPaymentSuccess(String transactionId);
            void onPaymentFailed(String error);
            void onPaymentCancelled();
        }
        
        public PaymentWebViewClient(PaymentRedirectListener listener) {
            this.listener = listener;
        }
        
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "WebView URL: " + url);
            
            if (url == null) {
                return false;
            }
            
            // Handle return URLs
            if (url.contains("payment/success")) {
                handlePaymentResult(url, true);
                return true;
            } else if (url.contains("payment/failed") || url.contains("payment/error")) {
                handlePaymentResult(url, false);
                return true;
            } else if (url.contains("payment/cancel")) {
                if (listener != null) {
                    listener.onPaymentCancelled();
                }
                return true;
            }
            
            // Handle app redirects
            if (url.startsWith("momo://") || url.startsWith("vnpay://") || 
                url.startsWith("zalopay://") || url.startsWith("intent://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    view.getContext().startActivity(intent);
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "Error handling app redirect", e);
                }
            }
            
            return false;
        }
        
        private void handlePaymentResult(String url, boolean isSuccess) {
            if (listener == null) return;
            
            try {
                Uri uri = Uri.parse(url);
                String transactionId = uri.getQueryParameter("transactionId");
                
                if (isSuccess) {
                    listener.onPaymentSuccess(transactionId != null ? transactionId : "");
                } else {
                    String error = uri.getQueryParameter("error");
                    listener.onPaymentFailed(error != null ? error : "Payment failed");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing payment result", e);
                if (isSuccess) {
                    listener.onPaymentSuccess("");
                } else {
                    listener.onPaymentFailed("Payment failed");
                }
            }
        }
    }
}
