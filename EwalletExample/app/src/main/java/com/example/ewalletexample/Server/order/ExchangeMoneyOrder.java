package com.example.ewalletexample.Server.order;

import android.util.Log;

import com.example.ewalletexample.Server.VerifyPin.VerifyPinAPI;
import com.example.ewalletexample.Server.VerifyPin.VerifyResponse;
import com.example.ewalletexample.Server.balance.BalanceResponse;
import com.example.ewalletexample.Server.balance.GetBalanceAPI;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

public class ExchangeMoneyOrder implements VerifyResponse, StatusOrder{
    private String userid, amount, pin, receiverphone;
    private long orderid, fee;
    private SourceFund sourceFund;
    private VerifyPinAPI verifyPinAPI;
    private BalanceResponse response;
    private GetStatusExchangeMoneyOrderThread statusExchangeMoneyOrder;

    public ExchangeMoneyOrder(String userid, String receiverphone, String pin, String amount, long fee, SourceFund sourceFund, BalanceResponse response) throws JSONException {
        this.userid = userid;
        this.receiverphone = receiverphone;
        this.pin = pin;
        this.amount = amount;
        this.fee = fee;
        this.sourceFund = sourceFund;
        verifyPinAPI = new VerifyPinAPI(userid, pin, this);
        this.response = response;
    }

    public void StartCreateExchangeMoneyOrder(){
        verifyPinAPI.StartVerify();
    }

    @Override
    public void VerifyPinResponse(boolean isSuccess) {
        if(isSuccess){
            try {
                String[] arr = new String[]{"userid:"+userid,"receiverphone:"+receiverphone,"amount:"+Long.valueOf(amount)};
                String json = HandlerJsonData.ExchangeToJsonString(arr);
                new CreateExchangeMoneyOrder().execute(ServerAPI.CREATE_EXCHANGE_MONEY_ORDER.GetUrl(), json);
            } catch (JSONException e){

            }
        }
    }

    class CreateExchangeMoneyOrder extends RequestServerAPI implements RequestServerFunction {
        public CreateExchangeMoneyOrder(){
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
            orderid = jsonData.getLong("orderid");
            try {
                String[] arr = new String[]{"userid:"+userid,"orderid:"+orderid,"sourceoffund:"+sourceFund.GetCode(),
                        "bankcode:","f6cardno:", "l4cardno:","amount:"+amount,"pin:"+pin,
                        "servicetype:"+ Service.EXCHANGE_SERVICE_TYPE.GetCode()};

                String json = HandlerJsonData.ExchangeToJsonString(arr);
                new SubmitExchangeMoneyOrder().execute(ServerAPI.SUBMIT_TRANSACTION.GetUrl(), json);
            } catch (JSONException e){

            }
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }

    class SubmitExchangeMoneyOrder extends RequestServerAPI implements RequestServerFunction{
        public SubmitExchangeMoneyOrder(){
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
//            long transactionid = jsonData.getLong("transactionid");
            try {
                String[] arr = new String[]{"userid:"+userid,"orderid:"+orderid,"password:"+pin};

                String json = HandlerJsonData.ExchangeToJsonString(arr);
                StartGetStatusOrder(json);
            } catch (JSONException e){

            }
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }

    void StartGetStatusOrder(String jsonData){
        statusExchangeMoneyOrder = new GetStatusExchangeMoneyOrderThread(this, jsonData);
        statusExchangeMoneyOrder.run();
    }

    @Override
    public void TransactionSuccess() {
        GetBalanceAPI balanceAPI = new GetBalanceAPI(userid,response);
        balanceAPI.GetBalance();
    }

    @Override
    public void TransactionExcuting() {
        statusExchangeMoneyOrder.run();
    }

    @Override
    public void TransactionFail() {
        Log.d("FAILURE","TransactionFail");
    }

    class GetStatusExchangeMoneyOrderThread extends Thread{
        StatusOrder statusTopupOrder;
        String jsonData;

        public GetStatusExchangeMoneyOrderThread(StatusOrder getStatusExchangeMoneyOrder, String json){
            jsonData = json;
            statusTopupOrder = getStatusExchangeMoneyOrder;
        }

        @Override
        public void run(){
            new GetStatusExchangeMoneyOrder(statusTopupOrder).execute(ServerAPI.GET_STATUS_EXCHANGE_MONEY_ORDER.GetUrl(), jsonData);
        }
    }

    class GetStatusExchangeMoneyOrder extends RequestServerAPI implements RequestServerFunction{
        private StatusOrder statusOrder;

        public GetStatusExchangeMoneyOrder(StatusOrder statusOrder){
            this.statusOrder = statusOrder;
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if(code == ErrorCode.SUCCESS.GetValue()){
                statusOrder.TransactionSuccess();
                return true;
            }
            else if(code > ErrorCode.SUCCESS.GetValue()){
                statusOrder.TransactionExcuting();
                return false;
            }

            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            Log.d("TAG", "GetStatusTopupOrder DataHandle: success" );
        }

        @Override
        public void ShowError(int errorCode, String message) {
            statusOrder.TransactionFail();
        }
    }
}
