package com.example.ewalletexample.Server.api.bank.link;

import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.example.ewalletexample.ChooseBankConnectActivity;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;

import javax.crypto.SecretKey;

import androidx.annotation.RequiresApi;

public class LinkAccountWithBankAPI {
    private LinkBankRequest request;
    private LinkBankResponse response;
    Gson gson;

    public LinkAccountWithBankAPI(LinkBankRequest request, LinkBankResponse response){
        this.request = request;
        this.response = response;
        gson = new Gson();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void StartLinkCard(String publicKeyString, String secretKeyString1, String secretKeyString2){
        PublicKey publicKey = SecurityUtils.generatePublicKey(publicKeyString);
        SecretKey secretKey1 = SecurityUtils.generateAESKeyFromText(secretKeyString1);
        SecretKey secretKey2 = SecurityUtils.generateAESKeyFromText(secretKeyString2);

        String encryptSecondKeyByRSA = SecurityUtils.encryptRSA(publicKey, secretKeyString2);

        String encryptKeyByRSA = SecurityUtils.encryptRSA(publicKey, secretKeyString1);
        String encryptKeyByAES = SecurityUtils.decryptAES(secretKey2, encryptKeyByRSA);

        String header = request.GetString() + secretKeyString1 + secretKeyString2;
        String hashHeader = SecurityUtils.encryptHmacSha256(secretKey1, header);
        String encryptHeader = SecurityUtils.encryptAES(secretKey2, hashHeader);
        String encryptHeaderByRSA = SecurityUtils.encryptRSA(publicKey, encryptHeader);

        request.key = encryptKeyByAES;
        request.secondKey = encryptSecondKeyByRSA;
        new LinkAccountWithBank().execute(ServerAPI.LINK_BANK_CARD_API.GetUrl(), gson.toJson(request), encryptHeaderByRSA);
    }

    class LinkAccountWithBank extends RequestServerAPI implements RequestServerFunction {
        public LinkAccountWithBank(){
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
            int bankReturnCode = jsonData.getInt("bankreturncode");

            if (bankReturnCode == 1){
                BankInfo bankInfo = new BankInfo();
                bankInfo.setBankCode(request.bankcode);
                bankInfo.setF6CardNo(request.cardno.substring(0, 6));
                bankInfo.setL4CardNo(request.cardno.substring(12));
                response.GetBankInfo(bankInfo);
            } else {
                response.GetBankInfo(null);
            }
        }

        @Override
        public void ShowError(int errorCode, String message) {
            response.GetBankInfo(null);
        }
    }
}
