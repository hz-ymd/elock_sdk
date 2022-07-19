package com.national.btlock.sdk.model;

public class LoginResult {
    private String status;
    private String message;

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

    Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        public String getAccountVerified() {
            return accountVerified;
        }

        public void setAccountVerified(String accountVerified) {
            this.accountVerified = accountVerified;
        }

        String accountVerified;


    }
}
