package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.ewalletexample.service.code.CodeEditText;
import com.example.ewalletexample.service.email.GMailSender;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.ResponseModelByKey;
import com.example.ewalletexample.service.websocket.WebsocketResponse;
import com.example.ewalletexample.utilies.Utilies;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateEmailActivity extends AppCompatActivity implements ResponseModelByKey<UserModel>, UpdateUserResponse, WebsocketResponse {

    FirebaseAuth mAuth;
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    ProgressBarManager progressBarManager;

    View layoutCode, layoutEnterEmail;
    Button btnVerify;
    UpdateUserAPI updateAPI;
    String userid, email, code, newEmail;
    CodeEditText codeEditText;
    TextView tvError;
    EditText etEmail, etCode01, etCode02, etCode03, etCode04, etCode05, etCode06;
    UserModel model;
    boolean enterEmail, changeBalance;
    long balance;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(tvError.getVisibility() == View.VISIBLE){
                tvError.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
        GetValueFromIntent();
        Initialize();
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        email = intent.getStringExtra(Symbol.EMAIL.GetValue());
    }

    void Initialize(){
        changeBalance = false;
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference());
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), findViewById(R.id.btnVerify));
        progressBarManager.ShowProgressBar("Loading");
        etEmail = findViewById(R.id.etEmail);
        etCode01 = findViewById(R.id.etCode01);
        etCode02 = findViewById(R.id.etCode02);
        etCode03 = findViewById(R.id.etCode03);
        etCode04 = findViewById(R.id.etCode04);
        etCode05 = findViewById(R.id.etCode05);
        etCode06 = findViewById(R.id.etCode06);
        etEmail.addTextChangedListener(textWatcher);
        etCode01.addTextChangedListener(textWatcher);
        etCode02.addTextChangedListener(textWatcher);
        etCode03.addTextChangedListener(textWatcher);
        etCode04.addTextChangedListener(textWatcher);
        etCode05.addTextChangedListener(textWatcher);
        etCode06.addTextChangedListener(textWatcher);
        tvError = findViewById(R.id.tvError);
        layoutCode = findViewById(R.id.layoutCode);
        layoutEnterEmail = findViewById(R.id.layoutEnterEmail);
        etEmail.setHint(email);
        btnVerify = findViewById(R.id.btnVerify);
        tvError.setVisibility(View.GONE);
        updateAPI = new UpdateUserAPI(userid, this);
        codeEditText = new CodeEditText(1, etCode01, etCode02, etCode03, etCode04, etCode05, etCode06);
        firebaseDatabaseHandler.GetModelByKey(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE, userid, UserModel.class, this);
        ShowEnterEmailLayout();
    }

    @Override
    public void GetModel(UserModel data) {
        this.model = data;
        progressBarManager.HideProgressBar();
    }

    void ShowErrorMessage(String message){
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }

    public void BackPreviousActivity(View view){
        Intent intent = new Intent();
        intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), changeBalance);
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void UpdateEmail(View view){
        if(enterEmail){
            SendCodeToEmail();
        } else {
            VerifyCode();
        }
    }

    void SendCodeToEmail(){
        if(etEmail.getText().toString().isEmpty()){
            ShowErrorMessage("Nhập email");
        } else {
            String email = etEmail.getText().toString();
            if(CheckInputField.EmailIsValid(email)){
                if(this.email.equalsIgnoreCase(email)){
                    Toast.makeText(this, "Email bạn nhập trùng với email bạn đã yêu cầu xác thực", Toast.LENGTH_SHORT).show();
                    return;
                }
                newEmail = email;
                code = Utilies.GetStringNumberByLength(6);
                new SendCodeToEmailAddressThread().execute(email, code);
                ShowEnterCodeLayout();
            } else {
                ShowErrorMessage("Định dạng email không đúng");
            }
        }
    }

    void VerifyCode(){
        progressBarManager.ShowProgressBar("Loading");
        String currentCode = codeEditText.GetCombineText();
        if(currentCode.equalsIgnoreCase(code)){
            this.email = newEmail;
            newEmail = "";
            CreateUserInFirebaseWithEmail(email);
            updateAPI.setEmail(email);
            updateAPI.UpdateUser();
        } else {
            progressBarManager.HideProgressBar();
            ShowErrorMessage("Mã xác nhận sai");
        }
    }

    void CreateUserInFirebaseWithEmail(String email){
        mAuth.signInWithEmailAndPassword(email, "123456").addOnSuccessListener(new OnSuccessListener<AuthResult>() {
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
    public void UpdateSuccess() {
        Intent intent = new Intent();
        intent.putExtra(Symbol.EMAIL.GetValue(), email);
        intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), changeBalance);
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void UpdateFail() {
        ShowErrorMessage("Lỗi kết nối");
    }

    void ShowEnterEmailLayout(){
        layoutCode.setVisibility(View.GONE);
        layoutEnterEmail.setVisibility(View.VISIBLE);
        btnVerify.setText("Tiếp tục");
        enterEmail = true;
    }

    void ShowEnterCodeLayout(){
        layoutCode.setVisibility(View.VISIBLE);
        layoutEnterEmail.setVisibility(View.GONE);
        btnVerify.setText("Xác thực");
        enterEmail = false;
    }

    @Override
    public void UpdateWallet(String userid, long balance) {
        changeBalance = true;
        if(this.userid.equalsIgnoreCase(userid)){
            this.balance = balance;
        }
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
            }
        }
    }
}
