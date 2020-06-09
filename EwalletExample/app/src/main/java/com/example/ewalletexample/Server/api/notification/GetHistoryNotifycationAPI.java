package com.example.ewalletexample.Server.api.notification;

import android.os.Build;

import com.example.ewalletexample.Server.api.transaction.TransactionDetailAPI;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
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

public class GetHistoryNotifycationAPI {
    private HistoryNotifyRequest request;
    private HistoryNotifycationResponse response;
    private Gson gson;
    private String secretKeyString1, secretKeyString2, encryptSecondKeyByRSA, encryptKeyByAES;
    private PublicKey publicKey;
    private SecretKey secretKey1, secretKey2;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public GetHistoryNotifycationAPI(HistoryNotifyRequest request, String publicKeyString, HistoryNotifycationResponse response){
        this.request = request;
        this.response = response;
        gson = new Gson();
        this.secretKeyString1 = request.key;
        this.secretKeyString2 = request.secondKey;
        this.publicKey = SecurityUtils.generatePublicKey(publicKeyString);
        this.secretKey1 = SecurityUtils.generateAESKeyFromText(this.secretKeyString1);
        this.secretKey2 = SecurityUtils.generateAESKeyFromText(this.secretKeyString2);
        this.encryptSecondKeyByRSA = SecurityUtils.encryptRSA(publicKey, secretKeyString2);
        this.encryptKeyByAES = SecurityUtils.decryptAES(secretKey2, SecurityUtils.encryptRSA(publicKey, secretKeyString1));
        request.key = encryptKeyByAES;
        request.secondKey = encryptSecondKeyByRSA;
    }

    public void SetStartTime(long time){
        request.starttime = time;
    }

    public void SetPage(int page){
        this.request.pagesize = page;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void StartRequest(){
        String header = request.userid + request.pagesize + request.starttime + secretKeyString1 + secretKeyString2;
        String hashHeader = SecurityUtils.encryptHmacSha256(secretKey1, header);
        String encryptHeader = SecurityUtils.encryptAES(secretKey2, hashHeader);
        String encryptHeaderByRSA = SecurityUtils.encryptRSA(publicKey, encryptHeader);

        new GetHistoryNotification().execute(ServerAPI.HISTORY_NOTIFICATION.GetUrl(), gson.toJson(request), encryptHeaderByRSA);
    }

    class GetHistoryNotification extends RequestServerAPI implements RequestServerFunction {

        public GetHistoryNotification(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if (code == ErrorCode.SUCCESS.GetValue())
                return true;

            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            JSONArray array = jsonData.getJSONArray("histories");
            List<UserNotifyEntity> userNotifyEntityList = new ArrayList<>();
            if (array.length() > 0){
                for (int i = 0; i < array.length(); i++){
                    JSONObject jsonObject = array.getJSONObject(i);
                    userNotifyEntityList.add(gson.fromJson(jsonObject.toString(), UserNotifyEntity.class));
                }
            }

            response.ListNotifyResponse(userNotifyEntityList);
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }
}
