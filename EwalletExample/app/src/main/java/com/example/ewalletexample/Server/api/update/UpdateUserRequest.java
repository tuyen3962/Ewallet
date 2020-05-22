package com.example.ewalletexample.Server.api.update;

public class UpdateUserRequest {
    private String userid;

    private String pin;
    private String cmnd;
    private String address;
    private String dob;
    private String email;
    private String key;

    private String cmndfontimg;
    private String cmndbackimg;
    private String avatar;

    public UpdateUserRequest(){
        String userid = "";
        String pin = "";
        String cmnd = "";
        String address = "";
        String dob = "";

        String cmndfontimg = "";
        String cmndbackimg = "";
        String avatar = "";
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getCmnd() {
        return cmnd;
    }

    public void setCmnd(String cmnd) {
        this.cmnd = cmnd;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCmndfontimg() {
        return cmndfontimg;
    }

    public void setCmndfontimg(String cmndfontimg) {
        this.cmndfontimg = cmndfontimg;
    }

    public String getCmndbackimg() {
        return cmndbackimg;
    }

    public void setCmndbackimg(String cmndbackimg) {
        this.cmndbackimg = cmndbackimg;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
