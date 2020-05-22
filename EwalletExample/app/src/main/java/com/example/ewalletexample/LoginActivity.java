package com.example.ewalletexample;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.example.ewalletexample.Server.api.balance.BalanceResponse;
import com.example.ewalletexample.Server.api.balance.GetBalanceAPI;
import com.example.ewalletexample.Server.api.login.UserLoginAPI;
import com.example.ewalletexample.Server.api.login.UserLoginRequest;
import com.example.ewalletexample.Server.api.login.UserLoginResponse;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.Response;
import com.example.ewalletexample.service.CheckInputField;
import com.example.ewalletexample.service.MemoryPreference.SharedPreferenceLocal;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.Encryption;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity implements BalanceResponse, UserLoginResponse {
    FirebaseAuth mAuth;
    MaterialTextView tvFullname, tvPhone;
    TextView tvError;
    PasswordFieldFragment passwordFieldFragment;
    NumberKeyboard keyboard;
    User user;
    ProgressBarManager progressBarManager;
    SharedPreferenceLocal local;
    GetBalanceAPI balanceAPI;
    UserLoginAPI userLoginAPI;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ui);

        InitLayoutProperties();
        if(savedInstanceState == null) {
            passwordFieldFragment = PasswordFieldFragment.newInstance("Nhập mật khẩu");
            getSupportFragmentManager().
                    beginTransaction().replace(R.id.passwordFrameLayout, passwordFieldFragment).commit();
        }
        GetValueFromIntent();
    }

    void InitLayoutProperties(){
        gson = new Gson();
        local = new SharedPreferenceLocal(this, Symbol.NAME_PREFERENCES.GetValue());
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            mAuth.signOut();
        }

        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar),
                findViewById(R.id.btnLogin), findViewById(R.id.btnLogout), findViewById(R.id.btnForgetPass));

        tvFullname = findViewById(R.id.tvFullName);
        tvPhone = findViewById(R.id.tvPhone);

        keyboard = findViewById(R.id.keyboard);
        SetDefault();
        tvError = findViewById(R.id.tvError);
        user = new User();

        keyboard.setListener(new NumberKeyboardListener() {
            @Override
            public void onNumberClicked(int i) {
                passwordFieldFragment.CheckIncreaseIndex(String.valueOf(i));
            }

            @Override
            public void onLeftAuxButtonClicked() {
                HideKeyBoard();
            }

            @Override
            public void onRightAuxButtonClicked() {
                passwordFieldFragment.CheckDecreaseIndex();
            }
        });
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        user.setPhoneNumber(intent.getStringExtra(Symbol.PHONE.GetValue()));
        user.setFullName(intent.getStringExtra(Symbol.FULLNAME.GetValue()));

        tvFullname.setText(user.getFullName());
        tvPhone.setText(user.getPhoneNumber());
    }

    public void ShowNumberKeyBoard(){
        tvError.setVisibility(View.GONE);
        keyboard.setVisibility(View.VISIBLE);
    }

    public void HideKeyBoard(){
        keyboard.setVisibility(View.GONE);
        passwordFieldFragment.DisablePasswordField();
    }

    void SetDefault(){
        keyboard.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void UserLoginEvent(View view){
        keyboard.setVisibility(View.GONE);
        progressBarManager.ShowProgressBar("Loading");

        String password = passwordFieldFragment.getTextByImage();

        Response response = CheckUsernameAndPassword(password);

        if(response.GetStatus()){
            SecretKey secretKey = Encryption.getSecretKey();
            String encryptPasswordByAES = Encryption.EncryptStringBySecretKey(secretKey, getString(R.string.share_key), password);
            String encodeSecretKeyByPublicKey = Encryption.EncryptSecretKeyByPublicKey(getString(R.string.public_key), secretKey);
            SendLoginRequest(encodeSecretKeyByPublicKey, encryptPasswordByAES);
        }
        else{
            progressBarManager.HideProgressBar();
            ShowErrorText(response.GetMessage());
        }
    }

    Response CheckUsernameAndPassword(String password){
        if(TextUtils.isEmpty(password)){
            return new Response(ErrorCode.VALIDATE_PIN_INVALID);
        }
        else if(!CheckInputField.PasswordIsValid(password)){
            return new Response(ErrorCode.VALIDATE_PIN_INVALID);
        }

        return new Response(ErrorCode.SUCCESS);
    }

    private void SendLoginRequest(String secretKey, String password){
        UserLoginRequest request = new UserLoginRequest(user.getPhoneNumber(), password, secretKey);
        userLoginAPI = new UserLoginAPI(request, this);
        userLoginAPI.StartLoginAPI();
    }

    public void UserForgetPasswordEvent(View view){
        keyboard.setVisibility(View.GONE);
        startActivity(new Intent(LoginActivity.this, VerifyUserForForget.class));
    }

    public void UserLogoutPhoneEvent(View view){
        keyboard.setVisibility(View.GONE);
        local.WriteValueByKey(Symbol.KEY_PHONE.GetValue(), "");
        local.WriteValueByKey(Symbol.KEY_FULL_NAME.GetValue(), "");
        startActivity(new Intent(LoginActivity.this, EnterPhoneStartAppActivity.class));
    }

    private void ShowErrorText(String message){
        keyboard.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }

    void GetUserBalance(){
        balanceAPI = new GetBalanceAPI(user.getUserId(), this);
        balanceAPI.GetBalance();
    }

    @Override
    public void GetBalanceResponse(long balance) {
        Intent intent = new Intent(LoginActivity.this, MainLayoutActivity.class);
        intent.putExtra(Symbol.USER.GetValue(), gson.toJson(user));
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        startActivity(intent);
    }

    @Override
    public void LoginSucess(User user) {
        this.user = user;
        GetUserBalance();
    }

    @Override
    public void LoginFail(int code) {
        ShowErrorText("Mật khẩu không đúng");
        progressBarManager.HideProgressBar();
    }
}
