package com.example.ewalletexample.Server.bank.list;

import android.view.View;

import com.example.ewalletexample.Server.bank.BankMappingCallback;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.UserBankCardActivity;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListBankConnectedAPI {
    private String userid;
    private List<BankInfo> bankInfoList;
    private BankMappingCallback response;

    public ListBankConnectedAPI(BankMappingCallback response, String userid){
        this.response = response;
        this.userid = userid;
    }

    public void GetListBank(){
        try {
            String json = HandlerJsonData.ExchangeToJsonString(new String[]{"userid:"+ userid});
            new GetListBankConnected().execute(ServerAPI.GET_BANK_LINKING_API.GetUrl(), json);
        } catch (JSONException e) {
            e.printStackTrace();
            response.MappingResponse(false, null);
        }
    }

    class GetListBankConnected extends RequestServerAPI implements RequestServerFunction {
        public GetListBankConnected(){
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
            bankInfoList = new ArrayList<>();
            JSONArray cardArray = jsonData.getJSONArray("cards");
            if(cardArray.length() == 0){
                response.MappingResponse(false, null);
                return;
            }

            for(int i = 0; i < cardArray.length(); i++){
                JSONObject card = cardArray.getJSONObject(i);
                String cardName = card.getString("cardname");
                String bankCode = card.getString("bankcode");
                String f6CardNo = card.getString("f6cardno");
                String l4CardNo = card.getString("l4cardno");
                BankInfo bankInfo = new BankInfo(cardName, bankCode, f6CardNo, l4CardNo);
                bankInfoList.add(bankInfo);
            }

            response.MappingResponse(true, bankInfoList);
        }

        @Override
        public void ShowError(int errorCode, String message) {
            response.MappingResponse(false, null);
        }
    }

}
