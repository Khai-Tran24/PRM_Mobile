package fpt.prm392.fe_salehunter.model;

import com.google.gson.annotations.SerializedName;

public class BaseResponseModel {

    public static final int SUCCESSFUL_OPERATION = 200; //Successful: Data Process, Data Update, Data Retrieval
    public static final int SUCCESSFUL_CREATION = 201; //Successful: Data Creation
    public static final int SUCCESSFUL_DELETED = 204; //Successful: Data Deleted
    public static final int FAILED_INVALID_DATA = 400; //Failed: Expired Token, Data Error
    public static final int FAILED_AUTH = 401; //Failed: Wrong Password, Not Authorized User
    public static final int FAILED_NOT_FOUND = 404; //Failed: Token Not Found, User Not Found
    public static final int FAILED_DATA_CONFLICT = 409; //Failed: Data Already Exists
    public static final int FAILED_SERVER_ERROR = 500; //Failed: Server Error
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

}
