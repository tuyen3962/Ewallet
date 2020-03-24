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
import android.widget.Toast;

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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class VerifyByPhoneActivity extends AppCompatActivity{

    FirebaseAuth auth;

    private User user;
    private String reason;

    private String mVerificationId;

    private final String urlRegisterUserInServer = "/um/register";

    EditText etCode01, etCode02, etCode03, etCode04, etCode05, etCode06;
    Button btnVerifyPhone;
    EditTextCodeChangeListener code12, code23, code34, code45, code56, code6;

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
                Log.d("TAG", "onClick: " + code);
                VerifyVerificationCode(code);
            }
        });
    }


    void Initalize(){
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

        Toast.makeText(this, user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();

        reason = intent.getStringExtra(Symbol.REASION_VERIFY);
        if(reason.equalsIgnoreCase(Symbol.REASON_VERIFY_FOR_FORGET)){
            String phone = intent.getStringExtra(Symbol.PHONE);
            user = new User(phone);
        }
        else{
            String fullName = intent.getStringExtra(Symbol.FULLNAME);
            String password = intent.getStringExtra(Symbol.PASSWORD);
            String phone = intent.getStringExtra(Symbol.PHONE);
            String email = intent.getStringExtra(Symbol.EMAIL);
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
                            if(reason.equalsIgnoreCase(Symbol.REASON_VERIFY_FOR_REGISTER)){
                                SendDataToServer();
//                                //verification successful we will start the profile activity
//                                Intent intent = new Intent(VerifyByPhoneActivity.this, LoginActivity.class);
//                                //Store user data
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

//                                startActivity(intent);
                            }
                            else{
                                //verification successful we will start the profile activity
                                Intent intent = new Intent(VerifyByPhoneActivity.this, ResetPassword.class);
                                intent.putExtra(Symbol.VERRIFY_FORGET, Symbol.VERIFY_FORGET_BY_PHONE);
                                intent.putExtra(Symbol.PHONE, user.getPhoneNumber());
                                startActivity(intent);
                            }

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                        }
                    }
                });
    }

    private void SendDataToServer(){
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

    private class LoadUserDataToServer extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String data = "";

            HttpURLConnection httpURLConnection = null;

            try {

                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(params[1]);
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            Log.d("TAG",data);
            return data;
        }
    }
}