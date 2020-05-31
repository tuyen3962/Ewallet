package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.CheckInputField;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.HandleDataFromFirebaseDatabase;
import com.example.ewalletexample.service.toolbar.CustomToolbarContext;
import com.example.ewalletexample.service.toolbar.ToolbarEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VerifyUserForForget extends AppCompatActivity implements HandleDataFromFirebaseDatabase<UserModel>, ToolbarEvent {

    FirebaseAuth auth;
    TextView tvChangeTypeVerify;
    TextInputEditText etDetail;
    CustomToolbarContext customToolbarContext;
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    boolean verifyAccountByPhone;
    String userid;
    Button btnVerifyAccount;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (etDetail.getError() != null && etDetail.getError().length() > 0){
                etDetail.setError("");
            }

            if (s.length() == 10){
                SetEnable(btnVerifyAccount, true);
            } else {
                SetEnable(btnVerifyAccount, false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_user_for_forget);
        Initialize();
    }

    void Initialize(){
        verifyAccountByPhone = true;
        etDetail = findViewById(R.id.etDetail);
        tvChangeTypeVerify = findViewById(R.id.tvChangeTypeVerify);
        auth = FirebaseAuth.getInstance();
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference(), this);
        btnVerifyAccount = findViewById(R.id.btnVerifyAccount);
        etDetail.addTextChangedListener(textWatcher);
        btnVerifyAccount.setEnabled(false);
        customToolbarContext = new CustomToolbarContext(this, this::BackToPreviousActivity);
        customToolbarContext.SetTitle("Quên mật khẩu");
    }

    public void VerifyAccountEvent(View view){
        String detail = etDetail.getText().toString();

        if(verifyAccountByPhone){
            VerifyAccountByPhone(detail);
        }else {
            VerifyAccountByEmail(detail);
        }
    }

    private void VerifyAccountByPhone(String phone){
        if(!CheckInputField.PhoneNumberIsValid(phone)){
            etDetail.setError("Số điện thoại không hợp lệ");
            return;
        }
        firebaseDatabaseHandler.RegisterDataListener();
    }

    @Override
    public void HandleDataModel(UserModel model){
        if(model != null){
            Intent intent = new Intent(VerifyUserForForget.this, VerifyByPhoneActivity.class);
            intent.putExtra(Symbol.REASION_VERIFY.GetValue(), Symbol.REASON_VERIFY_FOR_FORGET.GetValue());
            intent.putExtra(Symbol.VERRIFY_FORGET.GetValue(), Symbol.VERIFY_FORGET_BY_PHONE.GetValue());
            intent.putExtra(Symbol.PHONE.GetValue(), model.getPhone());
            intent.putExtra(Symbol.USER_ID.GetValue(), userid);
            startActivityForResult(intent, RequestCode.RESET_PASSWORD);
        }
        else{
            etDetail.setError("Khong ton tai so dien thoai");
        }
    }

    @Override
    public void HandleDataSnapShot(DataSnapshot dataSnapshot) {
        for(DataSnapshot data : dataSnapshot.child(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE.GetValue()).getChildren()){
            UserModel model = data.getValue(UserModel.class);
            if (model.getPhone().equalsIgnoreCase(etDetail.getText().toString())){
                userid = data.getKey();
                firebaseDatabaseHandler.UnregisterValueListener(model);
                return;
            }
        }

        firebaseDatabaseHandler.UnregisterValueListener(null);
    }

    @Override
    public void HandlerDatabaseError(DatabaseError databaseError) {

    }

    private void VerifyAccountByEmail(String email){
        SendLinkToEmailForReset(email);
    }

    private void SendLinkToEmailForReset(final String email){
        auth.createUserWithEmailAndPassword(email, "123456")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), "Registration successful. Please check your email for verification.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(VerifyUserForForget.this, ResetPassword.class);
                                        intent.putExtra(Symbol.VERRIFY_FORGET.GetValue(), Symbol.VERIFY_FORGET_BY_EMAIL.GetValue());
                                        intent.putExtra(Symbol.EMAIL.GetValue(), email);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void SetEnable(Button button, boolean isEnabled){
        if (button.isEnabled() == !isEnabled){
            button.setEnabled(isEnabled);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.RESET_PASSWORD){
            setResult(resultCode, data);
            finish();
        }
    }

    @Override
    public void BackToPreviousActivity() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
