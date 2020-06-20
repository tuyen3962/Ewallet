package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ewalletexample.Server.api.balance.BalanceResponse;
import com.example.ewalletexample.Server.api.balance.GetBalanceAPI;
import com.example.ewalletexample.Server.api.login.UserLoginAPI;
import com.example.ewalletexample.Server.api.login.UserLoginRequest;
import com.example.ewalletexample.Server.api.login.UserLoginResponse;
import com.example.ewalletexample.Server.api.update.UpdateUserAPI;
import com.example.ewalletexample.Server.api.update.UpdateUserResponse;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.Response;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.CheckInputField;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.HandleDataFromFirebaseDatabase;
import com.example.ewalletexample.service.toolbar.CustomToolbarContext;
import com.example.ewalletexample.service.toolbar.ToolbarEvent;
import com.example.ewalletexample.utilies.GsonUtils;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.example.ewalletexample.utilies.Utilies;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import javax.crypto.SecretKey;

public class ResetPassword extends AppCompatActivity implements ToolbarEvent, UpdateUserResponse, UserLoginResponse, BalanceResponse, HandleDataFromFirebaseDatabase<UserModel> {

    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    FirebaseAuth auth;
    CustomToolbarContext customToolbarContext;
    ProgressBarManager progressBarManager;
    TextInputEditText etPassword, etConfirmPassword;
    MaterialTextView tvUsername, tvError;
    UpdateUserAPI updateUserAPI;
    UserLoginAPI userLoginAPI;
    Button btnResetNewPassword;
    String reason, secretKeyString1, secretKeyString2;
    User user;
    SecretKey secretKey1, secretKey2;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (tvError.getVisibility() == View.VISIBLE){
                tvError.setVisibility(View.GONE);
            }

            if (etConfirmPassword.length() == 6 && etPassword.length() == 6){
                Utilies.SetEneableButton(btnResetNewPassword, true);
            } else {
                Utilies.SetEneableButton(btnResetNewPassword, false);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        auth = FirebaseAuth.getInstance();
        Initialize();

        GetValueFromIntent();
    }

