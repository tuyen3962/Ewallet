package com.example.ewalletexample.Server.api.login;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.service.ServerAPI;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class UserLoginAPI {
    private UserLoginRequest request;
    private UserLoginResponse response;
    Gson gson;

    public UserLoginAPI(UserLoginRequest request, UserLoginResponse response){
        this.request = request;
        this.response = response;
        gson = new Gson();
    }

    public void StartLoginAPI(){
        String loginRequest = gson.toJson(request);

        new LoginThread().execute(ServerAPI.LOGIN_API.GetUrl(), loginRequest);
    }

    private class LoginThread extends RequestServerAPI implements RequestServerFunction {
        public LoginThread() {
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if (code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }
            else {
                ShowError(code, "Fail");
                return false;
            }
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            User user = new User();
            user.setUserId(jsonData.getString("userid"));
            user.setFullName(jsonData.getString("fullname"));
            user.setAddress(jsonData.getString("address"));
            user.setPhoneNumber(jsonData.getString("phone"));
            user.setDateOfbirth(jsonData.getString("dob"));
            user.setCmnd(jsonData.getString("cmnd"));
            user.setEmail(jsonData.getString("email"));
            user.setCmndFrontImage(jsonData.getString("cmndFontImg"));
            user.setAvatar(jsonData.getString("avatar"));
            user.setCmndBackImage(jsonData.getString("cmndBackImg"));
            user.setStatus(jsonData.getInt("verify"));

            response.LoginSucess(user);
        }

        @Override
        public void ShowError(int errorCode, String message) {
            response.LoginFail(errorCode);
        }
    }
}
