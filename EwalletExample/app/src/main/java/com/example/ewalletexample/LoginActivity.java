package com.example.ewalletexample;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.example.ewalletexample.Server.api.balance.BalanceResponse;
import com.example.ewalletexample.Server.api.balance.GetBalanceAPI;
import com.example.ewalletexample.Server.api.login.UserLoginAPI;
import com.example.ewalletexample.Server.api.login.UserLoginRequest;
import com.example.ewalletexample.Server.api.login.UserLoginResponse;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.data.UserNotifyEntity;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.Response;
import com.example.ewalletexample.service.CheckInputField;
import com.example.ewalletexample.service.MemoryPreference.SharedPreferenceLocal;
import com.example.ewalletexample.service.notification.NotificationCreator;
import com.example.ewalletexample.service.websocket.WebsocketClient;
import com.example.ewalletexample.service.websocket.WebsocketResponse;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.security.PublicKey;

import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity implements BalanceResponse, UserLoginResponse, WebsocketResponse {
    FirebaseAuth mAuth;
    MaterialTextView tvFullname, tvPhone;
    TextView tvError;
    TextInputLayout inputPassword;
    TextInputEditText etPassword;
    User user;
    ProgressBarManager progressBarManager;
    SharedPreferenceLocal local;
    GetBalanceAPI balanceAPI;
    UserLoginAPI userLoginAPI;
    Gson gson;
    SecretKey secretKey1, secretKey2;
    String secretKeyString1, secretKeyString2;
    WebsocketClient websocketClient;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ui);

        InitLayoutProperties();
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
        etPassword = findViewById(R.id.etPassword);
        inputPassword = findViewById(R.id.input_layout_password);
        tvError = findViewById(R.id.tvError);
        user = new User();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void GetValueFromIntent(){
        Intent intent = getIntent();
        String update_symbol = intent.getStringExtra(Symbol.UPDATE_SYMBOL.GetValue());
        if (update_symbol.equalsIgnoreCase(Symbol.UPDATE_FOR_REGISTER.GetValue())){
            user = gson.fromJson(intent.getStringExtra(Symbol.USER.GetValue()), User.class);
            secretKeyString1 = intent.getStringExtra(Symbol.SECRET_KEY_01.GetValue());
            secretKey1 = SecurityUtils.generateAESKeyFromText(secretKeyString1);
            secretKeyString2 = intent.getStringExtra(Symbol.SECRET_KEY_02.GetValue());
            secretKey2 = SecurityUtils.generateAESKeyFromText(secretKeyString2);

            tvFullname.setText(user.getFullName());
            tvPhone.setText(user.getPhoneNumber());

            Intent data = new Intent(LoginActivity.this, UpdateUserInformationActivity.class);
            data.putExtra(Symbol.UPDATE_SYMBOL.GetValue(), update_symbol);
            data.putExtra(Symbol.USER.GetValue(), gson.toJson(user));
            data.putExtra(Symbol.SECRET_KEY_01.GetValue(), secretKeyString1);
            data.putExtra(Symbol.SECRET_KEY_02.GetValue(), secretKeyString2);
            startActivityForResult(data, RequestCode.UPDATE_REGISTER);
        } else {
            user.setUserId(intent.getStringExtra(Symbol.USER_ID.GetValue()));
            user.setPhoneNumber(intent.getStringExtra(Symbol.PHONE.GetValue()));
            user.setFullName(intent.getStringExtra(Symbol.FULLNAME.GetValue()));
        }

        websocketClient = new WebsocketClient(this, user.getUserId(), this);

        tvFullname.setText(user.getFullName());
        tvPhone.setText(user.getPhoneNumber());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void UserLoginEvent(View view){
        progressBarManager.ShowProgressBar("Loading");

        String password = etPassword.getText().toString();

        Response response = CheckUsernameAndPassword(password);

        if(response.GetStatus()){
            SendLoginRequest(password);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SendLoginRequest(String password){
        secretKey1 = SecurityUtils.generateAESKey();
        secretKey2 = SecurityUtils.generateAESKey();
        UserLoginRequest request = new UserLoginRequest(user.getPhoneNumber(), password,
                SecurityUtils.EncodeStringBase64(secretKey1.getEncoded()), SecurityUtils.EncodeStringBase64(secretKey2.getEncoded()));
        userLoginAPI = new UserLoginAPI(getString(R.string.share_key), getString(R.string.public_key), request, this);
        userLoginAPI.StartLoginAPI();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void LoginSucess(User user, String customToken, String secretKey1, String secretKey2) {
        this.user = user;
        this.secretKeyString1 = SecurityUtils.DecryptAESbyTwoSecretKey(this.secretKey2, this.secretKey1, secretKey1);
        this.secretKeyString2 = SecurityUtils.DecryptAESbyTwoSecretKey(this.secretKey2, this.secretKey1, secretKey2);
        if (mAuth.getCurrentUser() != null){
            mAuth.signOut();
        }

        mAuth.signInWithCustomToken(customToken);

        PublicKey publicKey = SecurityUtils.generatePublicKey(getString(R.string.public_key));
        balanceAPI = new GetBalanceAPI(publicKey, user.getUserId(), gson, this);
        balanceAPI.GetBalance(secretKeyString1, secretKeyString2);
    }

    @Override
    public void LoginFail(int code) {
        ShowErrorText("Mật khẩu không đúng");
        progressBarManager.HideProgressBar();
    }

    @Override
    public void GetBalanceResponse(long balance) {
        Intent intent = new Intent(LoginActivity.this, MainLayoutActivity.class);
        intent.putExtra(Symbol.USER.GetValue(), gson.toJson(user));
        intent.putExtra(Symbol.SECRET_KEY_01.GetValue(), secretKeyString1);
        intent.putExtra(Symbol.SECRET_KEY_02.GetValue(), secretKeyString2);
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        startActivityForResult(intent, RequestCode.LOGIN_CODE);
    }

    public void UserForgetPasswordEvent(View view){
        Intent intent = new Intent(LoginActivity.this, VerifyByPhoneActivity.class);
        intent.putExtra(Symbol.PHONE.GetValue(), user.getPhoneNumber());
        intent.putExtra(Symbol.FULLNAME.GetValue(), user.getFullName());
        intent.putExtra(Symbol.REASION_VERIFY.GetValue(), Symbol.REASON_VERIFY_FOR_FORGET.GetValue());
        intent.putExtra(Symbol.VERRIFY_FORGET.GetValue(), Symbol.VERIFY_FORGET_BY_PHONE.GetValue());
        startActivityForResult(intent, RequestCode.RESET_PASSWORD);
    }

    public void UserLogoutPhoneEvent(View view){
        local.WriteValueByKey(Symbol.KEY_PHONE.GetValue(), "");
        local.WriteValueByKey(Symbol.KEY_FULL_NAME.GetValue(), "");
        startActivity(new Intent(LoginActivity.this, EnterPhoneStartAppActivity.class));
    }

    private void ShowErrorText(String message){
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.LOGIN_CODE){
            progressBarManager.HideProgressBar();
            etPassword.setText("");
        } else if (requestCode == RequestCode.RESET_PASSWORD) {
            if (resultCode == RESULT_CANCELED){
                etPassword.setText("");
            } else {
                String userDetail = data.getStringExtra(Symbol.USER.GetValue());
                String secretKey1 = data.getStringExtra(Symbol.SECRET_KEY_01.GetValue());
                String secretKey2 = data.getStringExtra(Symbol.SECRET_KEY_02.GetValue());
                long balance = data.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
                SwitchToMainLayout(userDetail, secretKey1, secretKey2, balance);
            }
        } else if (requestCode == RequestCode.UPDATE_REGISTER){
            String userDetail = data.getStringExtra(Symbol.USER.GetValue());
            SwitchToMainLayout(userDetail, secretKeyString1, secretKeyString2, data.getLongExtra(Symbol.AMOUNT.GetValue(), 0));
        }
    }

    private void SwitchToMainLayout(String userDetail, String secret1, String secretKey2, long amount){
        Intent intent = new Intent(LoginActivity.this, MainLayoutActivity.class);
        intent.putExtra(Symbol.USER.GetValue(), userDetail);
        intent.putExtra(Symbol.SECRET_KEY_01.GetValue(), secret1);
        intent.putExtra(Symbol.SECRET_KEY_02.GetValue(), secretKey2);
        intent.putExtra(Symbol.AMOUNT.GetValue(), amount);
        startActivityForResult(intent, RequestCode.LOGIN_CODE);
    }

    @Override
    public void UpdateWallet(String userid, long balance) {

    }
}
