package com.example.ewalletexample.Server.api.transaction;

import com.example.ewalletexample.Server.api.bank.BankMappingCallback;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.data.TransactionDetail;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistory {
    BankMappingCallback response;
    private String userid;
    private long startTime;
    private int pageSize;

    public TransactionHistory(BankMappingCallback response, String userid, long startTime, int pageSize){
        this.response = response;
        this.userid = userid;
        this.startTime = startTime;
        this.pageSize = pageSize;
    }

    public void SetPageSize(int pageSize){
        this.pageSize = pageSize;
    }

    public void SetStartTime(long time){
        this.startTime = time;
    }

    public void StartGetHistoryTransaction() {
        try {
            String[] arr = new String[]{"userid:"+userid,"starttime:"+startTime,"pagesize:"+ pageSize};
            String json = HandlerJsonData.ExchangeToJsonString(arr);

            new TransactionHistoryAPI().execute(ServerAPI.HISTORY_TRANSACTION.GetUrl(), json);
        } catch (JSONException e){
            response.MappingResponse(false, null);
        }
    }

    class TransactionHistoryAPI extends RequestServerAPI implements RequestServerFunction {
        public TransactionHistoryAPI(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if (code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }

            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            List<TransactionDetail> transactionDetails = new ArrayList<>();

            JSONArray transactionArr = jsonData.getJSONArray("histories");
            for (int i = 0; i < transactionArr.length(); i++) {
                JSONObject transaction = transactionArr.getJSONObject(i);
                TransactionDetail detail = new TransactionDetail();
                detail.setTransactionid(transaction.getLong("transactionid"));
                detail.setAmount(transaction.getLong("amount"));
                detail.setOrderid(transaction.getLong("orderid"));
                detail.setServicetype(transaction.getInt("servicetype"));
                detail.setSourceoffund(transaction.getInt("sourceoffund"));
                detail.setChargetime(transaction.getString("chargetime"));
                transactionDetails.add(detail);
            }

            response.MappingResponse(true, transactionDetails);
        }

        @Override
        public void ShowError(int errorCode, String message) {
            response.MappingResponse(false, null);
        }
    }
}
