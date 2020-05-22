package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ewalletexample.Server.api.balance.BalanceResponse;
import com.example.ewalletexample.Server.api.balance.GetBalanceAPI;
import com.example.ewalletexample.Server.api.login.UserLoginAPI;
import com.example.ewalletexample.Server.api.login.UserLoginRequest;
import com.example.ewalletexample.Server.api.login.UserLoginResponse;
import com.example.ewalletexample.Server.api.update.UpdateUserAPI;
import com.example.ewalletexample.Server.api.update.UpdateUserResponse;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.Response;
import com.example.ewalletexample.service.CheckInputField;
import com.example.ewalletexample.service.toolbar.CustomToolbarContext;
import com.example.ewalletexample.service.toolbar.ToolbarEvent;
import com.example.ewalletexample.utilies.Encryption;
import com.example.ewalletexample.utilies.Utilies;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import javax.crypto.SecretKey;

public class ResetPassword extends AppCompatActivity implements ToolbarEvent, UpdateUserResponse, UserLoginResponse, BalanceResponse {

    FirebaseAuth auth;
    CustomToolbarContext customToolbarContext;
    ProgressBarManager progressBarManager;
    TextInputEditText etPassword, etConfirmPassword;
    MaterialTextView tvUsername, tvError;
    UpdateUserAPI updateUserAPI;
    UserLoginAPI userLoginAPI;
    Gson gson;
    Button btnResetNewPassword;
    String reason, email, phone, userid, encryptPasswordByAES, encodeSecretKeyByPublicKey;
    User user;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (tvError.getVisibility() == View.VISIBLE){
                tvError.setVisibility(View.GONE);
            }

            if (etConfirmPassword.length() == 6 && etPassword.length() == 6){
                Utilies.SetEneableButton(btnResetNewPassword, true);
            } else {
                Utilies.SetEneableButton(btnResetNewPassword, false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        auth = FirebaseAuth.getInstance();
        Initialize();

        GetValueFromIntent();
    }

    void Initialize(){
        gson = new Gson();
        tvUsername = findViewById(R.id.tvUsername);
        btnResetNewPassword = findViewById(R.id.btnResetNewPassword);
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), btnResetNewPassword);
        etPassword = findViewById(R.id.etNewPassword);
        tvError = findViewById(R.id.tvError);
        etConfirmPassword = findViewById(R.id.etConfirmNewPasswork);
        customToolbarContext = new CustomToolbarContext(this, this::BackToPreviousActivity);
        customToolbarContext.SetTitle("Quên mật khẩu");
        etConfirmPassword.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        reason = intent.getStringExtra(Symbol.VERRIFY_FORGET.GetValue());
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        if(reason.equalsIgnoreCase(Symbol.VERIFY_FORGET_BY_EMAIL.GetValue())){
            email = intent.getStringExtra(Symbol.EMAIL.GetValue());
            LoadingVerifyEmail verifyEmail = new LoadingVerifyEmail(email);
            verifyEmail.start();
        }
        else
        {
            phone = intent.getStringExtra(Symbol.PHONE.GetValue());
            tvUsername.setText(phone);
        }

        updateUserAPI = new UpdateUserAPI(userid, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void VerifyResetPasswordEvent(View view){
        progressBarManager.ShowProgressBar("Loading...");

        String password = etPassword.getText().toString();
        String confirmPass = etConfirmPassword.getText().toString();

        Response response = CheckPassword(password, confirmPass);

        if(response.GetStatus()){
            SecretKey secretKey = Encryption.getSecretKey();
            encryptPasswordByAES = Encryption.EncryptStringBySecretKey(secretKey, getString(R.string.share_key), password);
            encodeSecretKeyByPublicKey = Encryption.EncryptSecretKeyByPublicKey(getString(R.string.public_key), secretKey);

            updateUserAPI.setPin(encryptPasswordByAES);
            updateUserAPI.setKey(encodeSecretKeyByPublicKey);
            updateUserAPI.UpdateUser();
        } else {
            progressBarManager.HideProgressBar();
        }
    }

    private Response CheckPassword(String pass, String confirmPass){
        if(TextUtils.isEmpty(pass)){
            etPassword.setError("Hãy nhập mật khẩu mới");
            return new Response(ErrorCode.EMPTY_PIN);
        }
        else if(TextUtils.isEmpty(confirmPass)){
            etConfirmPassword.setError("Hãy nhập lại mật khẩu mới");
            return new Response(ErrorCode.EMPTY_CONFIRM_PIN);
        }
        else if(!CheckInputField.PasswordIsValid(pass)){
            etPassword.setError("Mật khẩu không hợp lệ");
            return new Response(ErrorCode.VALIDATE_PIN_INVALID);
        }
        else if(!pass.equalsIgnoreCase(confirmPass)){
            etConfirmPassword.setError("Mật khẩu không trùng");
            return new Response(ErrorCode.UNVALID_PIN_AND_CONFIRM_PIN);
        }

        return new Response(ErrorCode.SUCCESS);
    }

    @Override
    public void BackToPreviousActivity() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void UpdateSuccess() {
        UserLoginRequest request = new UserLoginRequest(phone, encryptPasswordByAES, encodeSecretKeyByPublicKey);
        userLoginAPI = new UserLoginAPI(request, this);
        userLoginAPI.StartLoginAPI();
    }

    @Override
    public void UpdateFail() {
        ShowErrorText("Máy chủ không phản hồi");
        progressBarManager.HideProgressBar();
    }

    @Override
    public void LoginSucess(User user) {
        this.user = user;
        GetBalanceAPI getBalanceAPI = new GetBalanceAPI(user.getUserId(), this);
        getBalanceAPI.GetBalance();
    }

    @Override
    public void LoginFail(int code) {
        progressBarManager.HideProgressBar();
    }

    void ShowErrorText(String mess){
        tvError.setText(mess);
        tvError.setVisibility(View.VISIBLE);
    }

    @Override
    public void GetBalanceResponse(long balance) {
        Intent intent = new Intent(ResetPassword.this, MainLayoutActivity.class);
        intent.putExtra(Symbol.USER.GetValue(), gson.toJson(user));
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        startActivity(intent);
    }

    class LoadingVerifyEmail extends Thread{
        private String email;

        public LoadingVerifyEmail(String _email){
            email = _email;
        }

        @Override
        public void run() {
            Toast.makeText(ResetPassword.this, "Loading" ,Toast.LENGTH_SHORT).show();
            while (true){
                try {
                    Thread.sleep(10000);
                    auth.signInWithEmailAndPassword(email, "123456").addOnCompleteListener(ResetPassword.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
//                        isFinishVerifyEmail = true;
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
