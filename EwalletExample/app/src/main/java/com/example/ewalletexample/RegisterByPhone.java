package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.model.UserModel;

public class RegisterByPhone extends AppCompatActivity {

    EditText etUserPhone, etPassword, etConfirmPass, etEmail;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_by_phone);

        initUI();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterEvent();
            }
        });
    }

    void initUI(){
        btnRegister = findViewById(R.id.btnRegister);
        etUserPhone = findViewById(R.id.etUserPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPass = findViewById(R.id.etConfirmPassword);
        etEmail = findViewById(R.id.etEmail);
    }

    private void RegisterEvent(){
        UserModel model = GetUserInformationFromInputField();

        if(model.IsEmpty()){
            Toast.makeText(RegisterByPhone.this, "Xin hãy nhập vào các ô trống",Toast.LENGTH_SHORT).show();
        }
        else{
            if(model.CheckPassword()){
                Intent intent = new Intent(RegisterByPhone.this, VerifyByPhoneActivity.class);
                intent.putExtra(Symbol.USERNAME, model.getUsername());
                intent.putExtra(Symbol.PASSWORD, model.getPassword());
                intent.putExtra(Symbol.EMAIL, model.getEmail());
                intent.putExtra(Symbol.PHONE, model.getPhoneNumber());

                startActivity(intent);
            }else{
                Toast.makeText(RegisterByPhone.this,"Mật khẩu không trùng với mật khẩu xác nhập. Xin hãy nhập lại",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private UserModel GetUserInformationFromInputField(){
        String username = etUserPhone.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPass = etConfirmPass.getText().toString();
        String email = etEmail.getText().toString();

        return new UserModel(username, password,confirmPass,username,email);
    }
}
