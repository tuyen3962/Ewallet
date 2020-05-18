package com.example.ewalletexample.Server.api.order;

import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

public class ExchangeMoneyOrder extends Order{
    private String receiverphone, note;

    public ExchangeMoneyOrder(String userid, String receiverphone, String pin, String amount, long fee, SourceFund sourceFund, String note, OrderResponse response)  {
        super(userid, pin, amount, fee, sourceFund, Service.EXCHANGE_SERVICE_TYPE, response);
        this.receiverphone = receiverphone;
        this.note = note;
    }

    @Override
    protected void VerifyPinSuccess() throws JSONException {
        String[] arr = new String[]{"userid:"+userid,"receiverphone:"+receiverphone,"amount:"+Long.valueOf(amount),"note:"+note};
        String json = HandlerJsonData.ExchangeToJsonString(arr);
        CreateOrder(ServerAPI.CREATE_EXCHANGE_MONEY_ORDER, json);
    }

    @Override
    protected void CreateOrderSuccess() throws JSONException {
        String[] arr = new String[]{"userid:"+userid,"orderid:"+orderid,"sourceoffund:"+sourceFund.GetCode(),
                "bankcode:","f6cardno:", "l4cardno:","amount:"+amount,"pin:"+pin,
                "servicetype:"+ Service.EXCHANGE_SERVICE_TYPE.GetCode()};

        String json = HandlerJsonData.ExchangeToJsonString(arr);
        SubmitOrder(json);
    }

    @Override
    protected void SubmitOrderSuccess() {
        GetStausOrder(ServerAPI.GET_STATUS_EXCHANGE_MONEY_ORDER);
    }

    @Override
    protected void GetDataFromJsonFromStatusOrder(JSONObject json) {

    }
}
