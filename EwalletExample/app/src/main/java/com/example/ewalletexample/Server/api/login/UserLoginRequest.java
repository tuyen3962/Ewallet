package com.example.ewalletexample.Server.api.login;

public class UserLoginRequest {
    private String phone;
    private String pin;
    private String key;
    private String secondKey;

    public UserLoginRequest(String phone, String pin, String key, String secondKey) {
        this.phone = phone;
        this.pin = pin;
        this.key = key;
        this.secondKey = secondKey;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecondKey() {
        return secondKey;
    }

    public void setSecondKey(String secondKey) {
        this.secondKey = secondKey;
    }

    public String getString(){
        return phone + pin + key + secondKey;
    }
}
