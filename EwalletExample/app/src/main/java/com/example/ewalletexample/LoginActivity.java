package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.Response;
import com.example.ewalletexample.service.CheckInputField;
import com.example.ewalletexample.service.ServerAPI;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText etUsername, etPassword;
    TextView tvError;

    ProgressBarManager progressBarManager;
    private String userid;
    private long userAmount;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(tvError.getVisibility() == View.VISIBLE){
                tvError.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ui);

        InitLayoutProperties();
    }

    void InitLayoutProperties(){
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), findViewById(R.id.btnLogin));

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        etUsername.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
        tvError = findViewById(R.id.tvError);
    }

    public void UserLoginEvent(View view){
        progressBarManager.ShowProgressBar("Loading");

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        Response response = CheckUsernameAndPassword(username,password);

        if(response.GetStatus()){
            SendLoginRequest(username,password);
        }
        else{
            progressBarManager.HideProgressBar();
            ShowErrorText(response.GetMessage());
        }
    }

    Response CheckUsernameAndPassword(String username, String password){
        if(TextUtils.isEmpty(username)){
            return new Response(ErrorCode.VALIDATE_PHONE_INVALID);
        }
        else if(!CheckInputField.PhoneNumberIsValid(username)){
            return new Response(ErrorCode.VALIDATE_PHONE_INVALID);
        }
        else if(TextUtils.isEmpty(password)){
            return new Response(ErrorCode.VALIDATE_PIN_INVALID);
        }
        else if(!CheckInputField.PasswordIsValid(password)){
            return new Response(ErrorCode.VALIDATE_PIN_INVALID);
        }

        return new Response(ErrorCode.SUCCESS);
    }

    private void SendLoginRequest(String username, String password){
        JSONObject json = new JSONObject();

        try{
            json.put("phone",username);
            json.put("pin",password);

            new LoginThread().execute(ServerAPI.LOGIN_API.GetUrl(), json.toString());
        }catch (Exception e){
            progressBarManager.HideProgressBar();
            Log.d("TAG", "CheckUsernameAndPassword: "  + e.getMessage());
        }
    }

    public void UserForgetPasswordEvent(View view){
        startActivity(new Intent(LoginActivity.this, VerifyUserForForget.class));
    }

    public void UserRegisterEvent(View view){
        startActivity(new Intent(LoginActivity.this, RegisterByPhone.class));
    }

    private void ShowErrorText(String message){
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }

    private void SwitchToMainScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(Symbol.AMOUNT.GetValue(), userAmount);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        startActivity(intent);
    }

    private class LoginThread extends RequestServerAPI implements RequestServerFunction {
        public LoginThread() {
            super();
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if (code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }
            else {
                progressBarManager.HideProgressBar();
                ShowError(code, "Fail");
                return false;
            }
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            userid = jsonData.getString("userid");

            JSONObject json = new JSONObject();
            json.put("userid", userid);

            new GetBalanceInMain().execute(ServerAPI.GET_BALANCE_API.GetUrl(), json.toString());
        }

        @Override
        public void ShowError(int errorCode, String message) {
            ShowErrorText("Số điện thoại hoặc mật khẩu sai");
            if(errorCode == ErrorCode.USER_PASSWORD_WRONG.GetValue()){
                ShowErrorText("Sai mật khẩu");
            }
        }
    }

    class GetBalanceInMain extends RequestServerAPI implements RequestServerFunction {
        public GetBalanceInMain() {
            super();
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if(code == ErrorCode.SUCCESS.GetValue() || code == ErrorCode.VALIDATE_USER_ID_INVALID.GetValue()){
                return true;
            }
            else{
                ShowError(0, "Fail");
                return false;
            }
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            userAmount = jsonData.getLong("amount");
            SwitchToMainScreen();
        }

        @Override
        public void ShowError(int errorCode, String message) {
            Log.d("ERROR", message);
        }
    }
}
