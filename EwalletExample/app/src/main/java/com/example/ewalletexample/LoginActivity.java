package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ewalletexample.Server.balance.BalanceResponse;
import com.example.ewalletexample.Server.balance.GetBalanceAPI;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.Response;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.CarrierNumber;
import com.example.ewalletexample.service.CheckInputField;
import com.example.ewalletexample.service.MemoryPreference.SharedPreferenceLocal;
import com.example.ewalletexample.service.ServerAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    MaterialTextView tvFullname, tvPhone;
    EditText etPassword;
    TextView tvError;
    User user;
    ProgressBarManager progressBarManager;
    SharedPreferenceLocal local;
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
        setContentView(R.layout.activity_login_ui);

        InitLayoutProperties();
        GetValueFromIntent();
    }

    void InitLayoutProperties(){
        local = new SharedPreferenceLocal(this, Symbol.NAME_PREFERENCES.GetValue());
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            mAuth.signOut();
        }

        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar),
                findViewById(R.id.btnLogin), findViewById(R.id.btnLogout), findViewById(R.id.btnForgetPass));

        tvFullname = findViewById(R.id.tvFullName);
        tvPhone = findViewById(R.id.tvPhone);

        etPassword = findViewById(R.id.etPassword);

        etPassword.addTextChangedListener(textWatcher);
        tvError = findViewById(R.id.tvError);
        user = new User();
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        user.setPhoneNumber(intent.getStringExtra(Symbol.PHONE.GetValue()));
        user.setFullName(intent.getStringExtra(Symbol.FULLNAME.GetValue()));

        local.WriteValueByKey(Symbol.KEY_PHONE.GetValue(), user.getPhoneNumber());
        local.WriteIntegerValueByKey(Symbol.KEY_STATE.GetValue(), 0);
        local.WriteValueByKey(Symbol.KEY_FULL_NAME.GetValue(), user.getFullName());
        local.DeleteKey(Symbol.KEY_USER_PHONE.GetValue());

        tvFullname.setText(user.getFullName());
        tvPhone.setText(user.getPhoneNumber());
    }

    public void UserLoginEvent(View view){
        progressBarManager.ShowProgressBar("Loading");

        String password = etPassword.getText().toString();

        Response response = CheckUsernameAndPassword(password);

        if(response.GetStatus()){
            SendLoginRequest(password);
        }
        else{
            progressBarManager.HideProgressBar();
            ShowErrorText(response.GetMessage());
        }
    }

    Response CheckUsernameAndPassword(String password){
        if(TextUtils.isEmpty(password)){
            return new Response(ErrorCode.VALIDATE_PIN_INVALID);
        }
        else if(!CheckInputField.PasswordIsValid(password)){
            return new Response(ErrorCode.VALIDATE_PIN_INVALID);
        }

        return new Response(ErrorCode.SUCCESS);
    }

    private void SendLoginRequest(String password){
        JSONObject json = new JSONObject();

        try{
            json.put("phone",user.getPhoneNumber());
            json.put("pin",password);

            new LoginThread().execute(ServerAPI.LOGIN_API.GetUrl(), json.toString());
        }catch (Exception e){
            progressBarManager.HideProgressBar();
            Log.d("TAG", "CheckUsernameAndPassword: "  + e.getMessage());
        }
    }

    public void UserForgetPasswordEvent(View view){
        startActivity(new Intent(LoginActivity.this, VerifyUserForForget.class));
    }

    public void UserLogoutPhoneEvent(View view){
        local.WriteIntegerValueByKey(Symbol.KEY_STATE.GetValue(), -1);
        startActivity(new Intent(LoginActivity.this, EnterPhoneStartAppActivity.class));
    }

    private void ShowErrorText(String message){
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }

    private void SwitchToMainScreen() {
        local.AddNewStringIntoSetString(Symbol.KEY_PHONES.GetValue(), user.getPhoneNumber());
        local.WriteIntegerValueByKey(Symbol.KEY_STATE.GetValue(), 1);
        local.WriteValueByKey(Symbol.KEY_USER_PHONE.GetValue(), user.ExchangeToJson());

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(Symbol.USER.GetValue(), user.ExchangeToJson());
        startActivity(intent);
    }

    private class LoginThread extends RequestServerAPI implements RequestServerFunction {
        public LoginThread() {
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if (code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }
            else {
                progressBarManager.HideProgressBar();
                ShowError(code, "Fail");
                return false;
            }
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            user = new User();
            user.setUserId(jsonData.getString("userid"));
            user.setFullName(jsonData.getString("fullname"));
            user.setAddress(jsonData.getString("address"));
            user.setPhoneNumber(jsonData.getString("phone"));
            user.setDateOfbirth(jsonData.getString("dob"));
            user.setCmnd(jsonData.getString("cmnd"));
            user.setEmail(jsonData.getString("email"));
            user.setImgID(jsonData.getString("image_id"));
            user.setImgAccountLink(jsonData.getString("image_profile"));
            user.setStatus(jsonData.getInt("status"));

            SwitchToMainScreen();
        }

        @Override
        public void ShowError(int errorCode, String message) {
            ShowErrorText("Số điện thoại hoặc mật khẩu sai");
            if(errorCode == ErrorCode.USER_PASSWORD_WRONG.GetValue()){
                ShowErrorText("Sai mật khẩu");
            }
        }
    }
}
