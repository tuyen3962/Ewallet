package com.example.ewalletexample.Server.api.update;

import android.os.Build;

import com.example.ewalletexample.R;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.SecretKey;

import androidx.annotation.RequiresApi;

public class UpdateUserAPI {
    private UpdateUserResponse response;
    private UpdateUserRequest request;
    private String publicKey;
    Gson gson;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public UpdateUserAPI(String userid, String publicKey, UpdateUserResponse response){
        request = new UpdateUserRequest();
        gson = new Gson();
        this.request.setUserid(userid);
        this.response = response;
        this.publicKey = publicKey;
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

    public void setSecondKey(String key){
        this.request.setSecondKey(key);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void UpdateUser(SecretKey secretKey1, SecretKey secretKey2){
        String secretKey1String = SecurityUtils.EncodeStringBase64(secretKey1.getEncoded());
        String encryptSecretKey1ByAES = SecurityUtils.encryptAES(secretKey2, secretKey1String);
        String encryptSecretKey1ByRSA = SecurityUtils.EncryptDataByPublicKey(publicKey, encryptSecretKey1ByAES);

        String secretKey2String = SecurityUtils.EncodeStringBase64(secretKey2.getEncoded());
        String encodeSecretKeyByPublicKey = SecurityUtils.EncryptDataByPublicKey(publicKey, secretKey2String);

        String rawHeader = request.getString() + secretKey1String + secretKey2String;
        String hashHeader = SecurityUtils.encryptHmacSha256(secretKey1, rawHeader);
        String encryptHeader = SecurityUtils.encryptAES(secretKey2, hashHeader);
        String encryptHeaderByRSA = SecurityUtils.EncryptDataByPublicKey(publicKey, encryptHeader);
        request.setKey(encryptSecretKey1ByRSA);
        request.setSecondKey(encodeSecretKeyByPublicKey);
        new UpdateUserProfile().execute(ServerAPI.UPDATE_USER_API.GetUrl(), gson.toJson(request), encryptHeaderByRSA);
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
