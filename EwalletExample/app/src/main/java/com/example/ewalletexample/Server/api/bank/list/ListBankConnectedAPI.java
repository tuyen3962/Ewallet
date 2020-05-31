package com.example.ewalletexample.Server.api.bank.list;

import android.os.Build;

import com.example.ewalletexample.Server.api.bank.BankMappingCallback;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import androidx.annotation.RequiresApi;

public class ListBankConnectedAPI {
    private String userid;
    private List<BankInfo> bankInfoList;
    private BankMappingCallback response;

    public ListBankConnectedAPI(BankMappingCallback response, String userid){
        this.response = response;
        this.userid = userid;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void GetListBank(String publicKeyString, String secretKeyString1, String secretKeyString2){
        try {
            PublicKey publicKey = SecurityUtils.generatePublicKey(publicKeyString);
            SecretKey secretKey1 = SecurityUtils.generateAESKeyFromText(secretKeyString1);
            SecretKey secretKey2 = SecurityUtils.generateAESKeyFromText(secretKeyString2);

            String encryptSecondKeyByRSA = SecurityUtils.encryptRSA(publicKey, secretKeyString2);

            String encryptKeyByRSA = SecurityUtils.encryptRSA(publicKey, secretKeyString1);
            String encryptKeyByAES = SecurityUtils.decryptAES(secretKey2, encryptKeyByRSA);

            String header = userid + secretKeyString1 + secretKeyString2;
            String hashHeader = SecurityUtils.encryptHmacSha256(secretKey1, header);
            String encryptHeader = SecurityUtils.encryptAES(secretKey2, hashHeader);
            String encryptHeaderByRSA = SecurityUtils.encryptRSA(publicKey, encryptHeader);

            String json = HandlerJsonData.ExchangeToJsonString(new String[]{"userid:"+ userid, "key:" + encryptKeyByAES, "secondKey:" + encryptSecondKeyByRSA});
            new GetListBankConnected().execute(ServerAPI.GET_BANK_LINKING_API.GetUrl(), json, encryptHeaderByRSA);
        } catch (JSONException e) {
            e.printStackTrace();
            response.MappingResponse(false, null);
        }
    }

    class GetListBankConnected extends RequestServerAPI implements RequestServerFunction {
        public GetListBankConnected(){
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
            bankInfoList = new ArrayList<>();
            JSONArray cardArray = jsonData.getJSONArray("cards");
            if(cardArray.length() == 0){
                response.MappingResponse(false, null);
                return;
            }

            for(int i = 0; i < cardArray.length(); i++){
                JSONObject card = cardArray.getJSONObject(i);
                String cardName = card.getString("cardname");
                String bankCode = card.getString("bankcode");
                String f6CardNo = card.getString("f6cardno");
                String l4CardNo = card.getString("l4cardno");
                BankInfo bankInfo = new BankInfo(cardName, bankCode, f6CardNo, l4CardNo);
                bankInfoList.add(bankInfo);
            }

            response.MappingResponse(true, bankInfoList);
        }

        @Override
        public void ShowError(int errorCode, String message) {
            response.MappingResponse(false, null);
        }
    }

}
