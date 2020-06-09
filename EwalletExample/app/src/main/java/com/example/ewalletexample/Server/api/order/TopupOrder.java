package com.example.ewalletexample.Server.api.order;

import android.os.Build;

import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.RequiresApi;

public class TopupOrder extends Order {
    private BankInfo bankInfo;
    private OrderResponse response;

    public TopupOrder(String userid, String pin, String amount, long fee, SourceFund codeSourceFund, BankInfo bankInfo, OrderResponse response) {
        super(userid, pin, amount, fee, codeSourceFund, Service.TOPUP_SERVICE_TYPE, response);
        this.bankInfo = bankInfo;
        this.response = response;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void VerifyPinSuccess() throws JSONException {
        String[] arr = new String[]{"userid:"+userid,"amount:"+Long.valueOf(amount),"key:"+encryptSecretKey1,"secondKey:"+encryptSecretKey2};
        String header = userid + amount + secretKeyString1 + secretKeyString2;
        String hashHeader = SecurityUtils.encryptHmacSha256(secretKey1, header);
        String encryptHeader = SecurityUtils.encryptAES(secretKey2, hashHeader);
        String encryptHeaderByRSA = SecurityUtils.encryptRSA(publicKey, encryptHeader);
        CreateOrder(ServerAPI.CREATE_TOPUP_ORDER, HandlerJsonData.ExchangeToJsonString(arr), encryptHeaderByRSA);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void CreateOrderSuccess() throws JSONException {
        String[] arr = new String[]{"userid:"+userid,"orderid:"+orderid,"sourceoffund:"+sourceFund.GetCode(),
                "bankcode:"+bankInfo.getBankCode(),"f6cardno:"+bankInfo.getF6CardNo(),
                "l4cardno:"+bankInfo.getL4CardNo(),"amount:"+amount,"pin:"+pin,
                "servicetype:"+ Service.TOPUP_SERVICE_TYPE.GetCode(),"key:"+encryptSecretKey1,"secondKey:"+encryptSecretKey2};

        String header = userid + orderid + sourceFund.GetCode() + bankInfo.getBankCode() +
                bankInfo.getF6CardNo() + bankInfo.getL4CardNo() + amount + pin +
                Service.TOPUP_SERVICE_TYPE.GetCode() + secretKeyString1 + secretKeyString2;
        String hashHeader = SecurityUtils.encryptHmacSha256(secretKey1, header);
        String encryptHeader = SecurityUtils.encryptAES(secretKey2, hashHeader);
        String encryptHeaderByRSA = SecurityUtils.encryptRSA(publicKey, encryptHeader);
        SubmitOrder(HandlerJsonData.ExchangeToJsonString(arr), encryptHeaderByRSA);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void SubmitOrderSuccess() {
        GetStausOrder(ServerAPI.GET_STATUS_TOPUP_ORDER);
    }

    @Override
    protected void GetDataFromJsonFromStatusOrder(JSONObject json) {

    }
}
