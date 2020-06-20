package com.example.ewalletexample.Server.api.balance;

import android.os.Build;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.GsonUtils;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;

import javax.crypto.SecretKey;

import androidx.annotation.RequiresApi;

public class GetBalanceAPI {
    private GetBalanceRequest request;
    private String userid;
    private BalanceResponse balanceResponse;
    private PublicKey publicKey;

    public GetBalanceAPI(PublicKey publicKey, String userid, BalanceResponse response){
        this.userid = userid;
        this.publicKey = publicKey;
        request = new GetBalanceRequest();
        balanceResponse = response;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void GetBalance(String secretKeyString1, String secretKeyString2)  {
        SecretKey secretKey1 = SecurityUtils.generateAESKeyFromText(secretKeyString1);
        SecretKey secretKey2 = SecurityUtils.generateAESKeyFromText(secretKeyString2);

        String encryptAESSecretKey1 = SecurityUtils.encryptAES(secretKey2, secretKeyString1);
        String encryptRSASecretKey1 = SecurityUtils.encryptRSA(publicKey, encryptAESSecretKey1);

        String encryptRSASecretKey2 = SecurityUtils.encryptRSA(publicKey, secretKeyString2);
        request.setUserid(this.userid);
        request.setKey(encryptRSASecretKey1);
        request.setSecondKey(encryptRSASecretKey2);

        String header = userid + secretKeyString1 + secretKeyString2;
        String hashHeaderBySecretKey1 = SecurityUtils.encryptHmacSha256(secretKey1, header);
        String encryptAESHeaderBySecretKey2 = SecurityUtils.encryptAES(secretKey2, hashHeaderBySecretKey1);
        String encryptRSAHeader = SecurityUtils.encryptRSA(publicKey, encryptAESHeaderBySecretKey2);
        new GetBalance().execute(ServerAPI.GET_BALANCE_API.GetUrl(), GsonUtils.toJsonString(request), encryptRSAHeader);
    }

    class GetBalance extends RequestServerAPI implements RequestServerFunction{
        public GetBalance(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if(code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }

            ShowError(code, ErrorCode.VALIDATE_USER_ID_INVALID.GetMessage());
            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            long amount = jsonData.getLong("amount");
            balanceResponse.GetBalanceResponse(amount);
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }
}
