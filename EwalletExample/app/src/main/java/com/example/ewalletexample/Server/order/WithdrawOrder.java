package com.example.ewalletexample.Server.order;

import android.util.Log;

import com.example.ewalletexample.Server.VerifyPin.VerifyPinAPI;
import com.example.ewalletexample.Server.VerifyPin.VerifyResponse;
import com.example.ewalletexample.Server.balance.BalanceResponse;
import com.example.ewalletexample.Server.balance.GetBalanceAPI;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.Code;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

public class WithdrawOrder implements VerifyResponse, StatusOrder{
    private String userid, amount, pin;
    private long orderid, fee;
    private short codeSourceFund;
    private BankInfo bankInfo;

    private VerifyPinAPI verifyPinAPI;
    private BalanceResponse response;
    private GetStatusWithdrawOrderThread statusWithdrawOrder;

    public WithdrawOrder(String userid, String pin, String amount, long fee, short codeSourceFund, String bankInfoJson, BalanceResponse response) throws JSONException {
        this.userid = userid;
        this.pin = pin;
        this.amount = amount;
        this.fee = fee;
        this.codeSourceFund = codeSourceFund;
        bankInfo = new BankInfo(bankInfoJson);
        verifyPinAPI = new VerifyPinAPI(userid, pin, this);
        this.response = response;
    }

    public void StartCreateWithdrawOrder(){
        verifyPinAPI.StartVerify();
    }

    @Override
    public void VerifyPinResponse(boolean isSuccess) {
        if(isSuccess){
            try {
                String[] arr = new String[]{"userid:"+userid,"f6cardno:"+bankInfo.getF6CardNo(),"l4cardno:"+bankInfo.getL4CardNo(),
                        "bankcode:"+bankInfo.getBankCode(),"amount:"+Long.valueOf(amount)};

                String json = HandlerJsonData.ExchangeToJsonString(arr);
                new CreateWithdrawOrder().execute(ServerAPI.CREATE_TOPUP_ORDER.GetUrl(), json);
            } catch (JSONException e){

            }
        }
    }

    private void StartGetStatusOrder(String jsonData){
        statusWithdrawOrder = new GetStatusWithdrawOrderThread(this, jsonData);
        statusWithdrawOrder.run();
    }

    @Override
    public void TransactionSuccess() {
        GetBalanceAPI balanceAPI = new GetBalanceAPI(userid, response);
        balanceAPI.GetBalance();
    }

    @Override
    public void TransactionExcuting() {
        statusWithdrawOrder.run();
    }

    @Override
    public void TransactionFail() {
        Log.d("FAILURE","TransactionFail");
    }

    class CreateWithdrawOrder extends RequestServerAPI implements RequestServerFunction {
        public CreateWithdrawOrder(){
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
                String[] arr = new String[]{"userid:"+userid,"orderid:"+orderid,"sourceoffund:"+codeSourceFund,
                        "bankcode:"+bankInfo.getBankCode(),"f6cardno:"+bankInfo.getF6CardNo(),
                        "l4cardno:"+bankInfo.getL4CardNo(),"amount:"+amount,"pin:"+pin,
                        "servicetype:"+ Code.TOPUP_SERVICE_TYPE.GetCode()};

                String json = HandlerJsonData.ExchangeToJsonString(arr);
                new SubmitWithdrawOrder().execute(ServerAPI.SUBMIT_TRANSACTION.GetUrl(), json);
            } catch (JSONException e){

            }
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }

    class SubmitWithdrawOrder extends RequestServerAPI implements RequestServerFunction{
        public SubmitWithdrawOrder(){
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

    class GetStatusWithdrawOrderThread extends Thread{
        StatusOrder statusTopupOrder;
        String jsonData;

        public GetStatusWithdrawOrderThread(StatusOrder getStatusTopupOrder, String json){
            jsonData = json;
            statusTopupOrder = getStatusTopupOrder;
        }

        @Override
        public void run(){
            new GetStatusWithdrawOrder(statusTopupOrder).execute(ServerAPI.GET_STATUS_TOPUP_ORDER.GetUrl(), jsonData);
        }
    }

    class GetStatusWithdrawOrder extends RequestServerAPI implements RequestServerFunction{
        private StatusOrder statusOrder;

        public GetStatusWithdrawOrder(StatusOrder statusOrder){
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

        }

        @Override
        public void ShowError(int errorCode, String message) {
            statusOrder.TransactionFail();
        }
    }
}
