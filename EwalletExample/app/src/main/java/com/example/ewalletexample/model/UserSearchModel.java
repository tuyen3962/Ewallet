package com.example.ewalletexample.model;

import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

public class UserSearchModel {
    private String userid;
    private String imgLink;
    private String fullName;
    private String phone;

    public UserSearchModel(String userid, String imgLink, String fullName, String phone) {
        this.userid = userid;
        this.imgLink = imgLink;
        this.fullName = fullName;
        this.phone = phone;
    }

    public UserSearchModel(){

    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
