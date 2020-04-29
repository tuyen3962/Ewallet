package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.service.CheckInputField;
import com.example.ewalletexample.service.editText.ErrorTextWatcher;
import com.example.ewalletexample.utilies.Utilies;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

public class RegisterByPhone extends AppCompatActivity {

    TextInputLayout inputLayoutFullName, inputLayoutPassword, inputLayoutConfirmPassword;
    TextInputEditText etFullName, etPassword, etConfirmPass;
    MaterialTextView tvPhone;
//    ErrorTextWatcher inputFullName, inputPassword, inputConfirmPassword;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_by_phone);

        Intialize();
        GetValueFromIntent();
    }

    void Intialize(){
        inputLayoutFullName = findViewById(R.id.input_layout_fullname);
        inputLayoutConfirmPassword = findViewById(R.id.input_layout_confirm_password);
        inputLayoutPassword = findViewById(R.id.input_layout_password);

        etFullName = findViewById(R.id.etFullname);
        tvPhone = findViewById(R.id.tvPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPass = findViewById(R.id.etConfirmPassword);
//        inputFullName = new ErrorTextWatcher(inputLayoutFullName, etFullName);
//        inputPassword = new ErrorTextWatcher(inputLayoutPassword, etPassword);
//        inputConfirmPassword = new ErrorTextWatcher(inputLayoutConfirmPassword, etConfirmPass);
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        phone = intent.getStringExtra(Symbol.PHONE.GetValue());
        tvPhone.setText(phone);
    }

    public void CancelEvent(View view){
        startActivity(new Intent(RegisterByPhone.this, EnterPhoneStartAppActivity.class));
    }

    public void RegisterEvent(View view){
        String fullname = etFullName.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPass = etConfirmPass.getText().toString();

        if(InputFieldIsValid(fullname, password, confirmPass)){
            SwitchToVerifyByPhoneActivity(fullname, password);
        } else {
            ClearSpecificEditText();
        }
    }

    private boolean InputFieldIsValid(String fullName, String password, String confirmPass){
        if(TextUtils.isEmpty(fullName)){
            inputLayoutFullName.setError(ErrorCode.VALIDATE_FULL_NAME_INVALID.GetMessage());
            return false;
        }

        return CheckPassword(password, confirmPass);
    }

    private boolean CheckPassword(String password, String confirmPass){
        if (TextUtils.isEmpty(password)){
            etPassword.setError(ErrorCode.EMPTY_PIN.GetMessage());
            return false;
        } else if (TextUtils.isEmpty(confirmPass)){
            etConfirmPass.setError(ErrorCode.EMPTY_PIN.GetMessage());
            return false;
        }
        else if(!CheckInputField.PasswordIsValid(password)){
            etPassword.setError(ErrorCode.VALIDATE_PIN_INVALID.GetMessage());
            return false;
        } else if(!CheckInputField.PasswordIsValid(confirmPass)){
            etConfirmPass.setError(ErrorCode.VALIDATE_PIN_INVALID.GetMessage());
            return false;
        }
        else if(password.equalsIgnoreCase(confirmPass)){
            return true;
        }
        else{
            etPassword.setError(ErrorCode.UNVALID_PIN_AND_CONFIRM_PIN.GetMessage());
            etConfirmPass.setError(ErrorCode.UNVALID_PIN_AND_CONFIRM_PIN.GetMessage());
            return false;
        }
    }

    private void SwitchToVerifyByPhoneActivity(String fullName, String password){
        Intent intent = new Intent(RegisterByPhone.this, VerifyByPhoneActivity.class);
        intent.putExtra(Symbol.REASION_VERIFY.GetValue(), Symbol.REASON_VERIFY_FOR_REGISTER.GetValue());
        intent.putExtra(Symbol.FULLNAME.GetValue(), fullName);
        intent.putExtra(Symbol.PASSWORD.GetValue(), password);
        intent.putExtra(Symbol.PHONE.GetValue(), phone);

        startActivity(intent);
    }

    private void ClearSpecificEditText(){
        etConfirmPass.setText("");
        etPassword.setText("");
    }
}
