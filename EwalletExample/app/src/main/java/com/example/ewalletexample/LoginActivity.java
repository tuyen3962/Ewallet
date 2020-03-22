package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etUsername, etPassword;
    Button btnLoginByPhone, btnRegister, btnForgetPassword, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ui);

        InitLayoutProperties();

        SetOnClickEventForButton();
    }

    void InitLayoutProperties(){
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnForgetPassword = findViewById(R.id.btnForgetPass);
        btnRegister = findViewById(R.id.btnRegister);
        btnLoginByPhone = findViewById(R.id.btnLoginByPhone);
    }

    void SetOnClickEventForButton(){
        btnForgetPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnLoginByPhone.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnLogin.getId()){
            LoginButtonEvent();
        } else if(view.getId() == btnRegister.getId()){
            RegisterButtonEvent();
        } else if (view.getId() == btnLoginByPhone.getId()){
            LoginByPhoneButtonEvent();
        }else if(view.getId() == btnForgetPassword.getId()){
            ForgetPasswordButtonEvent();
        }
    }

    void LoginButtonEvent(){
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        User user = CheckUsernameAndPassword(username,password);
    }

    User CheckUsernameAndPassword(String username, String password){
        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Nhap username", Toast.LENGTH_SHORT).show();
            return null;
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Nhap mat khau", Toast.LENGTH_SHORT).show();
            return null;
        }

        return null;
    }

    void RegisterButtonEvent(){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    void ForgetPasswordButtonEvent(){
        Intent intent = new Intent(LoginActivity.this, VerifyOptionActivity.class);
        intent.putExtra(Symbol.REASION_VERIFY,Symbol.REASON_VERIFY_FOR_FORGET);

        startActivity(intent);
    }

    void LoginByPhoneButtonEvent(){
        startActivity(new Intent(LoginActivity.this, RegisterByPhone.class));
    }
}
