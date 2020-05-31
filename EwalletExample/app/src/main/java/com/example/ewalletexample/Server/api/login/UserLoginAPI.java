package com.example.ewalletexample.Server.api.login;

import android.os.Build;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.SecretKey;

import androidx.annotation.RequiresApi;

public class UserLoginAPI {
    private UserLoginRequest request;
    private UserLoginResponse response;
    private String shareKeyText, publicKey;
    Gson gson;

    public UserLoginAPI(String shareKeyText, String publicKey, UserLoginRequest request, UserLoginResponse response){
        this.request = request;
        this.response = response;
        this.shareKeyText = shareKeyText;
        this.publicKey = publicKey;
        gson = new Gson();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void StartLoginAPI(){
        SecretKey key = SecurityUtils.generateAESKeyFromText(request.getKey());
        SecretKey secondKey = SecurityUtils.generateAESKeyFromText(request.getSecondKey());

        // Encrypt first key
        String encodeSecretKeyByPublicKey = SecurityUtils.EncryptDataByPublicKey(publicKey, request.getKey());

        //Encrypt second Key
        String encryptSecretkeyByFirstKey = SecurityUtils.encryptAES(key, request.getSecondKey());
        String encryptSecretKeyByPublicKey = SecurityUtils.EncryptDataByPublicKey(publicKey, encryptSecretkeyByFirstKey);

        //hash pass
        String hashPasswordByShareKey = SecurityUtils.EncryptStringByShareKey(shareKeyText, request.getPin());
        request.setPin(hashPasswordByShareKey);
        //encrypt pass
        String encryptPasswordByAES = SecurityUtils.encryptAES(key, hashPasswordByShareKey);
        String encryptPasswordBySecondKeyAES = SecurityUtils.encryptAES(secondKey, encryptPasswordByAES);
        String encryptPasswordByPublicKey = SecurityUtils.EncryptDataByPublicKey(publicKey, encryptPasswordBySecondKeyAES);

        // Encrypt header
        String headerStringEncrypt = request.getString();
        String hashAuthorizationTextAPI = SecurityUtils.EncryptStringBySecretKey(key, shareKeyText, headerStringEncrypt);
        String encryptHeaderBySecondSecretKey = SecurityUtils.encryptAES(secondKey, hashAuthorizationTextAPI);
        String encryptPublicKeyAuthorization = SecurityUtils.EncryptDataByPublicKey(publicKey, encryptHeaderBySecondSecretKey);

        request.setKey(encodeSecretKeyByPublicKey);
        request.setPin(encryptPasswordByPublicKey);
        request.setSecondKey(encryptSecretKeyByPublicKey);
        new LoginThread().execute(ServerAPI.LOGIN_API.GetUrl(), gson.toJson(request), encryptPublicKeyAuthorization);
    }

    private class LoginThread extends RequestServerAPI implements RequestServerFunction {
        public LoginThread() {
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if (code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }
            else {
                ShowError(code, "Fail");
                return false;
            }
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            User user = new User();
            user.setUserId(jsonData.getString("userid"));
            user.setFullName(jsonData.getString("fullname"));
            user.setAddress(jsonData.getString("address"));
            user.setPhoneNumber(jsonData.getString("phone"));
            user.setDateOfbirth(jsonData.getString("dob"));
            user.setCmnd(jsonData.getString("cmnd"));
            user.setEmail(jsonData.getString("email"));
            user.setCmndFrontImage(jsonData.getString("cmndFontImg"));
            user.setAvatar(jsonData.getString("avatar"));
            user.setCmndBackImage(jsonData.getString("cmndBackImg"));
            user.setStatus(jsonData.getInt("verify"));
            String customToken = jsonData.getString("customToken");
            String secretKey1 = jsonData.getString("secretKey1");
            String secretKey2 = jsonData.getString("secretKey2");
            response.LoginSucess(user, customToken, secretKey1, secretKey2);
        }

        @Override
        public void ShowError(int errorCode, String message) {
            response.LoginFail(errorCode);
        }
    }
}
