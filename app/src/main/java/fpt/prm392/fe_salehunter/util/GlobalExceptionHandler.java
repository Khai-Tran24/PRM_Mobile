package fpt.prm392.fe_salehunter.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

/**
 * Global exception handler to prevent app crashes from unhandled exceptions
 * and provide graceful error handling across the entire application.
 */
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "GlobalExceptionHandler";
    
    private final Context context;
    private final Thread.UncaughtExceptionHandler defaultHandler;

    public GlobalExceptionHandler(Context context) {
        this.context = context;
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    /**
     * Install the global exception handler
     * Call this in your Application class onCreate method
     */
    public static void install(Application application) {
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler(application));
        Log.i(TAG, "Global exception handler installed");
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            Log.e(TAG, "Uncaught exception occurred", throwable);
            
            // Log the error details
            logError(throwable);
            
            // Show user-friendly message
            showErrorToUser(throwable);
            
            // Optional: Send crash report to analytics service
            // Analytics.logCrash(throwable);
            
        } catch (Exception e) {
            Log.e(TAG, "Error in exception handler", e);
        } finally {
            // Clean up and restart or close app
            handleAppRestart();
        }
    }

    private void logError(Throwable throwable) {
        try {
            Log.e(TAG, "=== CRASH REPORT ===");
            Log.e(TAG, "Thread: " + Thread.currentThread().getName());
            Log.e(TAG, "Exception: " + throwable.getClass().getSimpleName());
            Log.e(TAG, "Message: " + throwable.getMessage());
            Log.e(TAG, "Stack trace:", throwable);
            Log.e(TAG, "===================");
        } catch (Exception e) {
            Log.e(TAG, "Failed to log error details", e);
        }
    }

    private void showErrorToUser(Throwable throwable) {
        try {
            // Determine user-friendly message based on exception type
            String userMessage = getUserFriendlyMessage(throwable);
            
            // Show toast message (this works even if UI is in bad state)
            if (context != null) {
                // Use handler to show toast on main thread
                android.os.Handler mainHandler = new android.os.Handler(context.getMainLooper());
                mainHandler.post(() -> {
                    try {
                        Toast.makeText(context, userMessage, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to show error toast", e);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to show error to user", e);
        }
    }

    private String getUserFriendlyMessage(Throwable throwable) {
        if (throwable == null) {
            return "An unexpected error occurred. Please restart the app.";
        }

        String className = throwable.getClass().getSimpleName();
        String message = throwable.getMessage();

        // Map technical exceptions to user-friendly messages
        switch (className) {
            case "NullPointerException":
                return "A data error occurred. Please try again.";
            
            case "NetworkOnMainThreadException":
            case "ConnectException":
            case "SocketTimeoutException":
                return "Network connection error. Please check your internet connection.";
            
            case "OutOfMemoryError":
                return "The app is using too much memory. Please restart the app.";
            
            case "SecurityException":
                return "Permission error. Please check app permissions.";
            
            case "IllegalStateException":
                return "The app is in an invalid state. Please restart the app.";
            
            case "IllegalArgumentException":
                return "Invalid data detected. Please try again.";
            
            case "ClassCastException":
                return "Data format error. Please update the app.";
            
            case "RuntimeException":
                if (message != null && message.contains("Unable to start activity")) {
                    return "Failed to open screen. Please try again.";
                }
                break;
        }

        // For unknown exceptions, provide generic message
        return "An unexpected error occurred. Please restart the app.";
    }

    private void handleAppRestart() {
        try {
            // Give some time for the toast to show
            Thread.sleep(2000);
            
            // Option 1: Restart the app
            // Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            // if (intent != null) {
            //     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            //     context.startActivity(intent);
            // }
            
            // Option 2: Close the app (safer approach)
            Process.killProcess(Process.myPid());
            System.exit(1);
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to handle app restart", e);
            
            // Last resort: call default handler
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(Thread.currentThread(), e);
            } else {
                Process.killProcess(Process.myPid());
                System.exit(1);
            }
        }
    }
}
