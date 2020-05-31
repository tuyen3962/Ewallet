package com.example.ewalletexample;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ewalletexample.Server.api.VerifyPin.VerifyPinAPI;
import com.example.ewalletexample.Server.api.VerifyPin.VerifyResponse;
import com.example.ewalletexample.Server.api.update.UpdateUserAPI;
import com.example.ewalletexample.Server.api.update.UpdateUserResponse;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.google.android.material.textview.MaterialTextView;

import javax.crypto.SecretKey;

public class ChangeNewPasswordActivity extends AppCompatActivity implements VerifyResponse, UpdateUserResponse {

    MaterialTextView tvToolbarTitle, tvErrorCurrentPassword, tvErrorNewPassword;
    ImageButton imgBtnBack;
    EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    Button btnUpdatePassword;
    String userid;
    VerifyPinAPI verifyPinAPI;
    Toolbar toolbar;
    UpdateUserAPI updateUserAPI;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (etConfirmPassword.length() == 6 && etCurrentPassword.length() == 6 && etNewPassword.length() == 6 && !btnUpdatePassword.isEnabled()){
                btnUpdatePassword.setEnabled(true);
            } else if(btnUpdatePassword.isEnabled()){
                btnUpdatePassword.setEnabled(false);
            }

            if (tvErrorCurrentPassword.getVisibility() == View.VISIBLE){
                tvErrorCurrentPassword.setVisibility(View.GONE);
            }

            if (tvErrorNewPassword.getVisibility() == View.VISIBLE) {
                tvErrorNewPassword.setVisibility(View.GONE);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_new_password);

        Initialize();
        GetValueFromIntent();
    }

    void Initialize(){
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        toolbar = findViewById(R.id.toolbarLayout);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        etNewPassword.addTextChangedListener(textWatcher);
        etConfirmPassword.addTextChangedListener(textWatcher);
        etCurrentPassword.addTextChangedListener(textWatcher);
        btnUpdatePassword.setEnabled(false);
        tvToolbarTitle.setText(getString(R.string.Change_password));
        imgBtnBack = findViewById(R.id.btnBackToPreviousActivity);
        tvErrorCurrentPassword = findViewById(R.id.tvErrorCurrentPass);
        tvErrorNewPassword = findViewById(R.id.tvErrorNewPassword);
        setSupportActionBar(toolbar);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        verifyPinAPI = new VerifyPinAPI(userid, this);
        updateUserAPI = new UpdateUserAPI(userid, getString(R.string.public_key), this);
    }

    public void ChangePasswordEvent(View view){
        String currentPin = etCurrentPassword.getText().toString();
        verifyPinAPI.SetPin(currentPin);
        verifyPinAPI.StartVerify();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void VerifyPinResponse(boolean isSuccess) {
        if(isSuccess){
            if (CheckNewPassword()){
                String password = etNewPassword.getText().toString();
                SecretKey secretKey1 = SecurityUtils.generateAESKey();
                SecretKey secretKey2 = SecurityUtils.generateAESKey();

                String encryptPasswordByAES = SecurityUtils.EncryptStringBySecretKey(secretKey1, getString(R.string.share_key), password);
                String encryptAESPassBySecondSecretKey = SecurityUtils.encryptAES(secretKey2, encryptPasswordByAES);

                updateUserAPI.setPin(encryptAESPassBySecondSecretKey);
                updateUserAPI.UpdateUser(secretKey1, secretKey2);
            }
        } else {
            ClearEditText();
            tvErrorCurrentPassword.setError("**Mật khẩu không đúng");
        }
    }

    boolean CheckNewPassword(){
        String newPassword = etNewPassword.getText().toString();
        String confirmPass = etConfirmPassword.getText().toString();

        if (newPassword.equalsIgnoreCase(confirmPass)){
            return true;
        } else {
            ClearEditText();
            tvErrorNewPassword.setError("**Mật khẩu không trùng");
            return false;
        }
    }

    void ClearEditText(){
        etNewPassword.setText("");
        etConfirmPassword.setText("");
        etCurrentPassword.setText("");
    }

    @Override
    public void UpdateSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void UpdateFail() {
        Toast.makeText(this, "Máy chủ đang bận. Xin hãy thử lại", Toast.LENGTH_SHORT).show();
    }
}
