package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    FirebaseAuth auth;

    View view;
    ProgressBar progressBar;
    private EditText etPassword, etConfirmPassword;
    private TextView tvUsername;
    Button btnReset;

    private boolean isFinishVerifyEmail = false;
    private String reason;
    private String email;
    private String phone;
    private User userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        auth = FirebaseAuth.getInstance();

        init();

        GetValueFromIntent();
    }

    void init(){
        view = findViewById(R.id.view);
        tvUsername = findViewById(R.id.tvUsername);
        progressBar = findViewById(R.id.progressLoading);
        etPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmNewPasswork);
        btnReset = findViewById(R.id.btnResetNewPassword);
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        reason = intent.getStringExtra(Symbol.VERRIFY_FORGET);
        if(reason.equalsIgnoreCase(Symbol.VERIFY_FORGET_BY_EMAIL)){
            email = intent.getStringExtra(Symbol.EMAIL);
            DisableResetPassword();
            LoadingVerifyEmail verifyEmail = new LoadingVerifyEmail(email);
            verifyEmail.start();
        }
        else
        {
            phone = intent.getStringExtra(Symbol.PHONE);
        }
    }

    void DisableResetPassword(){
        etPassword.setEnabled(false);
        etConfirmPassword.setEnabled(false);
        btnReset.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
    }

    void EnableResetPassword(){
        etPassword.setEnabled(true);
        etConfirmPassword.setEnabled(true);
        btnReset.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
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
                            EnableResetPassword();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class LoadingUserData extends Thread{
        private String data;
        private String reason;

        public LoadingUserData(String _reason, String _data){
            reason = _reason;
            data = _data;
        }

        public void Run() throws InterruptedException {

        }
    }
}
