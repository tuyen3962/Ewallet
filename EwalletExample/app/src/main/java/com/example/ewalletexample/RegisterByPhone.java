package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.model.Response;
import com.example.ewalletexample.service.CheckInputField;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterByPhone extends AppCompatActivity {

    EditText etFullname, etUserPhone, etPassword, etConfirmPass, etEmail;
    TextView tvWarning;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_by_phone);
        auth = FirebaseAuth.getInstance();
        initUI();
        AddTextWatcherIntoEditText();
    }

    void initUI(){
        etFullname = findViewById(R.id.etFullName);
        etUserPhone = findViewById(R.id.etUserPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPass = findViewById(R.id.etConfirmPassword);
        etEmail = findViewById(R.id.etEmail);
        tvWarning = findViewById(R.id.tvWarning);
    }

    void AddTextWatcherIntoEditText(){
        etFullname.addTextChangedListener(textWatcher);
        etUserPhone.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
        etConfirmPass.addTextChangedListener(textWatcher);
        etEmail.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(tvWarning.getVisibility() == View.VISIBLE){
                HideWarningTextview();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void RegisterEvent(View view){
        Response response = InputFieldIsValid();

        if(response.isStatus()){
            SwitchToVerifyByPhoneActivity();
        }

        ShowWarningTextview(response.getMessage());
    }

    private Response InputFieldIsValid(){
        String fullName = etFullname.getText().toString();

        if(TextUtils.isEmpty(fullName)){
            return new Response("Xin hãy nhập họ và tên",false);
        }

        String phone = etUserPhone.getText().toString();

        if(TextUtils.isEmpty(phone) && !CheckInputField.PhoneNumberIsValid(phone)){
            return new Response("Xin hãy nhập số điện thoại đăng kí",false);
        }

        Response response = CheckPassword();

        if(response.isStatus()){
            String email = etEmail.getText().toString();
            if(TextUtils.isEmpty(etEmail.getText().toString()) && !CheckInputField.EmailIsValid(email)){
                return new Response("Xin hãy nhập email",false);
            }
            else{
                return new Response("",true);
            }
        }

        return response;
    }

    private Response CheckPassword(){
        String pass = etPassword.getText().toString();
        String confirmPass = etConfirmPass.getText().toString();

        if (TextUtils.isEmpty(pass) || TextUtils.isEmpty(confirmPass)){
            return new Response("Xin hãy nhập mật khẩu", false);
        }
        else if(!CheckInputField.PasswordIsValid(pass) || !CheckInputField.PasswordIsValid(confirmPass)){
            return new Response("Mật khẩu không hợp lệ", false);
        }
        else if(pass.equalsIgnoreCase(confirmPass)){
            return new Response("",true);
        }
        else{
            return new Response("Mật khẩu và xác nhận mật khẩu không trùng", false);
        }
    }

    private void SwitchToVerifyByPhoneActivity(){
        Intent intent = new Intent(RegisterByPhone.this, VerifyByPhoneActivity.class);
        intent.putExtra(Symbol.REASION_VERIFY.GetValue(), Symbol.REASON_VERIFY_FOR_REGISTER.GetValue());
        intent.putExtra(Symbol.FULLNAME.GetValue(), etFullname.getText().toString());
        intent.putExtra(Symbol.PASSWORD.GetValue(), etPassword.getText().toString());
        intent.putExtra(Symbol.EMAIL.GetValue(), etEmail.getText().toString());
        intent.putExtra(Symbol.PHONE.GetValue(), etUserPhone.getText().toString());

        startActivity(intent);
    }

    private void ShowWarningTextview(String message){
        tvWarning.setText(message);
        tvWarning.setVisibility(View.VISIBLE);
    }

    private void HideWarningTextview(){
        tvWarning.setVisibility(View.GONE);
    }
}
