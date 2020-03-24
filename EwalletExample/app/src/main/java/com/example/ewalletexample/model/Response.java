package com.example.ewalletexample.model;

public class Response {
    private String message;
    private boolean status;

    public Response(String _message, boolean _status){
        message = _message;
        status = _status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
