package com.example.ewalletexample.Server.user.update;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateUserAPI {
    private UpdateUserResponse response;
    private String userid;
    private String cmnd;
    private String address;
    private String dateOfBirth;
    private String pin;
    private String imageProfile;
    private String imageID;

    public UpdateUserAPI(User user, UpdateUserResponse response){
        userid = user.getUserId();
        cmnd = user.getCmnd();
        address = user.getAddress();
        dateOfBirth = user.getDateOfbirth();
        this.response = response;
    }

    public UpdateUserAPI(String userid, String pin, UpdateUserResponse response){
        this.userid = userid;
        this.pin = pin;
        this.response = response;
    }

    public UpdateUserAPI(String userid, UpdateUserResponse response){
        this.userid = userid;
        this.address = "";
        this.dateOfBirth = "";
        this.cmnd = "";
        this.imageID = "";
        this.imageProfile = "";
        this.response = response;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public void UpdateUser(){
        try {
            String[] arrStr = new String[]{"userid:"+ userid,"pin:"+pin,"dob:"+dateOfBirth,
                    "cmnd:"+ cmnd,"address:"+ address, "image_profile:"+imageProfile,"image_id:"+imageID};
            String json = HandlerJsonData.ExchangeToJsonString(arrStr);
            new UpdateUserProfile().execute(ServerAPI.UPDATE_USER_API.GetUrl(), json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class UpdateUserProfile extends RequestServerAPI implements RequestServerFunction {

        @Override
        public boolean CheckReturnCode(int code) {
            if(code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }

            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            response.UpdateSuccess();
        }

        @Override
        public void ShowError(int errorCode, String message) {
            response.UpdateFail();
        }
    }
}
