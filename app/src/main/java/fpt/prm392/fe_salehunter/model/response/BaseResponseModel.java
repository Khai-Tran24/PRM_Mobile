package fpt.prm392.fe_salehunter.model.response;

import com.google.gson.annotations.SerializedName;

public class BaseResponseModel<T> {

    public static final int SUCCESSFUL_OPERATION = 200; //Successful: Data Process, Data Update, Data Retrieval
    public static final int SUCCESSFUL_CREATION = 201; //Successful: Data Creation
    public static final int SUCCESSFUL_DELETED = 204; //Successful: Data Deleted
    public static final int FAILED_BAD_REQUEST = 400; //Failed: Invalid Data, Bad Request
    public static final int FAILED_INVALID_DATA = 400; //Failed: Expired Token, Data Error (alias for BAD_REQUEST)
    public static final int FAILED_UNAUTHORIZED = 401; //Failed: Unauthorized
    public static final int FAILED_AUTH = 401; //Failed: Wrong Password, Not Authorized User (alias for UNAUTHORIZED)
    public static final int FAILED_FORBIDDEN = 403; //Failed: Forbidden access
    public static final int FAILED_NOT_FOUND = 404; //Failed: Token Not Found, User Not Found
    public static final int FAILED_DATA_CONFLICT = 409; //Failed: Data Already Exists
    public static final int FAILED_INTERNAL_SERVER_ERROR = 500; //Failed: Internal Server Error
    public static final int FAILED_SERVER_ERROR = 500; //Failed: Server Error (alias for INTERNAL_SERVER_ERROR)
    public static final int FAILED_SERVER_DOWN = 503; //Failed: Server Down / Service Not Available
    public static final int FAILED_REQUEST_FAILURE = 504; //Failed: Request Error

    @SerializedName("code")
    protected int code;

    @SerializedName("message")
    protected String message;

    @SerializedName("isSuccess")
    protected boolean isSuccess;

    // Legacy field for backward compatibility
    @SerializedName("status")
    protected String status;

    @SerializedName("data")
    protected T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * Safely get data with null check
     * @return data if not null, otherwise null
     */
    public T getDataSafely() {
        return data;
    }

    /**
     * Check if response has data
     * @return true if data is not null
     */
    public boolean hasData() {
        return data != null;
    }

    /**
     * Get message with fallback
     * @param fallback fallback message if message is null or empty
     * @return message or fallback
     */
    public String getMessageOrDefault(String fallback) {
        return (message != null && !message.trim().isEmpty()) ? message : fallback;
    }

    /**
     * Check if this is a successful response
     * @return true if code indicates success
     */
    public boolean isSuccessfulResponse() {
        return code == SUCCESSFUL_OPERATION || 
               code == SUCCESSFUL_CREATION || 
               code == SUCCESSFUL_DELETED;
    }

    /**
     * Check if this is an error response
     * @return true if code indicates error
     */
    public boolean isErrorResponse() {
        return code >= 400;
    }
}
