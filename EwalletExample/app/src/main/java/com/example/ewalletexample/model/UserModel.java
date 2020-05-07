package com.example.ewalletexample.model;

import com.example.ewalletexample.Symbol.Symbol;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserModel implements Serializable {
    private String phone;
    private String phoneToken;
    private String email;
    private String emailToken;
    private String fullname;
    private String imgLink;
    private List<String> friends;

    public UserModel(String phone, String phoneToken, String email, String emailToken, String fullname, String imgLink) {
        this.phone = phone;
        this.phoneToken = phoneToken;
        this.email = email;
        this.emailToken = emailToken;
        this.fullname = fullname;
        this.imgLink = imgLink;
        friends = new ArrayList<>();
    }

    public UserModel(String fullname, String phone, String phoneToken, String email){
        this.phone = phone;
        this.phoneToken = phoneToken;
        this.email = email;
        this.emailToken = "";
        this.imgLink = "";
        this.fullname = fullname;
        friends = new ArrayList<>();
    }

    public UserModel(){
        this.phone = "";
        this.phoneToken = "";
        this.email = "";
        this.emailToken = "";
        this.imgLink = "";
        this.fullname = "";
        friends = new ArrayList<>();
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
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
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailToken() {
        return emailToken;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                ", phone='" + phone + '\'' +
                ", phoneToken='" + phoneToken + '\'' +
                ", email='" + email + '\'' +
                ", emailToken='" + emailToken + '\'' +
                ", fullname='" + fullname + '\'' +
                ", imgLink='" + imgLink + '\'' +
                '}';
    }

    public void setEmailToken(String emailToken) {
        this.emailToken = emailToken;
    }

    public void setFriends(List<String> friend){
        this.friends = friend;
    }

    public List<String> getFriends(){
        if (this.friends == null){
            this.friends = new ArrayList<>();
        }
        return this.friends;
    }

    public void addNewFriend(String friendId){
        friends.add(friendId);
    }
}
