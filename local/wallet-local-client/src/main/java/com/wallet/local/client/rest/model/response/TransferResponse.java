package com.wallet.local.client.rest.model.response;

public class TransferResponse {

    boolean success;
    String message;

    public TransferResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public TransferResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
