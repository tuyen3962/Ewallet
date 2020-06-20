package com.example.ewalletexample.Server.api.transaction.fee;

import android.os.Build;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;

import javax.crypto.SecretKey;

import androidx.annotation.RequiresApi;

public class GetServiceFeeAPI {
    private GetServiceFeeRequest request;
    private GetServiceFeeResponse response;
    private Gson gson;

    public GetServiceFeeAPI(GetServiceFeeRequest request, GetServiceFeeResponse response){
        this.request = request;
        this.response = response;
        gson = new Gson();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void StartRequest(String publicKeyString){
        PublicKey publicKey = SecurityUtils.generatePublicKey(publicKeyString);
        SecretKey secretKey1 = SecurityUtils.generateAESKeyFromText(request.key);
        SecretKey secretKey2 = SecurityUtils.generateAESKeyFromText(request.secondKey);

        String encryptSecondKeyByRSA = SecurityUtils.encryptRSA(publicKey, request.secondKey);

        String encryptKeyByRSA = SecurityUtils.encryptRSA(publicKey, request.key);
        String encryptKeyByAES = SecurityUtils.decryptAES(secretKey2, encryptKeyByRSA);

        String header = request.service_code + request.key + request.secondKey;
        String hashHeader = SecurityUtils.encryptHmacSha256(secretKey1, header);
        String encryptHeader = SecurityUtils.encryptAES(secretKey2, hashHeader);
        String encryptHeaderByRSA = SecurityUtils.encryptRSA(publicKey, encryptHeader);

        request.key = encryptKeyByAES;
        request.secondKey = encryptSecondKeyByRSA;

        new GetTransactoinFee().execute(ServerAPI.GET_TRANSACTION_FEE.GetUrl(), gson.toJson(request), encryptHeaderByRSA);
    }

    class GetTransactoinFee extends RequestServerAPI implements RequestServerFunction {
        public GetTransactoinFee(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if (code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }

            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            response.GetTransactionFee(jsonData.getLong("service_fee"));
        }

        @Override
        public void ShowError(int errorCode, String message) {
            response.GetTransactionFee(-1);
        }
    }
}
