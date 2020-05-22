package com.example.ewalletexample.Server.api.update;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateUserAPI {
    private UpdateUserResponse response;
    private UpdateUserRequest request;
    Gson gson;

    public UpdateUserAPI(String userid, String pin, UpdateUserResponse response){
        request = new UpdateUserRequest();
        gson = new Gson();
        this.request.setUserid(userid);
        this.request.setPin(pin);
        this.response = response;
    }

    public UpdateUserAPI(String userid, UpdateUserResponse response){
        request = new UpdateUserRequest();
        gson = new Gson();
        this.request.setUserid(userid);
        this.response = response;
    }

    public void setAddress(String address) {
        this.request.setAddress(address);
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.request.setDob(dateOfBirth);
    }

    public void setPin(String pin) {
        this.request.setPin(pin);
    }

    public void setImageProfile(String imageProfile) {
        this.request.setAvatar(imageProfile);
    }

    public void setCmndFrontImage(String cmndFrontImage) {
        this.request.setCmndfontimg(cmndFrontImage);
    }

    public void setCmndBackImage(String cmndBackImage){
        this.request.setCmndbackimg(cmndBackImage);
    }

    public void setCmnd(String cmnd) {
        this.request.setCmnd(cmnd);
    }

    public void setEmail(String email) {
        this.request.setEmail(email);
    }

    public void setKey(String key){
        this.request.setKey(key);
    }

    public void UpdateUser(){
        new UpdateUserProfile().execute(ServerAPI.UPDATE_USER_API.GetUrl(), gson.toJson(request));
    }

    class UpdateUserProfile extends RequestServerAPI implements RequestServerFunction {
        public UpdateUserProfile(){
            SetRequestServerFunction(this);
        }


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
