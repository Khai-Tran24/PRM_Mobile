package fpt.prm392.fe_salehunter.util;

import android.util.Log;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import retrofit2.Response;

/**
 * Utility class for safe handling of API responses with comprehensive null checks
 * and error handling to prevent app crashes from null responses.
 */
public class SafeResponseHandler {
    private static final String TAG = "SafeResponseHandler";

    /**
     * Interface for handling successful responses
     */
    public interface OnSuccessCallback<T> {
        void onSuccess(T data);
    }

    /**
     * Interface for handling error responses
     */
    public interface OnErrorCallback {
        void onError(String errorMessage, int errorCode);
    }

    /**
     * Interface for handling network failures
     */
    public interface OnFailureCallback {
        void onFailure(String failureMessage);
    }

    /**
     * Safely observe a LiveData response with comprehensive null checks
     * @param lifecycleOwner The lifecycle owner
     * @param liveData The LiveData to observe
     * @param onSuccess Callback for successful responses
     * @param onError Callback for error responses
     * @param onFailure Callback for network failures
     */
    public static <T> void observeSafely(LifecycleOwner lifecycleOwner,
                                         LiveData<Response<BaseResponseModel<T>>> liveData,
                                         OnSuccessCallback<T> onSuccess,
                                         OnErrorCallback onError,
                                         OnFailureCallback onFailure) {
        
        if (lifecycleOwner == null) {
            Log.e(TAG, "LifecycleOwner is null, cannot observe LiveData");
            if (onFailure != null) {
                onFailure.onFailure("Internal error: LifecycleOwner is null");
            }
            return;
        }

        if (liveData == null) {
            Log.e(TAG, "LiveData is null, cannot observe");
            if (onFailure != null) {
                onFailure.onFailure("Internal error: LiveData is null");
            }
            return;
        }

        liveData.observe(lifecycleOwner, new Observer<Response<BaseResponseModel<T>>>() {
            @Override
            public void onChanged(Response<BaseResponseModel<T>> response) {
                handleResponse(response, onSuccess, onError, onFailure);
            }
        });
    }

    /**
     * Safely handle a response with comprehensive null checks
     */
    public static <T> void handleResponse(Response<BaseResponseModel<T>> response,
                                          OnSuccessCallback<T> onSuccess,
                                          OnErrorCallback onError,
                                          OnFailureCallback onFailure) {
        
        // Check if response is null
        if (response == null) {
            Log.e(TAG, "Response is null");
            if (onFailure != null) {
                onFailure.onFailure("No response received from server");
            }
            return;
        }

        // Check if response body is null
        if (response.body() == null) {
            Log.e(TAG, "Response body is null, HTTP code: " + response.code());
            if (onError != null) {
                onError.onError("Empty response from server", response.code());
            }
            return;
        }

        BaseResponseModel<T> body = response.body();

        // Handle based on response code
        switch (response.code()) {
            case BaseResponseModel.SUCCESSFUL_OPERATION:
                if (body.getData() != null) {
                    if (onSuccess != null) {
                        onSuccess.onSuccess(body.getData());
                    }
                } else {
                    Log.w(TAG, "Successful response but data is null");
                    if (onError != null) {
                        onError.onError("No data in successful response", response.code());
                    }
                }
                break;

            case BaseResponseModel.FAILED_NOT_FOUND:
                Log.w(TAG, "Resource not found");
                if (onError != null) {
                    onError.onError(body.getMessage() != null ? body.getMessage() : "Resource not found", response.code());
                }
                break;

            case BaseResponseModel.FAILED_UNAUTHORIZED:
                Log.w(TAG, "Unauthorized access");
                if (onError != null) {
                    onError.onError(body.getMessage() != null ? body.getMessage() : "Unauthorized access", response.code());
                }
                break;

            case BaseResponseModel.FAILED_FORBIDDEN:
                Log.w(TAG, "Forbidden access");
                if (onError != null) {
                    onError.onError(body.getMessage() != null ? body.getMessage() : "Access forbidden", response.code());
                }
                break;

            case BaseResponseModel.FAILED_BAD_REQUEST:
                Log.w(TAG, "Bad request");
                if (onError != null) {
                    onError.onError(body.getMessage() != null ? body.getMessage() : "Invalid request", response.code());
                }
                break;

            case BaseResponseModel.FAILED_REQUEST_FAILURE:
                Log.e(TAG, "Request failed");
                if (onFailure != null) {
                    onFailure.onFailure(body.getMessage() != null ? body.getMessage() : "Request failed");
                }
                break;

            case BaseResponseModel.FAILED_INTERNAL_SERVER_ERROR:
                Log.e(TAG, "Internal server error");
                if (onError != null) {
                    onError.onError(body.getMessage() != null ? body.getMessage() : "Internal server error", response.code());
                }
                break;

            default:
                Log.w(TAG, "Unknown response code: " + response.code());
                if (onError != null) {
                    onError.onError(body.getMessage() != null ? body.getMessage() : "Unknown error occurred", response.code());
                }
                break;
        }
    }

    /**
     * Safely extract data from response with null checks
     */
    public static <T> T safeExtractData(Response<BaseResponseModel<T>> response) {
        if (response == null) {
            Log.e(TAG, "Cannot extract data: response is null");
            return null;
        }

        if (response.body() == null) {
            Log.e(TAG, "Cannot extract data: response body is null");
            return null;
        }

        BaseResponseModel<T> body = response.body();
        if (body.getData() == null) {
            Log.w(TAG, "Response data is null");
            return null;
        }

        return body.getData();
    }

    /**
     * Check if response is successful and has data
     */
    public static <T> boolean isSuccessfulWithData(Response<BaseResponseModel<T>> response) {
        return response != null && 
               response.body() != null && 
               response.code() == BaseResponseModel.SUCCESSFUL_OPERATION &&
               response.body().getData() != null;
    }

    /**
     * Get safe error message from response
     */
    public static <T> String getSafeErrorMessage(Response<BaseResponseModel<T>> response) {
        if (response == null) {
            return "No response received";
        }

        if (response.body() == null) {
            return "Empty response from server";
        }

        String message = response.body().getMessage();
        return message != null ? message : "Unknown error occurred";
    }
}
