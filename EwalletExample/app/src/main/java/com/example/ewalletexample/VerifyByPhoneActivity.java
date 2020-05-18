package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.service.MemoryPreference.SharedPreferenceLocal;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.code.CheckOTPFunction;
import com.example.ewalletexample.service.code.CodeEditText;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class VerifyByPhoneActivity extends AppCompatActivity implements CheckOTPFunction {

    FirebaseAuth auth;
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;

    private User user;
    private String reason, verifyForget, mVerificationId;
    private Gson gson;

    EditText etCode01, etCode02, etCode03, etCode04, etCode05, etCode06;
    CodeEditText codeEditText;
    Button btnVerifyPhone, btnResendVerifyCode, btnChangePhone;
    TextView tvError, txVerifyPhone;
    ProgressBarManager progressBarManager;

    CountDownTimer countDown;
    boolean canResendCode;

    PhoneAuthProvider.ForceResendingToken token;
    SharedPreferenceLocal local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_by_phone);

        Initalize();
        GetValueFromIntent();
        SendVerifyCodeToPhoneNumber();

        SetupCountDownTimer();
        countDown.start();
    }

    void Initalize(){
        gson = new Gson();
        local = new SharedPreferenceLocal(this, Symbol.NAME_PREFERENCES.GetValue());
        auth = FirebaseAuth.getInstance();
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference());
        user = new User();
        etCode01 = findViewById(R.id.etCode01);
        etCode02 = findViewById(R.id.etCode02);
        etCode03 = findViewById(R.id.etCode03);
        etCode04 = findViewById(R.id.etCode04);
        etCode05 = findViewById(R.id.etCode05);
        etCode06 = findViewById(R.id.etCode06);
        tvError = findViewById(R.id.tvError);
        btnVerifyPhone = findViewById(R.id.btnVerifyPhone);
        btnVerifyPhone.setEnabled(false);
        btnResendVerifyCode = findViewById(R.id.btnResendVerifyPhone);
        btnChangePhone = findViewById(R.id.btnChangePhone);
        txVerifyPhone = findViewById(R.id.txVerifyPhone);
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), btnResendVerifyCode, btnVerifyPhone, btnChangePhone);
        AddTextWatcherEventToEditText();
    }

    void AddTextWatcherEventToEditText(){
        codeEditText = new CodeEditText(1, this, etCode01, etCode02, etCode03, etCode04, etCode05, etCode06);
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();

        reason = intent.getStringExtra(Symbol.REASION_VERIFY.GetValue());
        if(reason.equalsIgnoreCase(Symbol.REASON_VERIFY_FOR_FORGET.GetValue())){
            verifyForget = intent.getStringExtra(Symbol.VERRIFY_FORGET.GetValue());
            user.setPhoneNumber(intent.getStringExtra(Symbol.PHONE.GetValue()));
            user.setUserId(intent.getStringExtra(Symbol.USER_ID.GetValue()));
            btnVerifyPhone.setText("Tiếp tục");
            txVerifyPhone.setText("Nhập mã code đổi mật khẩu");
        }
        else if(reason.equalsIgnoreCase(Symbol.REASON_VERIFY_FOR_LOGIN.GetValue())){
            user.setUserId(intent.getStringExtra(Symbol.USER_ID.GetValue()));
            user.setFullName(intent.getStringExtra(Symbol.FULLNAME.GetValue()));
            user.setPhoneNumber(intent.getStringExtra(Symbol.PHONE.GetValue()));
            btnVerifyPhone.setText("Tiếp tục");
            txVerifyPhone.setText("Nhập mã code");
        } else {
            user.setPassword(intent.getStringExtra(Symbol.PASSWORD.GetValue()));
            user.setFullName(intent.getStringExtra(Symbol.FULLNAME.GetValue()));
            user.setPhoneNumber(intent.getStringExtra(Symbol.PHONE.GetValue()));
            btnVerifyPhone.setText("Xác thực");
            txVerifyPhone.setText("Nhập mã code");
        }
    }

    void SetupCountDownTimer(){
        canResendCode = false;

        countDown = new CountDownTimer(15000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                SetTextForButtonResend("Gửi lại (" + (millisUntilFinished/1000) + ")");
            }

            @Override
            public void onFinish() {
                SetTextForButtonResend("Gửi lại");
                SetActiveForButtonResend(true);
                canResendCode = true;
            }
        };
    }

    void SetTextForButtonResend(String text){
        btnResendVerifyCode.setText(text);
    }

    void SetActiveForButtonResend(boolean active){
        btnResendVerifyCode.setEnabled(active);
    }

    void SendVerifyCodeToPhoneNumber(){
        String phone = "+84" + user.getPhoneNumber().substring(1);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                15,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);

        Toast.makeText(this, "has send code " + phone, Toast.LENGTH_SHORT).show();
    }

    public void ResendVerifyCodeEvent(View view){
        if(canResendCode){
            SetActiveForButtonResend(false);
            countDown.start();

            String phone = "+84" + user.getPhoneNumber().substring(1);
            Toast.makeText(VerifyByPhoneActivity.this, "Đã gửi code lại",Toast.LENGTH_SHORT).show();
            if(token != null){
                ResendVerificationCode(phone, token);
            }
        }
    }

    void ResendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            Toast.makeText(VerifyByPhoneActivity.this, phoneAuthCredential.getSmsCode(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            ShowErrorText(e.getMessage());
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, token);
            //storing the verification id that is sent to the user
            mVerificationId = s;
            token = forceResendingToken;
            Toast.makeText(VerifyByPhoneActivity.this, "code sent", Toast.LENGTH_SHORT).show();
        }
    };

    public void VerifyVerificationCode(View view){
        String code = codeEditText.GetCombineText();
        if(TextUtils.isEmpty(code) || code.length() < 6){
            return;
        }

        progressBarManager.ShowProgressBar("Verifying");

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code);
        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    public void ChangePhone(View view){
        startActivity(new Intent(VerifyByPhoneActivity.this, EnterPhoneStartAppActivity.class));
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyByPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            VerifySuccess();
                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "Somthing is wrong, we will fix it soon...";
                            progressBarManager.HideProgressBar();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            ShowErrorText(message);
                        }
                    }
                });
    }

    private void VerifySuccess(){
        local.WriteValueByKey(Symbol.KEY_PHONE.GetValue(), user.getPhoneNumber());
        local.WriteValueByKey(Symbol.KEY_FULL_NAME.GetValue(), user.getFullName());
        local.AddNewStringIntoSetString(Symbol.KEY_PHONES.GetValue(), user.getPhoneNumber());

        if(reason.equalsIgnoreCase(Symbol.REASON_VERIFY_FOR_REGISTER.GetValue())){
            progressBarManager.SetMessage("Saving");
            SendRequestRegister();
        }
        else if (reason.equalsIgnoreCase(Symbol.REASON_VERIFY_FOR_LOGIN.GetValue())){
            SendRequestLogin();
        } else if(reason.equalsIgnoreCase(Symbol.REASON_VERIFY_FOR_FORGET.GetValue())){
            SendUserDetailForResetPassword();
        }
    }

    private void SendRequestLogin(){
        Intent intent = new Intent(VerifyByPhoneActivity.this, LoginActivity.class);
        intent.putExtra(Symbol.PHONE.GetValue(), user.getPhoneNumber());
        intent.putExtra(Symbol.FULLNAME.GetValue(), user.getFullName());
        startActivity(intent);
    }

    private void SendUserDetailForResetPassword(){
        progressBarManager.HideProgressBar();

        Intent intent = new Intent(VerifyByPhoneActivity.this, ResetPassword.class);
        intent.putExtra(Symbol.VERRIFY_FORGET.GetValue(), Symbol.VERIFY_FORGET_BY_PHONE.GetValue());
        intent.putExtra(Symbol.PHONE.GetValue(), user.getPhoneNumber());
        intent.putExtra(Symbol.USER_ID.GetValue(), user.getUserId());
        startActivity(intent);
    }

    private void SendRequestRegister(){
        JSONObject postData = new JSONObject();

        try {
            postData.put("fullname",user.getFullName());
            postData.put("pin" , user.getPassword());
            postData.put("phone", user.getPhoneNumber());

            new RegisterEvent().execute(ServerAPI.REGISTER_API.GetUrl(), postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ShowErrorText(String message){
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    @Override
    public void IsFull() {
        btnVerifyPhone.setEnabled(true);
    }

    @Override
    public void NotFull() {
        btnVerifyPhone.setEnabled(false);
    }

    private class RegisterEvent extends RequestServerAPI implements RequestServerFunction {
        public RegisterEvent(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if (code == ErrorCode.SUCCESS.GetValue()){
                return  true;
            }

            ShowError(code, "");
            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            user.setUserId(jsonData.getString("userid"));
            progressBarManager.HideProgressBar();

            Intent intent = new Intent(VerifyByPhoneActivity.this, UpdateUserInformationActivity.class);
            intent.putExtra(Symbol.UPDATE_SYMBOL.GetValue(), Symbol.UPDATE_FOR_REGISTER.GetValue());
            intent.putExtra(Symbol.USER.GetValue(), gson.toJson(user));
            startActivity(intent);
        }

        @Override
        public void ShowError(int errorCode, String message) {
            Log.d("ERROR", message);
        }
    }
}