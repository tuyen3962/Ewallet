package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ewalletexample.Server.RequestServerAPI;
import com.example.ewalletexample.Server.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.model.Response;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.CheckInputField;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.HandleDataFromFirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetPassword extends AppCompatActivity implements HandleDataFromFirebaseDatabase<UserModel> {

    FirebaseAuth auth;
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;

    ProgressDialog progressDialog;
    private EditText etPassword, etConfirmPassword;
    private TextView tvUsername, tvError;

    private boolean isFinishVerifyEmail = false;
    private String reason, email, phone, userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        auth = FirebaseAuth.getInstance();
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference(), this);
        Initialize();

        GetValueFromIntent();
    }

    void Initialize(){
        tvError = findViewById(R.id.tvError);
        tvUsername = findViewById(R.id.tvUsername);
        progressDialog = new ProgressDialog(this);
        etPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmNewPasswork);
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        reason = intent.getStringExtra(Symbol.VERRIFY_FORGET.GetValue());
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        if(reason.equalsIgnoreCase(Symbol.VERIFY_FORGET_BY_EMAIL.GetValue())){
            email = intent.getStringExtra(Symbol.EMAIL.GetValue());
            LoadingVerifyEmail verifyEmail = new LoadingVerifyEmail(email);
            verifyEmail.start();
        }
        else
        {
            phone = intent.getStringExtra(Symbol.PHONE.GetValue());
            tvUsername.setText(phone);
        }
    }

    private void ShowProgressDialog(String message){
        progressDialog.setTitle(message);
        progressDialog.show();
    }

    private void HideProgressDialog(){
        progressDialog.hide();
    }

    public void VerifyResetPasswordEvent(View view){
        ShowProgressDialog("Loading...");

        String password = etPassword.getText().toString();
        String confirmPass = etConfirmPassword.getText().toString();

        Response response = CheckPassword(password, confirmPass);

        if(response.GetStatus()){
            firebaseDatabaseHandler.RegisterDataListener();
        }
        else{
            ShowErrorText(response.GetMessage());
        }
    }

    private Response CheckPassword(String pass, String confirmPass){
        if(TextUtils.isEmpty(pass)){
            return new Response(ErrorCode.EMPTY_PIN);
        }
        else if(TextUtils.isEmpty(confirmPass)){
            return new Response(ErrorCode.EMPTY_CONFIRM_PIN);
        }
        else if(!CheckInputField.PasswordIsValid(pass)){
            return new Response(ErrorCode.VALIDATE_PIN_INVALID);
        }
        else if(!pass.equalsIgnoreCase(confirmPass)){
            return new Response(ErrorCode.UNVALID_PIN_AND_CONFIRM_PIN);
        }

        return new Response(ErrorCode.SUCCESS);
    }

    public void CancelResetPasswordEvent(View view){
        startActivity(new Intent(ResetPassword.this, LoginActivity.class));
    }

    private void ShowErrorText(String message){
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        HideProgressDialog();
    }

    @Override
    public void HandleDataModel(UserModel model) {
        if(model == null) return;

        JSONObject postData = new JSONObject();

        try {
            postData.put("userid", model.getUserID());
            postData.put("dob",model.getDob());
            postData.put("pin",etPassword.getText().toString());
            postData.put("cmnd",model.getCmnd());
            postData.put("address", model.getAddres());

            new ResetPasswordEvent().execute(ServerAPI.UPDATE_USER_API.GetUrl(), postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void HandleDataSnapShot(DataSnapshot dataSnapshot) {
        for(DataSnapshot data : dataSnapshot.child(Symbol.CHILD_NAME_FIREBASE_DATABASE.GetValue()).getChildren()){
            UserModel model = data.getValue(UserModel.class);
            if (model.getPhone().equalsIgnoreCase(phone)){
                Log.d("TAG", "HandleDataSnapShot: " + model.toString());
                firebaseDatabaseHandler.UnregisterValueListener(model);
                return;
            }
        }
        Log.d("TAG", "HandleDataSnapShot: null");
        firebaseDatabaseHandler.UnregisterValueListener(null);
    }

    @Override
    public void HandlerDatabaseError(DatabaseError databaseError) {
        ShowErrorText(databaseError.getMessage());
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
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ResetPasswordEvent extends RequestServerAPI implements RequestServerFunction {
        public ResetPasswordEvent(){
            super();
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            Log.d("TAG", "CheckReturnCode: " + code);
            if(code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }
            ShowError(code, "Doi mat khau that bai");
            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            startActivity(new Intent(ResetPassword.this, LoginActivity.class));
        }

        @Override
        public void ShowError(int errorCode, String message) {
            ShowErrorText(message);
        }
    }
}
