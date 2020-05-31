package com.example.ewalletexample.Server.api.bank.unlink;

import android.os.Build;
import android.util.Log;

import com.example.ewalletexample.Server.api.bank.BankMappingCallback;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;

import javax.crypto.SecretKey;

import androidx.annotation.RequiresApi;

public class UnlinkBankConnectedAPI {
    private BankMappingCallback response;
    private String userid;
    private BankInfo bankInfo;

    public UnlinkBankConnectedAPI(BankMappingCallback response, String userid, BankInfo info){
        this.response = response;
        this.userid = userid;
        this.bankInfo = info;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void StartUnlink(String publicKeyString, String secretKeyString1, String secretKeyString2){
        try {
            PublicKey publicKey = SecurityUtils.generatePublicKey(publicKeyString);
            SecretKey secretKey1 = SecurityUtils.generateAESKeyFromText(secretKeyString1);
            SecretKey secretKey2 = SecurityUtils.generateAESKeyFromText(secretKeyString2);

            String encryptSecondKeyByRSA = SecurityUtils.encryptRSA(publicKey, secretKeyString2);

            String encryptKeyByRSA = SecurityUtils.encryptRSA(publicKey, secretKeyString1);
            String encryptKeyByAES = SecurityUtils.decryptAES(secretKey2, encryptKeyByRSA);

            String header = userid + bankInfo.getF6CardNo() + bankInfo.getL4CardNo() + bankInfo.getBankCode() + secretKeyString1 + secretKeyString2;
            String hashHeader = SecurityUtils.encryptHmacSha256(secretKey1, header);
            String encryptHeader = SecurityUtils.encryptAES(secretKey2, hashHeader);
            String encryptHeaderByRSA = SecurityUtils.encryptRSA(publicKey, encryptHeader);

            String[] arr = new String[]{"userid:"+ userid, "bankcode:"+ bankInfo.getBankCode(),
                    "f6cardno:"+bankInfo.getF6CardNo(), "l4cardno:" + bankInfo.getL4CardNo(),
                    "key:" + encryptKeyByAES, "secondKey:" + encryptSecondKeyByRSA};
            String json = HandlerJsonData.ExchangeToJsonString(arr);
            new UnlinkBankAPI().execute(ServerAPI.UNLINK_BANK_CARD_API.GetUrl(), json, encryptHeaderByRSA);
        } catch (JSONException e) {
            response.MappingResponse(false, null);
            e.printStackTrace();
        }

    }

    class UnlinkBankAPI extends RequestServerAPI implements RequestServerFunction {
        public UnlinkBankAPI(){
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
            int bankreturncode = jsonData.getInt("bankreturncode");
            if(bankreturncode == 1){
                response.MappingResponse(true, null);
            }
            else {
                response.MappingResponse(false, null);
            }
        }

        @Override
        public void ShowError(int errorCode, String message) {
            response.MappingResponse(false, null);
        }
    }

}
