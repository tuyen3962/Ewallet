package com.example.ewalletexample.service.code;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class EditTextCodeChangeListener {

    EditText eText1, eText2;
    private String value;
    private int limitLength;

    public EditTextCodeChangeListener(EditText _eText1, EditText _eText2, int length){
        eText1 = _eText1;
        eText2 = _eText2;
        limitLength = length;

        eText1.addTextChangedListener(textWatcher);

        eText2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_DEL){
                    if(eText2.getText().length() == 0){
                        eText1.requestFocus();
                    }
                }

                return false;
            }
        });
    }

    public EditTextCodeChangeListener(EditText _eText1, int length){
        eText1 = _eText1;
        limitLength = length;

        eText1.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(charSequence.length() == limitLength){
                if(eText2 != null){
                    eText2.requestFocus();
                }
                value = String.valueOf(charSequence.charAt(0));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() > limitLength)
            {
                value = GetAnotherValueBaseCurrentValue(editable);
                editable.clear();
                editable.append(value);

                if(eText2 != null)
                {
                    eText2.requestFocus();
                }
            }
        }
    };

    private String GetAnotherValueBaseCurrentValue(Editable editable){
        String end = String.valueOf(editable.charAt(editable.length() - 1));
        String start = String.valueOf(editable.charAt(0));

        if(end.equalsIgnoreCase(value)){
            return start;
        }
        else
        {
            return end;
        }
    }
}
