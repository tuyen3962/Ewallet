package com.example.ewalletexample.service;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;


public class TextBalanceFormat {
    private EditText etBalance;
    private TextView tvBalance;
    private boolean isTextView;
    private int indexAddDot;
    private int startIndexAddDot = 3;
    private int indexIncrementAddDot = 4;
    private String value;

    private TextWatcher textBalanceWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable text) {
            if(text.length() > 0){
                if(text.length() > indexAddDot){
                    value = AddDotInIndex(text.toString());
                    Log.d("TAG", "afterTextChanged: " + text.toString() + " " + value);
//                    IncreaseIndex();
                }
                else if(text.length() > startIndexAddDot && text.length() == GetPreviousIndex()){
//                    value = RemoveDotInIndex(text.toString(), GetPreviousIndex());
                    Log.d("TAG", "afterTextChanged: " + text.toString());
//                    DecreaseIndex();
                }
            }
        }
    };

    public TextBalanceFormat(boolean isTextView, TextView tvBalance){
        this.tvBalance = tvBalance;
        this.isTextView = isTextView;

        indexAddDot = startIndexAddDot;
        value = "";
        tvBalance.addTextChangedListener(textBalanceWatcher);
    }

    public TextBalanceFormat(boolean isTextView, EditText etBalance){
        this.isTextView = isTextView;
        this.etBalance = etBalance;

        indexAddDot = startIndexAddDot;
        value = "";
        etBalance.addTextChangedListener(textBalanceWatcher);
    }

    public void IncreaseIndex(){
        indexAddDot += indexIncrementAddDot;
    }

    private void DecreaseIndex(){
        if (indexAddDot == startIndexAddDot){
            return;
        }

        indexAddDot -= indexIncrementAddDot;
    }

    private int GetPreviousIndex(){
        if (indexAddDot == startIndexAddDot){
            return indexAddDot;
        }

        return indexAddDot - indexIncrementAddDot;
    }

    private String AddDotInIndex(String text){
        String headText = text.substring(0,1);
        String lastText = text.substring(1);
        String textFixed = headText + "." + lastText;
        return textFixed;
    }

    private String RemoveDotInIndex(String text, int index){
        return text.substring(0, index-1);
    }

    public String GetTextBalance(){
        String balance;
        if (isTextView){
            balance = tvBalance.getText().toString();
        }
        else {
            balance = etBalance.getText().toString();
        }

        return formatBalanceText(balance);
    }

    private String formatBalanceText(String balance){
        return balance.replaceAll(".","");
    }
}
