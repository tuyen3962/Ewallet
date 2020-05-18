package com.example.ewalletexample.Server.api.order;

import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

public class TopupOrder extends Order {
    private BankInfo bankInfo;
    private OrderResponse response;

    public TopupOrder(String userid, String pin, String amount, long fee, SourceFund codeSourceFund, BankInfo bankInfo, OrderResponse response) {
        super(userid, pin, amount, fee, codeSourceFund, Service.TOPUP_SERVICE_TYPE, response);
        this.bankInfo = bankInfo;
        this.response = response;
    }

    @Override
    protected void VerifyPinSuccess() throws JSONException {
        String[] arr = new String[]{"userid:"+userid,"amount:"+Long.valueOf(amount)};
        String json = HandlerJsonData.ExchangeToJsonString(arr);
        CreateOrder(ServerAPI.CREATE_TOPUP_ORDER, json);
    }

    @Override
    protected void CreateOrderSuccess() throws JSONException {
        String[] arr = new String[]{"userid:"+userid,"orderid:"+orderid,"sourceoffund:"+sourceFund.GetCode(),
                "bankcode:"+bankInfo.getBankCode(),"f6cardno:"+bankInfo.getF6CardNo(),
                "l4cardno:"+bankInfo.getL4CardNo(),"amount:"+amount,"pin:"+pin,
                "servicetype:"+ Service.TOPUP_SERVICE_TYPE.GetCode()};

        String json = HandlerJsonData.ExchangeToJsonString(arr);
        SubmitOrder(json);
    }

    @Override
    protected void SubmitOrderSuccess() {
        GetStausOrder(ServerAPI.GET_STATUS_TOPUP_ORDER);
    }

    @Override
    protected void GetDataFromJsonFromStatusOrder(JSONObject json) {

    }
}
