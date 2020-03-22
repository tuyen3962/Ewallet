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
import com.example.ewalletexample.model.UserModel;

public class RegisterActivity extends AppCompatActivity {

    EditText etFullname, etUsername, etPassword, etConfirmPassword, etPhone, etEmail;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_ui);

        Initialize();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserModel model = GetUserInfoBaseOnEditText();
                if(!IsUserInfoEmpty(model)){
                    if(TextUtils.equals(model.getConfirmPassword(), model.getPassword())){
                        Intent intent = new Intent(RegisterActivity.this, VerifyOptionActivity.class);
                        intent.putExtra(Symbol.REASION_VERIFY, Symbol.REASON_VERIFY_FOR_REGISTER);
                        intent.putExtra(Symbol.FULLNAME, model.getFullName());
                        intent.putExtra(Symbol.USERNAME, model.getUsername());
                        intent.putExtra(Symbol.PASSWORD,model.getPassword());
                        intent.putExtra(Symbol.PHONE,model.getPhoneNumber());
                        intent.putExtra(Symbol.EMAIL,model.getEmail());
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(RegisterActivity.this,"Hãy nhập lại mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    UserModel GetUserInfoBaseOnEditText(){
        String fullName = etFullname.getText().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String phone = etPhone.getText().toString();
        String email = etEmail.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        return new UserModel(fullName, username,password,confirmPassword,phone,email);
    }

    boolean IsUserInfoEmpty(UserModel model) {
        if(TextUtils.isEmpty(model.getUsername()) || TextUtils.isEmpty(model.getEmail()) ||
                TextUtils.isEmpty(model.getPassword()) || TextUtils.isEmpty(model.getPhoneNumber()) ||
                TextUtils.isEmpty(model.getConfirmPassword())){
            Toast.makeText(this, "Hẫy nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return true;
        }
        else
            return false;
    }

    void Initialize(){
        etFullname = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        btnRegister = findViewById(R.id.btnRegister);
    }
}
