package com.example.ewalletexample.Server.api.transaction;

import com.example.ewalletexample.Server.api.bank.BankMappingCallback;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.data.TransactionDetail;
import com.example.ewalletexample.data.TransactionHistory;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryAPI {
    BankMappingCallback response;
    private String userid;
    private long startTime;
    private int pageSize;
    private Gson gson;

    public TransactionHistoryAPI(BankMappingCallback response, String userid, long startTime, int pageSize){
        this.response = response;
        this.userid = userid;
        this.startTime = startTime;
        this.pageSize = pageSize;
        gson = new Gson();
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

            new GetTransactionHistoryAPI().execute(ServerAPI.HISTORY_TRANSACTION.GetUrl(), json);
        } catch (JSONException e){
            response.MappingResponse(false, null);
        }
    }

    class GetTransactionHistoryAPI extends RequestServerAPI implements RequestServerFunction {
        public GetTransactionHistoryAPI(){
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
            List<TransactionHistory> transactionDetails = new ArrayList<>();

            JSONArray transactionArr = jsonData.getJSONArray("histories");
            for (int i = 0; i < transactionArr.length(); i++) {
                JSONObject transaction = transactionArr.getJSONObject(i);
                transactionDetails.add(gson.fromJson(transaction.toString(), TransactionHistory.class));
            }

            response.MappingResponse(true, transactionDetails);
        }

        @Override
        public void ShowError(int errorCode, String message) {
            response.MappingResponse(false, null);
        }
    }
}
