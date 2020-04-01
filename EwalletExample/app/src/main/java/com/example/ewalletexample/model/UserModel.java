package com.example.ewalletexample.model;

public class UserModel {
    private String userID;
    private String phone;
    private String phoneToken;
    private String email;
    private String emailToken;
    private String fullname;
    private String dob;
    private String cmnd;
    private String addres;

    public UserModel(String userID, String phone, String phoneToken, String email, String emailToken, String fullname, String dob, String cmnd, String addres) {
        this.userID = userID;
        this.phone = phone;
        this.phoneToken = phoneToken;
        this.email = email;
        this.emailToken = emailToken;
        this.fullname = fullname;
        this.dob = dob;
        this.cmnd = cmnd;
        this.addres = addres;
    }

    public UserModel(String fullname, String userID, String phone, String phoneToken, String email){
        this.userID = userID;
        this.phone = phone;
        this.phoneToken = phoneToken;
        this.email = email;
        this.emailToken = "";
        this.fullname = fullname;
        this.dob = "";
        this.cmnd = "";
        this.addres = "";
    }

    public UserModel(String userID, String phone, String phoneToken, String email){
        this.userID = userID;
        this.phone = phone;
        this.phoneToken = phoneToken;
        this.email = email;
        this.emailToken = "";
        this.fullname = "";
        this.dob = "";
        this.cmnd = "";
        this.addres = "";
    }

    public UserModel(){}

    public String getUserID() {
        return userID;
    }

    public void setUserID(String phone) {
        this.userID = userID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneToken() {
        return phoneToken;
    }

    public void setPhoneToken(String phoneToken) {
        this.phoneToken = phoneToken;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCmnd() {
        return cmnd;
    }

    public void setCmnd(String cmnd) {
        this.cmnd = cmnd;
    }

    public String getAddres() {
        return addres;
    }

    public void setAddres(String addres) {
        this.addres = addres;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailToken() {
        return emailToken;
    }

    public void setEmailToken(String emailToken) {
        this.emailToken = emailToken;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userID='" + userID + '\'' +
                ", phone='" + phone + '\'' +
                ", phoneToken='" + phoneToken + '\'' +
                ", email='" + email + '\'' +
                ", emailToken='" + emailToken + '\'' +
                ", fullname='" + fullname + '\'' +
                ", dob='" + dob + '\'' +
                ", cmnd='" + cmnd + '\'' +
                ", addres='" + addres + '\'' +
                '}';
    }
}
