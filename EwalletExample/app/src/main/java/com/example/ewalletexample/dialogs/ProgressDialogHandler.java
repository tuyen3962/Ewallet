package com.example.ewalletexample.dialogs;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogHandler {
    private ProgressDialog progressDialog;

    public ProgressDialogHandler(Context applicationContext){
        progressDialog = new ProgressDialog(applicationContext);
    }

    public void ShowProgressDialogWithTitle(String title){
        progressDialog.setTitle(title);
        progressDialog.show();
    }

    public void HideProgressDialog(){
        progressDialog.dismiss();
    }

    public void SetTextForDialog(String text){
        progressDialog.setMessage(text);
    }
}
