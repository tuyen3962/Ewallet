package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class VerifyOptionActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnVerifyByPhone, btnVerifyByEmail;
    ProgressBar progressBar;
    TextView tvIntroduce;

    private String reason;
    private User user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_option);

        btnVerifyByEmail = findViewById(R.id.btnVerifyByEmail);
        btnVerifyByPhone = findViewById(R.id.btnVerifyByPhone);
        tvIntroduce = findViewById(R.id.tvIntroduce);
//        progressBar = findViewById(R.id.proBar);

        mAuth = FirebaseAuth.getInstance();

        SettingByReasonVerify();

        btnVerifyByPhone.setOnClickListener(this);
        btnVerifyByEmail.setOnClickListener(this);
    }

    private void SettingByReasonVerify(){
        Intent intent = getIntent();

        reason = intent.getStringExtra(Symbol.REASION_VERIFY);
        if (reason.equalsIgnoreCase(Symbol.REASON_VERIFY_FOR_REGISTER))
        {
            String fullName = intent.getStringExtra(Symbol.FULLNAME);
            String username = intent.getStringExtra(Symbol.USERNAME);
            String password = intent.getStringExtra(Symbol.PASSWORD);
            String phone = intent.getStringExtra(Symbol.PHONE);
            String email = intent.getStringExtra(Symbol.EMAIL);

            user = new User(fullName, username, password, phone, email);

            tvIntroduce.setText("Xác thực tài khoản cho đăng kí");
            btnVerifyByPhone.setText("Xác thực bằng số điện thoại");
            btnVerifyByEmail.setText("Xác thực bằng email");
        }
        else{
            tvIntroduce.setText("Quên mật khẩu");
            btnVerifyByPhone.setText("Lấy lại mật khẩu từ số điện thoại");
            btnVerifyByEmail.setText("Lấy lại mật khẩu từ email");
        }
    }

    @Override
    public void onClick(View view){
        if (view.getId() == btnVerifyByEmail.getId()){
            VerifyAccountByEmail();
        }
        else if(view.getId() == btnVerifyByPhone.getId()){
            VerifyAccountByPhone();
        }
    }

    void VerifyAccountByEmail() {
        if(reason.equalsIgnoreCase(Symbol.REASON_VERIFY_FOR_FORGET)){
            Intent intent = new Intent(VerifyOptionActivity.this, VerifyUserForForget.class);
            intent.putExtra(Symbol.VERRIFY_FORGET, Symbol.VERIFY_FORGET_BY_EMAIL);
            startActivity(intent);
            return;
        }

        mAuth.createUserWithEmailAndPassword(user.getEmail(), "123456")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), "Registration successful. Please check your email for verification.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(VerifyOptionActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    void VerifyAccountByPhone(){
        if(reason.equalsIgnoreCase(Symbol.REASON_VERIFY_FOR_FORGET)){
            Intent intent = new Intent(VerifyOptionActivity.this, VerifyUserForForget.class);
            intent.putExtra(Symbol.VERRIFY_FORGET, Symbol.VERIFY_FORGET_BY_PHONE);
            startActivity(intent);
            return;
        }

        Intent intent = new Intent(VerifyOptionActivity.this, VerifyByPhoneActivity.class);
        intent.putExtra(Symbol.FULLNAME,user.getFullName());
        intent.putExtra(Symbol.USERNAME, user.getUsername());
        intent.putExtra(Symbol.PASSWORD, user.getPassword());
        intent.putExtra(Symbol.PHONE, user.getPhoneNumber());
        intent.putExtra(Symbol.EMAIL, user.getEmail());
        intent.putExtra(Symbol.REASION_VERIFY, reason);

        startActivity(intent);
    }

}
