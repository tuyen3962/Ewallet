package com.example.ewalletexample.Server.api.order;

import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

public class WithdrawOrder extends Order{
    private BankInfo bankInfo;

    public WithdrawOrder(String userid, String pin, String amount, long fee, SourceFund codeSourceFund, BankInfo bankInfoJson, OrderResponse response)  {
        super(userid, pin, amount, fee, codeSourceFund, Service.WITHDRAW_SERVICE_TYPE, response);
        this.bankInfo = bankInfoJson;
    }

    @Override
    protected void VerifyPinSuccess() throws JSONException {
        String[] arr = new String[]{"userid:"+userid,"f6cardno:"+bankInfo.getF6CardNo(),"l4cardno:"+bankInfo.getL4CardNo(),
                "bankcode:"+bankInfo.getBankCode(),"amount:"+Long.valueOf(amount)};

        String json = HandlerJsonData.ExchangeToJsonString(arr);
        CreateOrder(ServerAPI.CREATE_WITHDRAW_ORDER, json);
    }

    @Override
    protected void CreateOrderSuccess() throws JSONException {
        String[] arr = new String[]{"userid:"+userid,"orderid:"+orderid,"sourceoffund:"+sourceFund.GetCode(),
                "bankcode:"+bankInfo.getBankCode(),"f6cardno:"+bankInfo.getF6CardNo(),
                "l4cardno:"+bankInfo.getL4CardNo(),"amount:"+amount,"pin:"+pin,
                "servicetype:"+ Service.WITHDRAW_SERVICE_TYPE.GetCode()};

        String json = HandlerJsonData.ExchangeToJsonString(arr);
        SubmitOrder(json);
    }

    @Override
    protected void SubmitOrderSuccess() {
        GetStausOrder(ServerAPI.GET_STATUS_WITHDRAW_ORDER);
    }

    @Override
    protected void GetDataFromJsonFromStatusOrder(JSONObject json) {

    }

//    class CreateWithdrawOrder extends RequestServerAPI implements RequestServerFunction {
//        public CreateWithdrawOrder(){
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
//                String[] arr = new String[]{"userid:"+userid,"orderid:"+orderid,"sourceoffund:"+codeSourceFund,
//                        "bankcode:"+bankInfo.getBankCode(),"f6cardno:"+bankInfo.getF6CardNo(),
//                        "l4cardno:"+bankInfo.getL4CardNo(),"amount:"+amount,"pin:"+pin,
//                        "servicetype:"+ Service.WITHDRAW_SERVICE_TYPE.GetCode()};
//
//                String json = HandlerJsonData.ExchangeToJsonString(arr);
//                Log.d("TAG", "DataHandle: " + json);
//                new SubmitWithdrawOrder().execute(ServerAPI.SUBMIT_TRANSACTION.GetUrl(), json);
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

//    class SubmitWithdrawOrder extends RequestServerAPI implements RequestServerFunction{
//        public SubmitWithdrawOrder(){
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
//                new GetStatusWithdrawOrder().execute(ServerAPI.GET_STATUS_WITHDRAW_ORDER.GetUrl(), json);
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

//    class GetStatusWithdrawOrder extends RequestServerAPI implements RequestServerFunction{
//
//        public GetStatusWithdrawOrder(){
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
//
//        }
//
//        @Override
//        public void ShowError(int errorCode, String message) {
//
//        }
//    }
}
