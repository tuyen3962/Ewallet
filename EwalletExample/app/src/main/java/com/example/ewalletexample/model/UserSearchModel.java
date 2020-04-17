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

    public UserSearchModel(String json){
        ReadJson(json);
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

    public String ExchangeToJson(){
        String[] arr = new String[]{"userid:" + userid,"imgLink:" + imgLink, "phone:"+phone,"fullname:" + fullName};

        try {
            return HandlerJsonData.ExchangeToJsonString(arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void ReadJson(String jsonFile){
        try {
            JSONObject jsonObject = new JSONObject(jsonFile);

            setFullName(jsonObject.getString("fullname"));
            setImgLink(jsonObject.getString("imgLink"));
            setPhone(jsonObject.getString("phone"));
            setUserid(jsonObject.getString("userid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
