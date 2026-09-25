package com.example.attendance.data.model.response;

public class ApiResponse<T> {
    private boolean success;
    private int code;
    private String message;
    private T data;
    private String warning;

    public ApiResponse() {}

    public ApiResponse(boolean success, int code, String message, T data, String warning) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.warning = warning;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getWarning() { return warning; }
    public void setWarning(String warning) { this.warning = warning; }
}
