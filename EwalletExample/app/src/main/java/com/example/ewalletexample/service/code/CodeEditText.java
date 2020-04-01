package com.example.ewalletexample.service.code;

import android.util.Log;
import android.widget.EditText;;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class CodeEditText {
    private ArrayList<EditTextCodeChangeListener> listEditCodeChangeListeners;

//    private ArrayList<EditText> listEditTexts;
    private int lengthText;

    public CodeEditText(int length, @NonNull EditText... editText){
        listEditCodeChangeListeners = new ArrayList<>();
//        listEditTexts = new ArrayList<>();
        lengthText = length;
        InitializeListEditText(editText);
    }

    void InitializeListEditText(EditText... editText){
        EditText editText1 = editText[0];

        for (int i = 1; i < editText.length; i++){
            if(i == editText.length - 1){
                InitSingleEditCodeChangeListener(editText[i]);
            }

            InitTwoEditCodeChangeListener(editText1, editText[i]);
            editText1 = editText[i];
        }
    }

    void InitTwoEditCodeChangeListener(EditText editText1, EditText editText2){
        Log.d("TAG", "InitTwoEditCodeChangeListener: ");
        EditTextCodeChangeListener newCodeChangeListener = new EditTextCodeChangeListener(editText1, editText2, lengthText);
        listEditCodeChangeListeners.add(newCodeChangeListener);
    }

    void InitSingleEditCodeChangeListener(EditText editText1){
        EditTextCodeChangeListener newCodeChangeListener = new EditTextCodeChangeListener(editText1, lengthText);
        listEditCodeChangeListeners.add(newCodeChangeListener);
    }
}
