package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.service.EditTextCodeChangeListener;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

public class VerifyByPhoneActivity extends AppCompatActivity{

    FirebaseAuth auth;

    private User user;
    private String reason;

    private String mVerificationId;

    private final String urlRegisterUserInServer = "http://192.168.1.14:8080/um/register";

    EditText etCode01, etCode02, etCode03, etCode04, etCode05, etCode06;
    Button btnVerifyPhone;
    EditTextCodeChangeListener code12, code23, code34, code45, code56, code6;

    ProgressBar progressBar;
    TextView tvVerifying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_by_phone);

        auth = FirebaseAuth.getInstance();

        GetValueFromIntent();

        SendVerifyCodeToPhoneNumber();

        Initalize();

        btnVerifyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = GetCode();

                VerifyVerificationCode(code);
            }
        });
    }


    void Initalize(){
        progressBar = findViewById(R.id.progressBar);
        tvVerifying = findViewById(R.id.tvVerifying);

        etCode01 = findViewById(R.id.etCode01);
        etCode02 = findViewById(R.id.etCode02);
        etCode03 = findViewById(R.id.etCode03);
        etCode04 = findViewById(R.id.etCode04);
        etCode05 = findViewById(R.id.etCode05);
        etCode06 = findViewById(R.id.etCode06);
        btnVerifyPhone = findViewById(R.id.btnVerifyPhone);

        AddTextWatcherEventToEditText();
    }

    void AddTextWatcherEventToEditText(){
        code12 = new EditTextCodeChangeListener(etCode01, etCode02,1);
        code23 = new EditTextCodeChangeListener(etCode02, etCode03,1);
        code34 = new EditTextCodeChangeListener(etCode03, etCode04,1);
        code45 = new EditTextCodeChangeListener(etCode04, etCode05,1);
        code56 = new EditTextCodeChangeListener(etCode05, etCode06,1);
        code6 = new EditTextCodeChangeListener(etCode06,1);
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

    void GetValueFromIntent(){
        Intent intent = getIntent();

        reason = intent.getStringExtra(Symbol.REASION_VERIFY.GetValue());
        if(reason.equalsIgnoreCase(Symbol.REASON_VERIFY_FOR_FORGET.GetValue())){
            String phone = intent.getStringExtra(Symbol.PHONE.GetValue());
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
            Toast.makeText(VerifyByPhoneActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
//            Toast.makeText(VerifyByPhoneActivity.this, "code has sent " + s,Toast.LENGTH_LONG).show();
            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    void VerifyVerificationCode(String code){
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
                                //verification successful we will start the profile activity
                                Intent intent = new Intent(VerifyByPhoneActivity.this, ResetPassword.class);
                                intent.putExtra(Symbol.VERRIFY_FORGET.GetValue(), Symbol.VERIFY_FORGET_BY_PHONE);
                                intent.putExtra(Symbol.PHONE.GetValue(), user.getPhoneNumber());
                                startActivity(intent);
                            }

                        } else {

                            //verification unsuccessful.. display an error message
                            HideLoading();
                            String message = "Somthing is wrong, we will fix it soon...";
                            HideLoading();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                        }
                    }
                });
    }

    private void ShowLoading(){
        SetActiveForCodeUI(false);
        ShowProgressLoading();
    }

    private void HideLoading(){
        SetActiveForCodeUI(true);
        HideProgressLoading();
    }

    private void SetActiveForCodeUI(boolean active){
        etCode06.setEnabled(active);
        etCode01.setEnabled(active);
        etCode02.setEnabled(active);
        etCode03.setEnabled(active);
        etCode04.setEnabled(active);
        etCode05.setEnabled(active);
        btnVerifyPhone.setEnabled(active);
    }

    private void ShowProgressLoading(){
        tvVerifying.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void HideProgressLoading(){
        tvVerifying.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void SendRequestRegisterToServer(){
        JSONObject postData = new JSONObject();

        try {
            postData.put("fullname",user.getFullName());
            postData.put("pin" , user.getPassword());
            postData.put("phone", user.getPhoneNumber());

            new LoadUserDataToServer().execute(urlRegisterUserInServer, postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void SaveUserProfileAndLogOutTheCurrentFirebaseUser(){
        FirebaseUser firebaseUser = auth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getFullName())
                .build();

        if(firebaseUser != null){
            firebaseUser.updateProfile(profileUpdates);
            auth.signOut();
        }
    }

    private class LoadUserDataToServer extends AsyncTask<String, Integer, ResponseEntity<String>> {

        @Override
        protected ResponseEntity<String> doInBackground(String... params) {
            final String url = params[0];

            RestTemplate restTemplate = new RestTemplate();

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

                HttpEntity<String> entity = new HttpEntity<>(params[1], headers);

                ResponseEntity<String> response = restTemplate.exchange(url,HttpMethod.POST, entity, String.class);

                return response;
            }
            catch (Exception e){
                Log.w("Warn",e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(ResponseEntity<String> response){
            if(response != null){
                String body = response.getBody();
                try{
                    JSONObject  json = new JSONObject(body);
                    int retureCode = json.getInt("returncode");
                    if (retureCode == ErrorCode.SUCCESS.GetValue()){

                        if(reason.equalsIgnoreCase(Symbol.REASON_VERIFY_FOR_REGISTER.GetValue())){
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(VerifyByPhoneActivity.this, LoginActivity.class);
                            //Store user data
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            SaveUserProfileAndLogOutTheCurrentFirebaseUser();
                            startActivity(intent);
                        }
                        else{
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(VerifyByPhoneActivity.this, ResetPassword.class);
                            //Store user data
                            intent.putExtra(Symbol.VERRIFY_FORGET.GetValue(), Symbol.VERIFY_FORGET_BY_PHONE.GetValue());
                            intent.putExtra(Symbol.PHONE.GetValue(), user.getPhoneNumber());

                            startActivity(intent);
                        }
                    }
                    else
                    {
                        HideLoading();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}