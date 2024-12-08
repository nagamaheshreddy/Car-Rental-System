package com.Trimble.TrimbleCars.Response;

public class ResponseDTO<T> {

    private int statusCode;           // HTTP status code (e.g., 200, 404)
    private String statusDescription; // Message describing the status (e.g., "Success", "Not Found")
    private T data;    
    

    // Constructors
    public ResponseDTO() {}

    public ResponseDTO(int statusCode, String statusDescription, T data) {
        this.statusCode = statusCode;
        this.statusDescription = statusDescription;
        this.data = data;
    }
    
    

  // Getters and Setters
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
