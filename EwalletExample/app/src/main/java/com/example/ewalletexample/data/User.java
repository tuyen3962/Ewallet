package com.example.ewalletexample.data;

import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.utilies.Date;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String userId;
    private String fullName;
    private String password;
    private String phoneNumber;
    private String email;
    private String avatar;
    private String cmndFrontImage;
    private String cmndBackImage;
    private String dateOfbirth;
    private Date dob;
    private String address;
    private String cmnd;
    private int status;

    public User(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public User(String fullName, String phoneNumber, String pin){
        this.phoneNumber = phoneNumber;
        this.password = pin;
        email = "";
        avatar = "";
        dateOfbirth = "";
        address = "";
        this.fullName = fullName;
    }

    public User(UserModel model){
        setAvatar(model.getImgLink());
        setFullName(model.getFullname());
    }

    public User(){
        userId = "";
        fullName = "";
        password = "";
        phoneNumber = "";
        email = "";
        avatar = "";
        cmndFrontImage = "";
        dateOfbirth = "";
        address = "";
        cmnd = "";
        status = -1;
        cmndBackImage = "";
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if(password.equalsIgnoreCase("null"))
            this.password = "";
        else
            this.password = password;;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if(phoneNumber.equalsIgnoreCase("null"))
            this.phoneNumber = "";
        else
            this.phoneNumber = phoneNumber;;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if(email.equalsIgnoreCase("null"))
            this.email = "";
        else
            this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        if(avatar.equalsIgnoreCase("null"))
            this.avatar = "";
        else
            this.avatar = avatar;
    }

    public String getDateOfbirth() {
        return dateOfbirth;
    }

    public void setDateOfbirth(String dateOfbirth) {
        this.dateOfbirth = dateOfbirth;
    }

    public String getAddress() {
        if(address.equalsIgnoreCase("null"))
            return "";
        return address;
    }

    public void setAddress(String address) {
        if(address.equalsIgnoreCase("null"))
            this.address = "";
        else
            this.address = address;
    }

    public String getCmnd() {
        if(cmnd.equalsIgnoreCase("null"))
            return "";
        return cmnd;
    }

    public void setCmnd(String cmnd) {
        if(cmnd.equalsIgnoreCase("null"))
            this.cmnd = "";
        else
            this.cmnd = cmnd;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDayOfBirth(){
        if(dob == null) return "";
        return String.valueOf(dob.getDay());
    }

    public String getMonthOfBirth(){
        if(dob == null) return "";
        return String.valueOf(dob.getMonth());
    }

    public String getYearOfBirth(){
        if(dob == null) return "";
        return String.valueOf(dob.getYear());
    }

    public String getCmndFrontImage() {
        return cmndFrontImage;
    }

    public void setCmndFrontImage(String cmndFrontImage) {
        if(cmndFrontImage.equalsIgnoreCase("null"))
            this.cmndFrontImage = "";
        else
            this.cmndFrontImage = cmndFrontImage;
    }

    public String getCmndBackImage() {
        return cmndBackImage;
    }

    public void setCmndBackImage(String cmndBackImage) {
        this.cmndBackImage = cmndBackImage;
    }

    public void setDate(){
        if(this.dateOfbirth.isEmpty() || this.dateOfbirth.equalsIgnoreCase("null")){
            dob = null;
            return;
        }
        String[] splits = dateOfbirth.split("/");
        dob = new Date(splits[2], splits[1], splits[0]);
    }
}