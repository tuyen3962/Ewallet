package com.example.ewalletexample.Server.api.order;

import android.os.Build;

import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.RequiresApi;

public class ExchangeMoneyOrder extends Order{
    private String receiverphone, note;

    public ExchangeMoneyOrder(String userid, String receiverphone, String pin, String amount, long fee, SourceFund sourceFund, String note, OrderResponse response)  {
        super(userid, pin, amount, fee, sourceFund, Service.EXCHANGE_SERVICE_TYPE, response);
        this.receiverphone = receiverphone;
        this.note = note;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void VerifyPinSuccess() throws JSONException {
        String[] arr = new String[]{"userid:"+userid,"receiverphone:"+receiverphone,
                "amount:"+Long.valueOf(amount),"note:"+note,"key:"+encryptSecretKey1,"secondKey:"+encryptSecretKey2};
        String header = userid + receiverphone + amount + note + secretKeyString1 + secretKeyString2;
        String hashHeader = SecurityUtils.encryptHmacSha256(secretKey1, header);
        String encryptHeader = SecurityUtils.encryptAES(secretKey2, hashHeader);
        String encryptHeaderByRSA = SecurityUtils.encryptRSA(publicKey, encryptHeader);
        CreateOrder(ServerAPI.CREATE_EXCHANGE_MONEY_ORDER, HandlerJsonData.ExchangeToJsonString(arr), encryptHeaderByRSA);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void CreateOrderSuccess() throws JSONException {
        String[] arr = new String[]{"userid:"+userid,"orderid:"+orderid,"sourceoffund:"+sourceFund.GetCode(),
                "bankcode:","f6cardno:", "l4cardno:","amount:"+amount,"pin:"+pin,"servicetype:"+ Service.EXCHANGE_SERVICE_TYPE.GetCode(),
                "key:"+encryptSecretKey1,"secondKey:"+encryptSecretKey2};
        String header = userid + orderid + sourceFund.GetCode() + amount + Service.EXCHANGE_SERVICE_TYPE.GetCode() + secretKeyString1 + secretKeyString2;
        String hashHeader = SecurityUtils.encryptHmacSha256(secretKey1, header);
        String encryptHeader = SecurityUtils.encryptAES(secretKey2, hashHeader);
        String encryptHeaderByRSA = SecurityUtils.encryptRSA(publicKey, encryptHeader);
        SubmitOrder(HandlerJsonData.ExchangeToJsonString(arr), encryptHeaderByRSA);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void SubmitOrderSuccess() {
        GetStausOrder(ServerAPI.GET_STATUS_EXCHANGE_MONEY_ORDER);
    }

    @Override
    protected void GetDataFromJsonFromStatusOrder(JSONObject json) {

    }
}
