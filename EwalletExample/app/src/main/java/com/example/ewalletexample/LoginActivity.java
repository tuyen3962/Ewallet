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

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ui);

        InitLayoutProperties();
    }

    void InitLayoutProperties(){
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
    }

    public void UserLoginEvent(View view){
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

    public void UserForgetPasswordEvent(){
//        Intent intent = new Intent(LoginActivity.this, VerifyOptionActivity.class);
//        intent.putExtra(Symbol.REASION_VERIFY,Symbol.REASON_VERIFY_FOR_FORGET);
//
//        startActivity(intent);
    }

    public void UserRegisterEvent(View view){
        startActivity(new Intent(LoginActivity.this, RegisterByPhone.class));
    }
}
