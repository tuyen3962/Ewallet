package com.example.ewalletexample.Server.api.transaction.list;

import android.os.Build;

import com.example.ewalletexample.Server.api.bank.BankMappingCallback;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.data.TransactionDetail;
import com.example.ewalletexample.data.TransactionHistory;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import androidx.annotation.RequiresApi;

public class TransactionHistoryAPI {
    BankMappingCallback response;
    private String userid;
    private long startTime;
    private int pageSize;
    private Gson gson;

    public TransactionHistoryAPI(BankMappingCallback response, String userid, long startTime, int pageSize){
        this.response = response;
        this.userid = userid;
        this.startTime = startTime;
        this.pageSize = pageSize;
        gson = new Gson();
    }

    public void SetPageSize(int pageSize){
        this.pageSize = pageSize;
    }

    public void SetStartTime(long time){
        this.startTime = time;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void StartGetHistoryTransaction(String publicKeyString, String secretKeyString1, String secretKeyString2) {
        try {
            PublicKey publicKey = SecurityUtils.generatePublicKey(publicKeyString);
            SecretKey secretKey1 = SecurityUtils.generateAESKeyFromText(secretKeyString1);
            SecretKey secretKey2 = SecurityUtils.generateAESKeyFromText(secretKeyString2);

            String encryptSecondKeyByRSA = SecurityUtils.encryptRSA(publicKey, secretKeyString2);

            String encryptKeyByRSA = SecurityUtils.encryptRSA(publicKey, secretKeyString1);
            String encryptKeyByAES = SecurityUtils.decryptAES(secretKey2, encryptKeyByRSA);

            String header = userid + startTime + pageSize + secretKeyString1 + secretKeyString2;
            String hashHeader = SecurityUtils.encryptHmacSha256(secretKey1, header);
            String encryptHeader = SecurityUtils.encryptAES(secretKey2, hashHeader);
            String encryptHeaderByRSA = SecurityUtils.encryptRSA(publicKey, encryptHeader);

            String[] arr = new String[]{"userid:"+userid,"starttime:"+startTime,"pagesize:"+ pageSize, "key:"+encryptKeyByAES,"secondKey:"+encryptSecondKeyByRSA};

            new GetTransactionHistoryAPI().execute(ServerAPI.HISTORY_TRANSACTION.GetUrl(), HandlerJsonData.ExchangeToJsonString(arr), encryptHeaderByRSA);
        } catch (JSONException e){
            response.MappingResponse(false, null);
        }
    }

    class GetTransactionHistoryAPI extends RequestServerAPI implements RequestServerFunction {
        public GetTransactionHistoryAPI(){
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
            List<TransactionHistory> transactionDetails = new ArrayList<>();

            JSONArray transactionArr = jsonData.getJSONArray("histories");
            for (int i = 0; i < transactionArr.length(); i++) {
                JSONObject transaction = transactionArr.getJSONObject(i);
                transactionDetails.add(gson.fromJson(transaction.toString(), TransactionHistory.class));
            }

            response.MappingResponse(true, transactionDetails);
        }

        @Override
        public void ShowError(int errorCode, String message) {
            response.MappingResponse(false, null);
        }
    }
}
