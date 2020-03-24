package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ewalletexample.Symbol.Symbol;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class VerifyUserForForget extends AppCompatActivity {

    private final String tvDetailPhone = "Số điện thoại";
    private final String hintEtDetailPhone = "Nhập số điện thoại";

    private final String tvDetailEmail = "Email";
    private final String hintEtDetailEmail = "Nhập email";

    FirebaseAuth auth;

    TextView tvDetail, tvChangeTypeVerify;
    EditText etDetail;
    Button btnVerifyAccount;

    boolean verifyAccountByPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_user_for_forget);

        auth = FirebaseAuth.getInstance();

        init();

        ShowUIVerifyForgetByPhone();
        verifyAccountByPhone = true;
    }

    void init(){
        etDetail = findViewById(R.id.etDetail);
        tvDetail = findViewById(R.id.tvDetail);
        btnVerifyAccount = findViewById(R.id.btnVerifyAccount);
        tvChangeTypeVerify = findViewById(R.id.tvChangeTypeVerify);
    }

    public void ChangeTypeVerifyEvent(View view){
        verifyAccountByPhone = !verifyAccountByPhone;
        if(verifyAccountByPhone){
            ShowUIVerifyForgetByPhone();
        }
        else{
            ShowUIVerifyForgetByEmail();
        }
    }

    public void VerifyAccountEvent(View view){
        String detail = etDetail.getText().toString();

        if(verifyAccountByPhone){
            VerifyAccountByPhone(detail);
        }else {
            VerifyAccountByEmail(detail);
        }
    }

    private void VerifyAccountByPhone(String phone){
        Intent intent = new Intent(VerifyUserForForget.this, VerifyByPhoneActivity.class);
        intent.putExtra(Symbol.REASION_VERIFY.GetValue(), Symbol.REASON_VERIFY_FOR_FORGET.GetValue());
        intent.putExtra(Symbol.PHONE.GetValue(), phone);
        startActivity(intent);
    }

    private void VerifyAccountByEmail(String email){
        SendLinkToEmailForReset(email);
    }

    private void SendLinkToEmailForReset(final String email){
        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
        auth.createUserWithEmailAndPassword(email, "123456")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), "Registration successful. Please check your email for verification.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(VerifyUserForForget.this, ResetPassword.class);
                                        intent.putExtra(Symbol.VERRIFY_FORGET.GetValue(), Symbol.VERIFY_FORGET_BY_EMAIL.GetValue());
                                        Toast.makeText(VerifyUserForForget.this,email, Toast.LENGTH_SHORT).show();
                                        intent.putExtra(Symbol.EMAIL.GetValue(), email);
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

    private void ShowUIVerifyForgetByPhone(){
        tvDetail.setText(tvDetailPhone);
        etDetail.setHint(hintEtDetailPhone);
    }

    private void ShowUIVerifyForgetByEmail(){
        tvDetail.setText(tvDetailEmail);
        etDetail.setHint(hintEtDetailEmail);
    }
}
