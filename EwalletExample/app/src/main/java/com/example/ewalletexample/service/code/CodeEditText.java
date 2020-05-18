package com.example.ewalletexample.service.code;

import android.util.Log;
import android.widget.EditText;;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class CodeEditText {
    private ArrayList<EditTextCodeChangeListener> listEditCodeChangeListeners;
    private CheckOTPFunction function;
    private ArrayList<EditText> listEditTexts;
    private int lengthText;

    public CodeEditText(int length, CheckOTPFunction function, @NonNull EditText... editText){
        this.function = function;
        listEditCodeChangeListeners = new ArrayList<>();
        listEditTexts = new ArrayList<>();
        lengthText = length;
        InitializeListEditText(editText);
    }

    public CodeEditText(int length, @NonNull EditText... editText){
        listEditCodeChangeListeners = new ArrayList<>();
        listEditTexts = new ArrayList<>();
        lengthText = length;
        InitializeListEditText(editText);
    }

    void InitializeListEditText(EditText... editText){
        EditText editText1 = editText[0];
        listEditTexts.add(editText1);
        for (int i = 1; i < editText.length; i++){
            listEditTexts.add(editText[i]);
            if(i == editText.length - 1){
                InitSingleEditCodeChangeListener(editText[i]);
            }

            InitTwoEditCodeChangeListener(editText1, editText[i]);
            editText1 = editText[i];
        }
    }

    void InitTwoEditCodeChangeListener(EditText editText1, EditText editText2){
        EditTextCodeChangeListener newCodeChangeListener = new EditTextCodeChangeListener(this, editText1, editText2, lengthText);
        listEditCodeChangeListeners.add(newCodeChangeListener);
    }

    void InitSingleEditCodeChangeListener(EditText editText1){
        EditTextCodeChangeListener newCodeChangeListener = new EditTextCodeChangeListener(this, editText1, lengthText);
        listEditCodeChangeListeners.add(newCodeChangeListener);
    }

    public String GetCombineText(){
        String result = "";

        for (EditText text : listEditTexts){
            result += text.getText().toString();
        }

        return result;
    }

    public void CheckIsFull(){
        if (function != null){
            if(GetCombineText().length() == listEditTexts.size()){
                function.IsFull();
                return;
            }

            function.NotFull();
        }
    }
}
