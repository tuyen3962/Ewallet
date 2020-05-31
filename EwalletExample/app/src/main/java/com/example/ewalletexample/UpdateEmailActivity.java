package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ewalletexample.Server.api.update.UpdateUserAPI;
import com.example.ewalletexample.Server.api.update.UpdateUserResponse;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.CheckInputField;
import com.example.ewalletexample.service.code.CheckOTPFunction;
import com.example.ewalletexample.service.code.CodeEditText;
import com.example.ewalletexample.service.email.GMailSender;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.ResponseModelByKey;
import com.example.ewalletexample.service.toolbar.CustomToolbarContext;
import com.example.ewalletexample.service.toolbar.ToolbarEvent;
import com.example.ewalletexample.service.websocket.WebsocketResponse;
import com.example.ewalletexample.utilies.Utilies;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateEmailActivity extends AppCompatActivity implements ResponseModelByKey<UserModel>, WebsocketResponse,
        ToolbarEvent, CheckOTPFunction {

    FirebaseAuth mAuth;
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    ProgressBarManager progressBarManager;

    Button btnVerify;
    String userid, email, code;
    CodeEditText codeEditText;
    TextView tvError, tvEmail, tvCountTimeDown;
    EditText etCode01, etCode02, etCode03, etCode04, etCode05, etCode06;
    UserModel model;
    CustomToolbarContext customToolbarContext;
    CountDownTimer resendCountDownTime, timeValidationCode;
    boolean changeBalance, isCodeValid;
    long balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
        GetValueFromIntent();
        Initialize();
        SetupCountTimeDown();
        SendCodeToEmail(tvCountTimeDown);
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        email = intent.getStringExtra(Symbol.EMAIL.GetValue());
    }

    void Initialize(){
        isCodeValid = false;
        changeBalance = false;
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference());
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), findViewById(R.id.btnVerify), findViewById(R.id.btnChangeEmail), findViewById(R.id.btnBackToPreviousActivity));
        tvEmail = findViewById(R.id.tvEmail);
        tvEmail.setText(email);
        tvCountTimeDown = findViewById(R.id.tvCountTimeDown);
        etCode01 = findViewById(R.id.etCode01);
        etCode02 = findViewById(R.id.etCode02);
        etCode03 = findViewById(R.id.etCode03);
        etCode04 = findViewById(R.id.etCode04);
        etCode05 = findViewById(R.id.etCode05);
        etCode06 = findViewById(R.id.etCode06);
        customToolbarContext = new CustomToolbarContext(this, this);

        tvError = findViewById(R.id.tvError);
        btnVerify = findViewById(R.id.btnVerify);
        btnVerify.setEnabled(false);
        tvError.setVisibility(View.GONE);
        codeEditText = new CodeEditText(1, this, etCode01, etCode02, etCode03, etCode04, etCode05, etCode06);
        firebaseDatabaseHandler.GetModelByKey(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE, userid, UserModel.class, this);
    }

    void SetupCountTimeDown(){
        resendCountDownTime = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCountTimeDown.setText((millisUntilFinished/1000) + "");
            }

            @Override
            public void onFinish() {
                tvCountTimeDown.setEnabled(true);
                tvCountTimeDown.setText("Gửi lại");
            }
        };

        timeValidationCode = new CountDownTimer(180000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                isCodeValid = false;
            }
        };
    }

    @Override
    public void IsFull() {
        btnVerify.setEnabled(true);
        if(tvError.getVisibility() == View.VISIBLE){
            tvError.setVisibility(View.GONE);
        }
    }

    @Override
    public void NotFull() {
        btnVerify.setEnabled(false);
        if(tvError.getVisibility() == View.VISIBLE){
            tvError.setVisibility(View.GONE);
        }
    }

    @Override
    public void GetModel(UserModel data) {
        this.model = data;
    }

    public void SendCodeToEmail(View view){
        tvCountTimeDown.setEnabled(false);
        resendCountDownTime.start();
        code = Utilies.GetStringNumberByLength(6);
        new SendCodeToEmailAddressThread().execute(email, code);
    }

    @Override
    public void BackToPreviousActivity() {
        Intent intent = new Intent();
        intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), changeBalance);
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void ChangeEmail(View view){
        BackToPreviousActivity();
    }

    public void VerifyOTPEmail(View view){
        if (isCodeValid){
            progressBarManager.ShowProgressBar("Loading");
            String currentCode = codeEditText.GetCombineText();
            if(currentCode.equalsIgnoreCase(code)){
                CreateUserInFirebaseWithEmail(email);
            } else {
                progressBarManager.HideProgressBar();
                ShowErrorMessage("Mã xác nhận sai");
            }
        } else {
            Toast.makeText(this,"Mã xác thực đã quá hạn. Xin vui lòng nhấn gửi lại đế lấy mã mới.",Toast.LENGTH_SHORT).show();
        }
    }

    void CreateUserInFirebaseWithEmail(String email){
        mAuth.signInWithEmailAndPassword(email, "123456").addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if(firebaseUser != null){
                    model.setEmail(firebaseUser.getEmail());
                    firebaseDatabaseHandler.UpdateData(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE.GetValue(), userid, model);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mAuth.createUserWithEmailAndPassword(email, "123456").addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if(firebaseUser != null){
                            model.setEmail(firebaseUser.getEmail());
                            model.setEmailToken(firebaseUser.getUid());
                            firebaseDatabaseHandler.UpdateData(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE.GetValue(), userid, model);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "onFailure: ");
                    }
                });
            }
        });
    }

    @Override
    public void UpdateWallet(String userid, long balance) {
        changeBalance = true;
        if(this.userid.equalsIgnoreCase(userid)){
            this.balance = balance;
        }
    }

    void ShowErrorMessage(String message){
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }

    class SendCodeToEmailAddressThread extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String receiverEmail = params[0];
                String code = params[1];
                GMailSender gMailSender = new GMailSender(getString(R.string.email_address), getString(R.string.password_email));
                String subject = getString(R.string.subject_email);
                String body = getString(R.string.content_email) + code;
                gMailSender.sendMail(subject, body, getString(R.string.email_address), receiverEmail);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result){
                Toast.makeText(UpdateEmailActivity.this, "Đã gửi otp cho email của bạn. Xin hãy nhập mã OTP.", Toast.LENGTH_SHORT).show();
                isCodeValid = true;
                timeValidationCode.start();
            }
        }
    }
}
