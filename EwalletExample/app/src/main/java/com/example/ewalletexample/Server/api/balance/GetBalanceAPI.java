package com.example.ewalletexample.Server.api.balance;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

public class GetBalanceAPI {
    private String userid;
    private BalanceResponse balanceResponse;

    public GetBalanceAPI(String userid, BalanceResponse response){
        this.userid = userid;
        balanceResponse = response;
    }

    public void GetBalance()  {
        try {
            String[] arr = new String[]{"userid:" + userid};

            String json = HandlerJsonData.ExchangeToJsonString(arr);
            new GetBalance().execute(ServerAPI.GET_BALANCE_API.GetUrl(), json);
        } catch (JSONException e) {

        }
    }

    class GetBalance extends RequestServerAPI implements RequestServerFunction{
        public GetBalance(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if(code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }

            ShowError(code, ErrorCode.VALIDATE_USER_ID_INVALID.GetMessage());
            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            long amount = jsonData.getLong("amount");
            balanceResponse.GetBalanceResponse(amount);
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }
}
