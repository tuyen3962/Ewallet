package com.example.ewalletexample.Server.api.VerifyPin;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyPinAPI {
    private String userid, pin;
    private VerifyResponse response;

    public VerifyPinAPI(String userid, String pin, VerifyResponse response){
        this.userid = userid;
        this.pin = pin;
        this.response = response;
    }

    public VerifyPinAPI(String userid, VerifyResponse response){
        this.userid = userid;
        this.response = response;
    }

    public void SetPin(String pin){
        this.pin = pin;
    }

    public void StartVerify(){
        try {
            String[] arr = new String[]{"userid:"+userid,"pin:"+pin};
            String json = HandlerJsonData.ExchangeToJsonString(arr);
            new RequestVerifyPin().execute(ServerAPI.VERIFY_PIN_API.GetUrl(), json);
        }catch (JSONException e){
            return;
        }
    }

    class RequestVerifyPin extends RequestServerAPI implements RequestServerFunction {
        public RequestVerifyPin(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if(code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }

            ShowError(0, "Không đúng mã pin");
            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            response.VerifyPinResponse(true);
        }

        @Override
        public void ShowError(int errorCode, String message) {
            response.VerifyPinResponse(false);
        }
    }

}
