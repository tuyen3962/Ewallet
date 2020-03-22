package com.example.ewalletexample.model;

import android.text.TextUtils;

public class UserModel {
    private int userId;
    public String fullName;
    private String username;
    private String password;
    private String confirmPassword;
    private String phoneNumber;
    private String email;

    public UserModel(int userId, String username, String password, String confirmPassword, String phoneNumber, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public UserModel(String fullName, String username, String password, String confirmPassword, String phoneNumber, String email) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public UserModel(String username, String password, String confirmPassword, String phoneNumber, String email) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean CheckPassword(){
        if(password.equalsIgnoreCase(confirmPassword)){
            return true;
        }else{
            return false;
        }
    }

    public boolean IsEmpty(){
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(phoneNumber) ||
                TextUtils.isEmpty(email)){
            return true;
        }
        else{
            return false;
        }
    }
}
