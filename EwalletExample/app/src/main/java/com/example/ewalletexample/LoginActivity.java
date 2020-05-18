package com.example.ewalletexample;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.example.ewalletexample.Server.api.balance.BalanceResponse;
import com.example.ewalletexample.Server.api.balance.GetBalanceAPI;
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
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity implements BalanceResponse {
    FirebaseAuth mAuth;
    MaterialTextView tvFullname, tvPhone;
    TextView tvError;
    PasswordFieldFragment passwordFieldFragment;
    NumberKeyboard keyboard;
    User user;
    ProgressBarManager progressBarManager;
    SharedPreferenceLocal local;
    GetBalanceAPI balanceAPI;
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
            String encryptKey = "";

            SecretKey shareKey = Encryption.generateAESKeyFromText(getString(R.string.share_key));
            encryptKey = Encryption.encryptHmacSha256(shareKey, password);

            SecretKey secretKey = Encryption.generateAESKey();
            encryptKey = Encryption.encryptHmacSha256(secretKey, encryptKey);

            String secretKeyString = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
            String encryptPassword = Encryption.EncryptStringByPublicKey(getString(R.string.public_key), secretKeyString);

            SecretKey secretKey1 = Encryption.generateAESKeyFromText(secretKeyString);
            String decodeStringKey = 
//            SendLoginRequest(encryptPassword, encryptKey);
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
        JSONObject json = new JSONObject();

        try{
            json.put("phone",user.getPhoneNumber());
            json.put("key", secretKey);
            json.put("pin",password);

            new LoginThread().execute(ServerAPI.LOGIN_API.GetUrl(), json.toString());
        }catch (Exception e){
            progressBarManager.HideProgressBar();
            Log.d("TAG", "CheckUsernameAndPassword: "  + e.getMessage());
        }
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
        local.AddNewStringIntoSetString(Symbol.KEY_PHONES.GetValue(), user.getPhoneNumber());
        Intent intent = new Intent(LoginActivity.this, MainLayoutActivity.class);
        intent.putExtra(Symbol.USER.GetValue(), gson.toJson(user));
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        startActivity(intent);
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
                progressBarManager.HideProgressBar();
                ShowError(code, "Fail");
                return false;
            }
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            user = new User();
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
            GetUserBalance();
        }

        @Override
        public void ShowError(int errorCode, String message) {
            ShowErrorText("Số điện thoại hoặc mật khẩu sai");
            if(errorCode == ErrorCode.USER_PASSWORD_WRONG.GetValue()){
                ShowErrorText("Sai mật khẩu");
            }
        }
    }
}
