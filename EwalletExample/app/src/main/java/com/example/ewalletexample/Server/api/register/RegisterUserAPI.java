package com.example.ewalletexample.Server.api.register;

import android.os.Build;
import android.util.Log;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.GsonUtils;
import com.example.ewalletexample.utilies.SecurityUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;

import javax.crypto.SecretKey;

import androidx.annotation.RequiresApi;

public class RegisterUserAPI {
    private RegisterCallback callback;
    private RegisterRequest request;

    public RegisterUserAPI(RegisterRequest request, RegisterCallback callback) {
        this.request = request;
        this.callback = callback;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void StartRegister(String publicKeyString, String shareKey){
        PublicKey publicKey = SecurityUtils.generatePublicKey(publicKeyString);

        SecretKey secretKey1 = SecurityUtils.generateAESKey();
        SecretKey secretKey2 = SecurityUtils.generateAESKey();

        String hashPassword = SecurityUtils.EncryptStringByShareKey(shareKey, request.pin);
        String encryptPasswordByFirstKeyAES = SecurityUtils.encryptAES(secretKey1, hashPassword);
        String encryptPasswordBySecondKeyAES = SecurityUtils.encryptAES(secretKey2, encryptPasswordByFirstKeyAES);
        String encryptPinByPK = SecurityUtils.encryptRSA(publicKey, encryptPasswordBySecondKeyAES);

        String encodeSecretKey1 = SecurityUtils.EncodeStringBase64(secretKey1.getEncoded());
        String encryptSecretKey1ByPK = SecurityUtils.encryptRSA(publicKey, encodeSecretKey1);

        String encodeSecretKey2 = SecurityUtils.EncodeStringBase64(secretKey2.getEncoded());
        String encryptSecretKey2ByPK = SecurityUtils.encryptRSA(publicKey, SecurityUtils.encryptAES(secretKey1, encodeSecretKey2));

        String headerStringEncrypt = request.fullname + request.phone + hashPassword + encodeSecretKey1 + encodeSecretKey2;
        String hashAuthorizationTextAPI = SecurityUtils.EncryptStringBySecretKey(secretKey1, shareKey, headerStringEncrypt);
        String encryptPublicKeyAuthorization = SecurityUtils.encryptRSA(publicKey, SecurityUtils.encryptAES(secretKey2, hashAuthorizationTextAPI));

        request.pin = encryptPinByPK;
        request.key = encryptSecretKey1ByPK;
        request.secondKey = encryptSecretKey2ByPK;

        new Register(secretKey1, secretKey2).execute(ServerAPI.REGISTER_API.GetUrl(), GsonUtils.toJsonString(request),encryptPublicKeyAuthorization );
    }

    private class Register extends RequestServerAPI implements RequestServerFunction {
        SecretKey secretKey1, secretKey2;

        public Register(SecretKey secretKey1, SecretKey secretKey2){
            this.secretKey1 = secretKey1;
            this.secretKey2 = secretKey2;
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if (code == ErrorCode.SUCCESS.GetValue()){
                return  true;
            }

            ShowError(code, "");
            return false;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            String userid = jsonData.getString("userid");
            String customToken = jsonData.getString("customToken");
            String secretKey1String = SecurityUtils.DecryptAESbyTwoSecretKey(this.secretKey1, this.secretKey2, jsonData.getString("secretKey1"));
            String secretKey2String = SecurityUtils.DecryptAESbyTwoSecretKey(this.secretKey1, this.secretKey2,jsonData.getString("secretKey2"));
            callback.RegisterSuccessful(userid, customToken, secretKey1String, secretKey2String);
            callback.RegisterTemp(userid, request.fullname, request.phone);
        }

        @Override
        public void ShowError(int errorCode, String message) {
            Log.d("ERROR", message);
        }
    }

}
