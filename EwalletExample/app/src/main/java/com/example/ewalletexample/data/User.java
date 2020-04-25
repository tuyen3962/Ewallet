package com.example.ewalletexample.data;

import com.example.ewalletexample.Symbol.Symbol;
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
    private String imgAccountLink;
    private String imgID;
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
        imgAccountLink = "";
        dateOfbirth = "";
        address = "";
        this.fullName = fullName;
    }

    public User(UserModel model){
        setImgAccountLink(model.getImgLink());
        setFullName(model.getFullname());
    }

    public User(){
        userId = "";
        fullName = "";
        password = "";
        phoneNumber = "";
        email = "";
        imgAccountLink = "";
        imgID = "";
        dateOfbirth = "";
        address = "";
        cmnd = "";
        status = -1;
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

    public String getImgAccountLink() {
        return imgAccountLink;
    }

    public void setImgAccountLink(String imgAccountLink) {
        if(imgAccountLink.equalsIgnoreCase("null"))
            this.imgAccountLink = "";
        else
            this.imgAccountLink = imgAccountLink;
    }

    public String getDateOfbirth() {
        return dateOfbirth;
    }

    public void setDateOfbirth(String dateOfbirth) {
        if(dateOfbirth.isEmpty() || dateOfbirth.equalsIgnoreCase("null")){
            dob = null;
            this.dateOfbirth = "";
            return;
        }
        this.dateOfbirth = dateOfbirth;
        String[] splits = dateOfbirth.split("/");
        dob = new Date(splits[2], splits[1], splits[0]);
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

    public String getImgID() {
        return imgID;
    }

    public void setImgID(String imgID) {
        if(imgID.equalsIgnoreCase("null"))
            this.imgID = "";
        else
            this.imgID = imgID;
    }

    public String ExchangeToJson()  {
        try {
            String[] arr = new String[]{"userid:"+userId,"fullname:"+fullName,"password:"+password,"phoneNumber:"+phoneNumber,
                    "email:"+email,"imgAccountLink:"+imgAccountLink,"imgID:"+imgID,"dateOfbirth:"+dateOfbirth,"address:"+address,"cmnd:"+cmnd,"status:"+status};

            String json = HandlerJsonData.ExchangeToJsonString(arr);
            return json;
        } catch (JSONException e){
            return "";
        }
    }

    public void ReadJson(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            setUserId(jsonObject.getString("userid"));
            setFullName(jsonObject.getString("fullname"));
            setPassword(jsonObject.getString("password"));
            setPhoneNumber(jsonObject.getString("phoneNumber"));
            setDateOfbirth(jsonObject.getString("dateOfbirth"));
            setImgID(jsonObject.getString("imgID"));
            setImgAccountLink(jsonObject.getString("imgAccountLink"));
            setEmail(jsonObject.getString("email"));
            setAddress(jsonObject.getString("address"));
            setCmnd(jsonObject.getString("cmnd"));
            setStatus(jsonObject.getInt("status"));
        } catch (JSONException e){
            return;
        }
    }
}