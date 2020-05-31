package com.example.ewalletexample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.service.AnimationManager;
import com.example.ewalletexample.service.CheckInputField;
import com.example.ewalletexample.service.editText.ErrorTextWatcher;
import com.example.ewalletexample.service.toolbar.CustomToolbarContext;
import com.example.ewalletexample.service.toolbar.ToolbarEvent;
import com.example.ewalletexample.utilies.Utilies;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

public class RegisterByPhone extends AppCompatActivity implements ToolbarEvent {
    TextInputLayout inputLayoutFullName, inputPassword, inputConfirmPassword;
    TextInputEditText etFullName, etPassword, etConfirmPassword;
    MaterialTextView tvPhone, tvError;
    AnimationManager animationManager;
    CustomToolbarContext customToolbarContext;
    Button btnRegister;
    String phone;

    TextWatcher checkEditText = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!tvError.getText().toString().isEmpty()){
                tvError.setText("");
            }

            if (etFullName.length() > 0 && etPassword.length() == 6 && etConfirmPassword.length() == 6){
                Utilies.SetEneableButton(btnRegister, true);
            } else {
                Utilies.SetEneableButton(btnRegister, false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_by_phone);

        Intialize();
        GetValueFromIntent();
    }

    void Intialize(){
        inputLayoutFullName = findViewById(R.id.input_layout_fullname);
        animationManager = new AnimationManager(this);
        etFullName = findViewById(R.id.etFullname);
        tvPhone = findViewById(R.id.tvPhone);
        tvError = findViewById(R.id.tvError);
        inputPassword = findViewById(R.id.input_layout_password);
        inputConfirmPassword = findViewById(R.id.input_layout_confirm_password);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        customToolbarContext = new CustomToolbarContext(this, this::BackToPreviousActivity);
        customToolbarContext.SetTitle("Nhập thông tin");
        btnRegister.setEnabled(false);
        etFullName.addTextChangedListener(checkEditText);
        etPassword.addTextChangedListener(checkEditText);
        etConfirmPassword.addTextChangedListener(checkEditText);
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        phone = intent.getStringExtra(Symbol.PHONE.GetValue());
        tvPhone.setText(phone);
    }

    public void RegisterEvent(View view){
        Utilies.HideKeyboard(this);
        String fullname = etFullName.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPass = etConfirmPassword.getText().toString();

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
            ShowErrorText(ErrorCode.EMPTY_PIN.GetMessage());
            return false;
        } else if (TextUtils.isEmpty(confirmPass)){
            ShowErrorText(ErrorCode.EMPTY_PIN.GetMessage());
            return false;
        }
        else if(!CheckInputField.PasswordIsValid(password)){
            ShowErrorText(ErrorCode.VALIDATE_PIN_INVALID.GetMessage());
            return false;
        } else if(!CheckInputField.PasswordIsValid(confirmPass)){
            ShowErrorText(ErrorCode.VALIDATE_PIN_INVALID.GetMessage());
            return false;
        }
        else if(password.equalsIgnoreCase(confirmPass)){
            return true;
        }
        else{
            ShowErrorText(ErrorCode.UNVALID_PIN_AND_CONFIRM_PIN.GetMessage());
            return false;
        }
    }

    private void SwitchToVerifyByPhoneActivity(String fullName, String password){
        Intent intent = new Intent(RegisterByPhone.this, VerifyByPhoneActivity.class);
        intent.putExtra(Symbol.REASION_VERIFY.GetValue(), Symbol.REASON_VERIFY_FOR_REGISTER.GetValue());
        intent.putExtra(Symbol.FULLNAME.GetValue(), fullName);
        intent.putExtra(Symbol.PASSWORD.GetValue(), password);
        intent.putExtra(Symbol.PHONE.GetValue(), phone);
        startActivityForResult(intent, RequestCode.UPDATE_REGISTER);
    }

    private void ClearSpecificEditText(){
        etPassword.setText("");
        etConfirmPassword.setText("");
    }

    void ShowErrorText(String text){
        tvError.setText(text);
    }

    @Override
    public void BackToPreviousActivity() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.UPDATE_REGISTER && resultCode == RESULT_CANCELED){
            finish();
        }
    }
}
