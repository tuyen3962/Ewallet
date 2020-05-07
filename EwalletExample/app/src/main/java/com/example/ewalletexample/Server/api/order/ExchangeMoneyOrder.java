package com.example.ewalletexample.Server.api.order;

import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

public class ExchangeMoneyOrder extends Order{
    private String receiverphone;

    public ExchangeMoneyOrder(String userid, String receiverphone, String pin, String amount, long fee, SourceFund sourceFund, OrderResponse response)  {
        super(userid, pin, amount, fee, sourceFund, Service.EXCHANGE_SERVICE_TYPE, response);
        this.receiverphone = receiverphone;
    }

    @Override
    protected void VerifyPinSuccess() throws JSONException {
        String[] arr = new String[]{"userid:"+userid,"receiverphone:"+receiverphone,"amount:"+Long.valueOf(amount)};
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

//    class CreateExchangeMoneyOrder extends RequestServerAPI implements RequestServerFunction {
//        public CreateExchangeMoneyOrder(){
//            SetRequestServerFunction(this);
//        }
//
//        @Override
//        public boolean CheckReturnCode(int code) {
//            if(code == ErrorCode.SUCCESS.GetValue()){
//                return true;
//            }
//
//            return false;
//        }
//
//        @Override
//        public void DataHandle(JSONObject jsonData) throws JSONException {
//            orderid = jsonData.getLong("orderid");
//            try {
//                String[] arr = new String[]{"userid:"+userid,"orderid:"+orderid,"sourceoffund:"+sourceFund.GetCode(),
//                        "bankcode:","f6cardno:", "l4cardno:","amount:"+amount,"pin:"+pin,
//                        "servicetype:"+ Service.EXCHANGE_SERVICE_TYPE.GetCode()};
//
//                String json = HandlerJsonData.ExchangeToJsonString(arr);
//                new SubmitExchangeMoneyOrder().execute(ServerAPI.SUBMIT_TRANSACTION.GetUrl(), json);
//            } catch (JSONException e){
//
//            }
//        }
//
//        @Override
//        public void ShowError(int errorCode, String message) {
//            GetStausOrder(ServerAPI.GET_STATUS_EXCHANGE_MONEY_ORDER);
//        }
//    }

//    class SubmitExchangeMoneyOrder extends RequestServerAPI implements RequestServerFunction{
//        public SubmitExchangeMoneyOrder(){
//            SetRequestServerFunction(this);
//        }
//
//        @Override
//        public boolean CheckReturnCode(int code) {
//            if(code == ErrorCode.SUCCESS.GetValue()){
//                return true;
//            }
//
//            return false;
//        }
//
//        @Override
//        public void DataHandle(JSONObject jsonData) throws JSONException {
//            int bankreturncode = jsonData.getInt("bankreturncode");
////            long transactionid = jsonData.getLong("transactionid");
//            try {
//                String[] arr = new String[]{"userid:"+userid,"orderid:"+orderid,"password:"+pin};
//
//                String json = HandlerJsonData.ExchangeToJsonString(arr);
//                new GetStatusExchangeMoneyOrder().execute(ServerAPI.GET_STATUS_EXCHANGE_MONEY_ORDER.GetUrl(), json);
//            } catch (JSONException e){
//
//            }
//        }
//
//        @Override
//        public void ShowError(int errorCode, String message) {
//
//        }
//    }

//    class GetStatusExchangeMoneyOrder extends RequestServerAPI implements RequestServerFunction{
//
//        public GetStatusExchangeMoneyOrder(){
//            SetRequestServerFunction(this);
//        }
//
//        @Override
//        public boolean CheckReturnCode(int code) {
//            if(code == ErrorCode.SUCCESS.GetValue()){
//                return true;
//            }
//            else if(code > ErrorCode.SUCCESS.GetValue()){
//                return false;
//            }
//
//            return false;
//        }
//
//        @Override
//        public void DataHandle(JSONObject jsonData) throws JSONException {
//            Log.d("TAG", "GetStatusTopupOrder DataHandle: success" );
//        }
//
//        @Override
//        public void ShowError(int errorCode, String message) {
//
//        }
//    }
}