    void Initialize(){
        user = new User();
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference(), this);
        tvUsername = findViewById(R.id.tvUsername);
        btnResetNewPassword = findViewById(R.id.btnResetNewPassword);
        btnResetNewPassword.setEnabled(false);
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), btnResetNewPassword);
        etPassword = findViewById(R.id.etNewPassword);
        tvError = findViewById(R.id.tvError);
        etConfirmPassword = findViewById(R.id.etConfirmNewPasswork);
        customToolbarContext = new CustomToolbarContext(this, this::BackToPreviousActivity);
        customToolbarContext.SetTitle("Quên mật khẩu");
        etConfirmPassword.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void GetValueFromIntent(){
        Intent intent = getIntent();
        reason = intent.getStringExtra(Symbol.VERRIFY_FORGET.GetValue());
        if(reason.equalsIgnoreCase(Symbol.VERIFY_FORGET_BY_EMAIL.GetValue())){
            user.setEmail(intent.getStringExtra(Symbol.EMAIL.GetValue()));
            LoadingVerifyEmail verifyEmail = new LoadingVerifyEmail(user.getEmail());
            verifyEmail.start();
        }
        else
        {
            user.setPhoneNumber(intent.getStringExtra(Symbol.PHONE.GetValue()));
            tvUsername.setText(user.getPhoneNumber());
            firebaseDatabaseHandler.RegisterDataListener();
            progressBarManager.ShowProgressBar("Loading");
        }
    }

    @Override
    public void HandleDataModel(UserModel data) {
        progressBarManager.HideProgressBar();
        btnResetNewPassword.setEnabled(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void HandleDataSnapShot(DataSnapshot dataSnapshot) {
        for(DataSnapshot data : dataSnapshot.child(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE.GetValue()).getChildren()){
            UserModel model = data.getValue(UserModel.class);
            if (model != null && model.getPhone().equalsIgnoreCase(user.getPhoneNumber())){
                user.setUserId(data.getKey());
                updateUserAPI = new UpdateUserAPI(user.getUserId(), getString(R.string.public_key),this);
                firebaseDatabaseHandler.UnregisterValueListener(null);
                break;
            }
        }
    }

    @Override
    public void HandlerDatabaseError(DatabaseError databaseError) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void VerifyResetPasswordEvent(View view){
        progressBarManager.ShowProgressBar("Loading...");

        String password = etPassword.getText().toString();
        String confirmPass = etConfirmPassword.getText().toString();

        Response response = CheckPassword(password, confirmPass);

        if(response.GetStatus()){
            secretKey1 = SecurityUtils.generateAESKey();
            secretKey2 = SecurityUtils.generateAESKey();

            String encryptPasswordByAES = SecurityUtils.EncryptStringBySecretKey(secretKey1, getString(R.string.share_key), password);
            String encryptAESPassBySecondSecretKey = SecurityUtils.encryptAES(secretKey2, encryptPasswordByAES);

            updateUserAPI.setPin(encryptAESPassBySecondSecretKey);
            updateUserAPI.UpdateUser(secretKey1, secretKey2);
        } else {
            progressBarManager.HideProgressBar();
        }
    }

    private Response CheckPassword(String pass, String confirmPass){
        if(TextUtils.isEmpty(pass)){
            etPassword.setError("Hãy nhập mật khẩu mới");
            return new Response(ErrorCode.EMPTY_PIN);
        }
        else if(TextUtils.isEmpty(confirmPass)){
            etConfirmPassword.setError("Hãy nhập lại mật khẩu mới");
            return new Response(ErrorCode.EMPTY_CONFIRM_PIN);
        }
        else if(!CheckInputField.PasswordIsValid(pass)){
            etPassword.setError("Mật khẩu không hợp lệ");
            return new Response(ErrorCode.VALIDATE_PIN_INVALID);
        }
        else if(!pass.equalsIgnoreCase(confirmPass)){
            etConfirmPassword.setError("Mật khẩu không trùng");
            return new Response(ErrorCode.UNVALID_PIN_AND_CONFIRM_PIN);
        }

        return new Response(ErrorCode.SUCCESS);
    }

    @Override
    public void BackToPreviousActivity() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void UpdateSuccess() {
        UserLoginRequest request = new UserLoginRequest(user.getPhoneNumber(), etPassword.getText().toString(),
                SecurityUtils.EncodeStringBase64(secretKey1.getEncoded()), SecurityUtils.EncodeStringBase64(secretKey2.getEncoded()));
        userLoginAPI = new UserLoginAPI(getString(R.string.share_key), getString(R.string.public_key), request, this);
        userLoginAPI.StartLoginAPI();
    }

    @Override
    public void UpdateFail() {
        ShowErrorText("Máy chủ không phản hồi");
        progressBarManager.HideProgressBar();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void LoginSucess(User user, String customToken, String secretKey1, String secretKey2) {
        this.user = user;
        this.secretKeyString1 = SecurityUtils.DecryptAESbyTwoSecretKey(this.secretKey2, this.secretKey1, secretKey1);
        this.secretKeyString2 = SecurityUtils.DecryptAESbyTwoSecretKey(this.secretKey2, this.secretKey1, secretKey2);
        if (auth.getCurrentUser() != null){
            auth.signOut();
        }
        auth.signInWithCustomToken(customToken);
        GetBalanceAPI getBalanceAPI = new GetBalanceAPI(SecurityUtils.generatePublicKey(getString(R.string.public_key)), user.getUserId(), this);
        getBalanceAPI.GetBalance(secretKeyString1, secretKeyString2);
    }

    @Override
    public void LoginFail(int code) {
        progressBarManager.HideProgressBar();
    }

    void ShowErrorText(String mess){
        tvError.setText(mess);
        tvError.setVisibility(View.VISIBLE);
    }

    @Override
    public void GetBalanceResponse(long balance) {
        Intent intent = new Intent();
        intent.putExtra(Symbol.USER.GetValue(), GsonUtils.toJsonString(user));
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        intent.putExtra(Symbol.SECRET_KEY_01.GetValue(), secretKeyString1);
        intent.putExtra(Symbol.SECRET_KEY_02.GetValue(), secretKeyString2);
        setResult(RESULT_OK, intent);
        finish();
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
}
