package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.model.Response;
import com.example.ewalletexample.service.CheckInputField;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.HandleDataFromFirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterByPhone extends AppCompatActivity implements HandleDataFromFirebaseDatabase<UserModel> {

    EditText etFullname, etUserPhone, etPassword, etConfirmPass;
    TextView tvWarning;

    DatabaseReference mDatabase;
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(tvWarning.getVisibility() == View.VISIBLE){
                HideWarningTextview();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_by_phone);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(mDatabase, this);
        Intialize();
        AddTextWatcherIntoEditText();
    }

    void Intialize(){
        etFullname = findViewById(R.id.etFullName);
        etUserPhone = findViewById(R.id.etUserPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPass = findViewById(R.id.etConfirmPassword);
        tvWarning = findViewById(R.id.tvWarning);
    }

    void AddTextWatcherIntoEditText(){
        etUserPhone.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
        etConfirmPass.addTextChangedListener(textWatcher);
    }

    public void RegisterEvent(View view){
        Response response = InputFieldIsValid();

        if(response.GetStatus()){
            firebaseDatabaseHandler.RegisterDataListener();
        }
        else{
            ShowWarningText(response.GetMessage());
            ClearSpecificEditText();
        }

    }

    private Response InputFieldIsValid(){
        String fullName = etFullname.getText().toString();

        if(TextUtils.isEmpty(fullName)){
            return new Response(ErrorCode.VALIDATE_FULL_NAME_INVALID);
        }

        String phone = etUserPhone.getText().toString();

        if(TextUtils.isEmpty(phone) && !CheckInputField.PhoneNumberIsValid(phone)){
            return new Response(ErrorCode.VALIDATE_PHONE_INVALID);
        }

        Response response = CheckPassword();

        if(response.GetStatus()){
            return new Response(ErrorCode.SUCCESS);
        }

        return response;
    }

    private Response CheckPassword(){
        String pass = etPassword.getText().toString();
        String confirmPass = etConfirmPass.getText().toString();

        if (TextUtils.isEmpty(pass) || TextUtils.isEmpty(confirmPass)){
            return new Response(ErrorCode.EMPTY_PIN);
        }
        else if(!CheckInputField.PasswordIsValid(pass) || !CheckInputField.PasswordIsValid(confirmPass)){
            return new Response(ErrorCode.VALIDATE_PIN_INVALID);
        }
        else if(pass.equalsIgnoreCase(confirmPass)){
            return new Response(ErrorCode.SUCCESS);
        }
        else{
            return new Response(ErrorCode.UNVALID_PIN_AND_CONFIRM_PIN);
        }
    }

    private void SwitchToVerifyByPhoneActivity(){
        Intent intent = new Intent(RegisterByPhone.this, VerifyByPhoneActivity.class);
        intent.putExtra(Symbol.REASION_VERIFY.GetValue(), Symbol.REASON_VERIFY_FOR_REGISTER.GetValue());
        intent.putExtra(Symbol.FULLNAME.GetValue(), etFullname.getText().toString());
        intent.putExtra(Symbol.PASSWORD.GetValue(), etPassword.getText().toString());
        intent.putExtra(Symbol.PHONE.GetValue(), etUserPhone.getText().toString());

        startActivity(intent);
    }

    private void ShowWarningText(String message){
        tvWarning.setText(message);
        tvWarning.setVisibility(View.VISIBLE);
    }

    private void HideWarningTextview(){
        tvWarning.setVisibility(View.GONE);
    }

    private void ClearSpecificEditText(){
        etConfirmPass.setText("");
        etPassword.setText("");
    }

    @Override
    public void HandleDataModel(UserModel data) {
        if(data == null){
            SwitchToVerifyByPhoneActivity();
        }
        else{
            ShowWarningText(ErrorCode.VALIDATE_PHONE_DUPLICATE.GetMessage());
            ClearSpecificEditText();
        }
    }

    @Override
    public void HandleDataSnapShot(DataSnapshot dataSnapshot) {
        String phone = etUserPhone.getText().toString();
        if(dataSnapshot.child("users").getChildrenCount() > 0){
            firebaseDatabaseHandler.UnregisterValueListener(null);
            return;
        }
        else  {
            for(DataSnapshot data : dataSnapshot.child("users").getChildren()){
                UserModel model = data.getValue(UserModel.class);
                if(model.getPhone().equalsIgnoreCase(phone)){
                    firebaseDatabaseHandler.UnregisterValueListener(model);
                    return;
                }
            }
        }

        firebaseDatabaseHandler.UnregisterValueListener(null);
    }

    @Override
    public void HandlerDatabaseError(DatabaseError databaseError) {

    }
}
