package com.example.ewalletexample.service.editText;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ErrorTextWatcher {
    private TextInputLayout inputLayout;
    private TextInputEditText inputEditText;

    public ErrorTextWatcher(TextInputLayout inputLayout, TextInputEditText inputEditText){
        this.inputLayout = inputLayout;
        this.inputEditText = inputEditText;

        AddTextWatcherClearErrorText();
    }

    private void AddTextWatcherClearErrorText(){
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(inputLayout.isErrorEnabled()){
                    inputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
