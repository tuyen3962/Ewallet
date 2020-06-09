package com.example.ewalletexample.Server.api.VerifyPin;

import android.os.Build;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;

import javax.crypto.SecretKey;

import androidx.annotation.RequiresApi;

public class VerifyPinAPI {
    private String userid, pin;
    private VerifyResponse response;

    public VerifyPinAPI(String userid, String pin, VerifyResponse response){
        this.userid = userid;
        this.pin = pin;
        this.response = response;
    }

    public VerifyPinAPI(String userid, VerifyResponse response){
        this.userid = userid;
        this.response = response;
    }

    public void SetPin(String pin){
        this.pin = pin;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void StartVerify(String publicKeyString, String secretKeyString1, String secretKeyString2){
        try {
            PublicKey publicKey = SecurityUtils.generatePublicKey(publicKeyString);
            SecretKey secretKey1 = SecurityUtils.generateAESKeyFromText(secretKeyString1);
            SecretKey secretKey2 = SecurityUtils.generateAESKeyFromText(secretKeyString2);

            String encryptSecretKey1ByAES = SecurityUtils.encryptAES(secretKey2, secretKeyString1);
            String encryptSecretKey1ByRSA = SecurityUtils.encryptRSA(publicKey, encryptSecretKey1ByAES);

            String encryptSecondKey = SecurityUtils.encryptRSA(publicKey, secretKeyString2);

            String rawHeader = userid + pin + secretKeyString1 + secretKeyString2;
            String hashHeader = SecurityUtils.encryptHmacSha256(secretKey1, rawHeader);
            String encryptHeader = SecurityUtils.encryptAES(secretKey2, hashHeader);
            String encryptHeaderByRSA = SecurityUtils.encryptRSA(publicKey, encryptHeader);

            String[] arr = new String[]{"userid:"+userid,"pin:"+pin,
                    "key:"+encryptSecretKey1ByRSA,"secondKey:"+encryptSecondKey};
            new RequestVerifyPin().execute(ServerAPI.VERIFY_PIN_API.GetUrl(),
                    HandlerJsonData.ExchangeToJsonString(arr), encryptHeaderByRSA);
        }catch (JSONException e){
            return;
        }
    }

    class RequestVerifyPin extends RequestServerAPI implements RequestServerFunction {
        public RequestVerifyPin(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if(code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }

            ShowError(0, "Không đúng mã pin");
            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            response.VerifyPinResponse(true);
        }

        @Override
        public void ShowError(int errorCode, String message) {
            response.VerifyPinResponse(false);
        }
    }

}
