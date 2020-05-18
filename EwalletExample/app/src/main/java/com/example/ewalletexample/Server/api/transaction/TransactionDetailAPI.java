package com.example.ewalletexample.Server.api.transaction;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

public class TransactionDetailAPI {
    private String userid, transactionId;
    private TransactionDetailResponse response;

    public TransactionDetailAPI(String userid, String transactionId, TransactionDetailResponse response){
        this.userid = userid;
        this.transactionId = transactionId;
        this.response = response;
    }

    public void StartRequest(){
        try {
            String[] arr = new String[]{"userid:"+this.userid,"transactionid:"+Long.valueOf(transactionId)};

            String json = HandlerJsonData.ExchangeToJsonString(arr);
            new GetTransactionDetailAPI().execute(ServerAPI.TRANSACTION_DETAIL.GetUrl(), json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class GetTransactionDetailAPI extends RequestServerAPI implements RequestServerFunction {
        public GetTransactionDetailAPI(){
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
            response.TransactionResponse(jsonData.getJSONObject("data").toString());
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }
}
