package com.example.ewalletexample.model;

public class EmailModel {
    private String userID;
    private String email;
    private String token;

    public EmailModel(){

    }

    public EmailModel(String userID, String email, String token){
        this.userID = userID;
        this.email = email;
        this.token = token;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String phone) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
