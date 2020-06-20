package com.example.ewalletexample.Server.api.transaction.single;

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

public class TransactionDetailAPI {
    private String userid, transactionId;
    private TransactionDetailResponse response;

    public TransactionDetailAPI(String userid, String transactionId, TransactionDetailResponse response){
        this.userid = userid;
        this.transactionId = transactionId;
        this.response = response;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void StartRequest(String publicKeyString, String secretKeyString1, String secretKeyString2){
        try {
            PublicKey publicKey = SecurityUtils.generatePublicKey(publicKeyString);
            SecretKey secretKey1 = SecurityUtils.generateAESKeyFromText(secretKeyString1);
            SecretKey secretKey2 = SecurityUtils.generateAESKeyFromText(secretKeyString2);

            String encryptSecondKeyByRSA = SecurityUtils.encryptRSA(publicKey, secretKeyString2);

            String encryptKeyByRSA = SecurityUtils.encryptRSA(publicKey, secretKeyString1);
            String encryptKeyByAES = SecurityUtils.decryptAES(secretKey2, encryptKeyByRSA);

            String header = this.userid + this.transactionId + secretKeyString1 + secretKeyString2;
            String hashHeader = SecurityUtils.encryptHmacSha256(secretKey1, header);
            String encryptHeader = SecurityUtils.encryptAES(secretKey2, hashHeader);
            String encryptHeaderByRSA = SecurityUtils.encryptRSA(publicKey, encryptHeader);

            String[] arr = new String[]{"userid:"+this.userid,"transactionid:"+Long.valueOf(transactionId),"key:"+encryptKeyByAES,"secondKey:"+encryptSecondKeyByRSA};

            new GetTransactionDetailAPI().execute(ServerAPI.TRANSACTION_DETAIL.GetUrl(), HandlerJsonData.ExchangeToJsonString(arr), encryptHeaderByRSA);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class GetTransactionDetailAPI extends RequestServerAPI implements RequestServerFunction {
        public GetTransactionDetailAPI(){
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
            response.TransactionResponse(jsonData.getJSONObject("data").toString());
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }
}
