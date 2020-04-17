package com.example.ewalletexample.Server.register;

import android.content.Intent;
import android.util.Log;

import com.example.ewalletexample.MainActivity;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.VerifyByPhoneActivity;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterUserAPI {
    private ResgisterCallback callback;
    private String fullName;
    private String phone;
    private String pin;

    public RegisterUserAPI(String fullName, String phone, String pin, ResgisterCallback callback) {
        this.fullName = fullName;
        this.phone = phone;
        this.pin = pin;
        this.callback = callback;
    }

    public void StartRegister(){
        JSONObject postData = new JSONObject();

        try {
            postData.put("fullname",fullName);
            postData.put("pin" , pin);
            postData.put("phone", phone);

            new Register().execute(ServerAPI.REGISTER_API.GetUrl(), postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class Register extends RequestServerAPI implements RequestServerFunction {
        public Register(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if (code == ErrorCode.SUCCESS.GetValue()){
                return  true;
            }

            ShowError(code, "");
            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            String userid = jsonData.getString("userid");

            callback.RegisterSuccessful(userid, fullName, phone);
        }

        @Override
        public void ShowError(int errorCode, String message) {
            Log.d("ERROR", message);
        }
    }

}
