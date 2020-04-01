package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ewalletexample.Server.RequestServerAPI;
import com.example.ewalletexample.Server.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.code.CodeEditText;
import com.example.ewalletexample.service.code.EditTextCodeChangeListener;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.HandleDataFromFirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class VerifyByPhoneActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;

    private User user;
    private String reason, verifyForget, userid;

    private String mVerificationId;

    EditText etCode01, etCode02, etCode03, etCode04, etCode05, etCode06;
    CodeEditText codeEditText;
    Button btnVerifyPhone, btnResendVerifyCode;
    TextView tvError;
    ProgressDialog progressDialog;

    CountDownTimer countDown;
    boolean canResendCode;

    PhoneAuthProvider.ForceResendingToken token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_by_phone);

        auth = FirebaseAuth.getInstance();
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference());

        GetValueFromIntent();

        SendVerifyCodeToPhoneNumber();

        Initalize();

        SetupCountDownTimer();
        countDown.start();
    }

    void Initalize(){
        progressDialog = new ProgressDialog(this);

        etCode01 = findViewById(R.id.etCode01);
        etCode02 = findViewById(R.id.etCode02);
        etCode03 = findViewById(R.id.etCode03);
        etCode04 = findViewById(R.id.etCode04);
        etCode05 = findViewById(R.id.etCode05);
        etCode06 = findViewById(R.id.etCode06);
        tvError = findViewById(R.id.tvError);
        btnVerifyPhone = findViewById(R.id.btnVerifyPhone);
        btnResendVerifyCode = findViewById(R.id.btnResendVerifyPhone);
        AddTextWatcherEventToEditText();
    }

    void AddTextWatcherEventToEditText(){
        codeEditText = new CodeEditText(1, etCode01, etCode02, etCode03, etCode04, etCode05, etCode06);
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();

        reason = intent.getStringExtra(Symbol.REASION_VERIFY.GetValue());
        if(reason.equalsIgnoreCase(Symbol.REASON_VERIFY_FOR_FORGET.GetValue())){
            verifyForget = intent.getStringExtra(Symbol.VERRIFY_FORGET.GetValue());
            String phone = intent.getStringExtra(Symbol.PHONE.GetValue());
            userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
            user = new User(phone);
        }
        else{
            String fullName = intent.getStringExtra(Symbol.FULLNAME.GetValue());
            String password = intent.getStringExtra(Symbol.PASSWORD.GetValue());
            String phone = intent.getStringExtra(Symbol.PHONE.GetValue());
            String email = intent.getStringExtra(Symbol.EMAIL.GetValue());
            user = new User(fullName,phone,password,email);
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

    String GetCode(){
        return etCode01.getText().toString() +
                etCode02.getText().toString() +
                 etCode03.getText().toString() +
                  etCode04.getText().toString() +
                   etCode05.getText().toString() +
                    etCode06.getText().toString();
    }

    void SendVerifyCodeToPhoneNumber(){
        String phone = "+84" + user.getPhoneNumber().substring(1);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
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

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            Toast.makeText(VerifyByPhoneActivity.this, code, Toast.LENGTH_LONG).show();
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
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
        }
    };

    public void VerifyVerificationCode(View view){
        String code = GetCode();
        ShowLoading();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code);
        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyByPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(reason.equalsIgnoreCase(Symbol.REASON_VERIFY_FOR_REGISTER.GetValue())){
                                SendRequestRegisterToServer();
                            }
                            else{
                                SendUserDetailForResetPassword();
                            }
                        } else {

                            //verification unsuccessful.. display an error message
                            String message = "Somthing is wrong, we will fix it soon...";
                            HideLoading();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            ShowErrorText(message);
                        }
                    }
                });
    }

    private void SendUserDetailForResetPassword(){
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if(firebaseUser != null){
            auth.signOut();
        }

        Intent intent = new Intent(VerifyByPhoneActivity.this, ResetPassword.class);
        intent.putExtra(Symbol.VERRIFY_FORGET.GetValue(), Symbol.VERIFY_FORGET_BY_PHONE.GetValue());
        intent.putExtra(Symbol.PHONE.GetValue(), user.getPhoneNumber());
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        startActivity(intent);
    }

    private void ShowLoading(){
        progressDialog.setTitle("Verifying...");
        progressDialog.show();
    }

    private void HideLoading(){
        progressDialog.hide();
    }

    private void ShowErrorText(String message){
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void SendRequestRegisterToServer(){
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

    private void SaveUserProfileAndLogOutTheCurrentFirebaseUser(String userID){
        FirebaseUser firebaseUser = auth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getFullName())
                .build();

        if(firebaseUser != null){
            firebaseUser.updateProfile(profileUpdates);
            UserModel model = new UserModel(user.getFullName(), userID, user.getPhoneNumber(), firebaseUser.getUid(), user.getEmail());
            firebaseDatabaseHandler.PushDataIntoDatabase("users", model);
            auth.signOut();
        }
    }

    private class RegisterEvent extends RequestServerAPI implements RequestServerFunction {
        public RegisterEvent(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            Log.d("TAG", "CheckReturnCode: " + code);
            if (code == ErrorCode.SUCCESS.GetValue()){
                return  true;
            }

            ShowError(code, "");
            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            userid = jsonData.getString("userid");
            Log.d("TAG", "DataHandle: " + verifyForget + " " + reason);
            //verification successful we will start the profile activity
            SaveUserProfileAndLogOutTheCurrentFirebaseUser(userid);
            JSONObject postData = new JSONObject();
            postData.put("userid",userid);

            new GetBalance().execute(ServerAPI.GET_BALANCE_API.GetUrl(), postData.toString());
        }

        @Override
        public void ShowError(int errorCode, String message) {
            Log.d("ERROR", message);
        }
    }

    private class GetBalance extends RequestServerAPI implements RequestServerFunction{
        public GetBalance(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if (code == ErrorCode.SUCCESS.GetValue()){
                return  true;
            }
            else{
                ShowError(code, "");
                return false;
            }
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            long amount = jsonData.getLong("amount");
            Intent intent = new Intent(VerifyByPhoneActivity.this, MainActivity.class);
            intent.putExtra(Symbol.USER_ID.GetValue(),userid);
            intent.putExtra(Symbol.AMOUNT.GetValue(), amount);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        @Override
        public void ShowError(int errorCode, String message) {
            Log.d("ERROR", message);
        }
    }
}