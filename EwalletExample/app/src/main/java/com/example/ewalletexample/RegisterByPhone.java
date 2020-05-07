package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.service.AnimationManager;
import com.example.ewalletexample.service.CheckInputField;
import com.example.ewalletexample.service.editText.ErrorTextWatcher;
import com.example.ewalletexample.utilies.Utilies;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

public class RegisterByPhone extends AppCompatActivity {
    private static final String PASSWORD_TAG = "PASSWORD", CONFIRM_PASS_TAG = "CONFIRM_PASSWORD";
    TextInputLayout inputLayoutFullName;
    TextInputEditText etFullName;
    MaterialTextView tvPhone, tvError;
    PasswordFieldFragment passwordField, confirmPasswordField, currentPasswordField;
    NumberKeyboard keyboard;
    AnimationManager animationManager;
//    ErrorTextWatcher inputFullName, inputPassword, inputConfirmPassword;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_by_phone);

        Intialize();
        if(savedInstanceState == null) {
            passwordField = new PasswordFieldFragment("Nhập mật khẩu");
            confirmPasswordField = new PasswordFieldFragment("Nhập lại mật khẩu");
            Utilies.AddFragmentIntoActivity(this, R.id.passwordLayout, passwordField, PASSWORD_TAG);
            Utilies.AddFragmentIntoActivity(this, R.id.confirmPasswordLayout, confirmPasswordField, CONFIRM_PASS_TAG);
            passwordField.SetHintText("Nhập mật khẩu");
            confirmPasswordField.SetHintText("Nhập lại mật khẩu");
        }
        GetValueFromIntent();
    }

    void Intialize(){
        inputLayoutFullName = findViewById(R.id.input_layout_fullname);
        animationManager = new AnimationManager(this);
        etFullName = findViewById(R.id.etFullname);
        tvPhone = findViewById(R.id.tvPhone);
        tvError = findViewById(R.id.tvError);
        keyboard = findViewById(R.id.keyboard);
        keyboard.setVisibility(View.GONE);

        keyboard.setListener(new NumberKeyboardListener() {
            @Override
            public void onNumberClicked(int i) {
                if(currentPasswordField != null){
                    currentPasswordField.CheckIncreaseIndex(String.valueOf(i));
                }
            }

            @Override
            public void onLeftAuxButtonClicked() {
                keyboard.setVisibility(View.GONE);
                currentPasswordField.ShowDefaultPassword();
            }

            @Override
            public void onRightAuxButtonClicked() {
                if(currentPasswordField != null){
                    currentPasswordField.CheckDecreaseIndex();
                }
            }
        });
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
        animationManager.HideAnimationView(keyboard);
        startActivity(new Intent(RegisterByPhone.this, EnterPhoneStartAppActivity.class));
    }

    public void RegisterEvent(View view){
        animationManager.HideAnimationView(keyboard);
        Utilies.HideKeyboard(this);
        String fullname = etFullName.getText().toString();
        String password = passwordField.getTextByImage();
        String confirmPass = confirmPasswordField.getTextByImage();

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
            tvError.setText(ErrorCode.EMPTY_PIN.GetMessage());
            return false;
        } else if (TextUtils.isEmpty(confirmPass)){
            tvError.setText(ErrorCode.EMPTY_PIN.GetMessage());
            return false;
        }
        else if(!CheckInputField.PasswordIsValid(password)){
            tvError.setText(ErrorCode.VALIDATE_PIN_INVALID.GetMessage());
            return false;
        } else if(!CheckInputField.PasswordIsValid(confirmPass)){
            tvError.setText(ErrorCode.VALIDATE_PIN_INVALID.GetMessage());
            return false;
        }
        else if(password.equalsIgnoreCase(confirmPass)){
            return true;
        }
        else{
            tvError.setText(ErrorCode.UNVALID_PIN_AND_CONFIRM_PIN.GetMessage());
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
        passwordField.ClearText();
        confirmPasswordField.ClearText();
    }

    public void ShowNumberKeyBoard(int viewId) {
        tvError.setText("");
        if(viewId == passwordField.getId()){
            currentPasswordField = passwordField;
        } else {
            currentPasswordField = confirmPasswordField;
        }
        Utilies.HideKeyboard(this);
        etFullName.clearFocus();
        if(keyboard.getVisibility() != View.VISIBLE){
            animationManager.ShowAnimationView(keyboard);
        }
    }
}
