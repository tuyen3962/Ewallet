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

public class WithdrawOrder extends Order{
    private BankInfo bankInfo;

    public WithdrawOrder(String userid, String pin, String amount, long fee, SourceFund codeSourceFund, BankInfo bankInfoJson, OrderResponse response)  {
        super(userid, pin, amount, fee, codeSourceFund, Service.WITHDRAW_SERVICE_TYPE, response);
        this.bankInfo = bankInfoJson;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void VerifyPinSuccess() throws JSONException {
        String[] arr = new String[]{"userid:"+userid,"f6cardno:"+bankInfo.getF6CardNo(),"l4cardno:"+bankInfo.getL4CardNo(),
                "bankcode:"+bankInfo.getBankCode(),"amount:"+Long.valueOf(amount)};
        String header = userid + bankInfo.getF6CardNo() + bankInfo.getL4CardNo() + bankInfo.getBankCode() + amount + secretKeyString1 + secretKeyString2;
        String hashHeader = SecurityUtils.encryptHmacSha256(secretKey1, header);
        String encryptHeader = SecurityUtils.encryptAES(secretKey2, hashHeader);
        String encryptHeaderByRSA = SecurityUtils.encryptRSA(publicKey, encryptHeader);
        CreateOrder(ServerAPI.CREATE_WITHDRAW_ORDER, HandlerJsonData.ExchangeToJsonString(arr), encryptHeaderByRSA);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void CreateOrderSuccess() throws JSONException {
        String[] arr = new String[]{"userid:"+userid,"orderid:"+orderid,"sourceoffund:"+sourceFund.GetCode(),
                "bankcode:"+bankInfo.getBankCode(),"f6cardno:"+bankInfo.getF6CardNo(),
                "l4cardno:"+bankInfo.getL4CardNo(),"amount:"+amount,"pin:"+pin,
                "servicetype:"+ Service.WITHDRAW_SERVICE_TYPE.GetCode()};
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
        GetStausOrder(ServerAPI.GET_STATUS_WITHDRAW_ORDER);
    }

    @Override
    protected void GetDataFromJsonFromStatusOrder(JSONObject json) {

    }
}
