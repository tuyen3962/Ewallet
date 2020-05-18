package com.example.ewalletexample.Server.api.bank.unlink;

import android.util.Log;

import com.example.ewalletexample.Server.api.bank.BankMappingCallback;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

public class UnlinkBankConnectedAPI {
    private BankMappingCallback response;
    private String userid;
    private BankInfo bankInfo;

    public UnlinkBankConnectedAPI(BankMappingCallback response, String userid, BankInfo info){
        this.response = response;
        this.userid = userid;
        this.bankInfo = info;
    }

    public void StartUnlink(){
        try {
            String[] arr = new String[]{"userid:"+ userid, "bankcode:"+ bankInfo.getBankCode(),
                    "f6cardno:"+bankInfo.getF6CardNo(), "l4cardno:" + bankInfo.getL4CardNo()};
            String json = HandlerJsonData.ExchangeToJsonString(arr);
            new UnlinkBankAPI().execute(ServerAPI.UNLINK_BANK_CARD_API.GetUrl(), json);
        } catch (JSONException e) {
            response.MappingResponse(false, null);
            e.printStackTrace();
        }

    }

    class UnlinkBankAPI extends RequestServerAPI implements RequestServerFunction {
        public UnlinkBankAPI(){
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
            if(bankreturncode == 1){
                response.MappingResponse(true, null);
            }
            else {
                response.MappingResponse(false, null);
            }
        }

        @Override
        public void ShowError(int errorCode, String message) {
            response.MappingResponse(false, null);
        }
    }

}
