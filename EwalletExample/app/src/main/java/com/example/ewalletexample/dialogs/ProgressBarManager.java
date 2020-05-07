package com.example.ewalletexample.dialogs;

import android.view.View;
import android.widget.TextView;

import com.example.ewalletexample.R;

import java.util.List;

public class ProgressBarManager {
    private View progressLayout;
    private TextView progressText;
    private View[] listButtons;

    public ProgressBarManager(View progressLayout,View... buttons){
        this.progressLayout = progressLayout;
        progressText = progressLayout.findViewById(R.id.pbText);
        listButtons = buttons;
    }

    public void ShowProgressBar(String text){
        progressText.setText(text);
        progressLayout.setVisibility(View.VISIBLE);
        if(listButtons != null)
        {
            for (View btn : listButtons){
                btn.setEnabled(false);
            }
        }
    }

    public void HideProgressBar(){
        progressLayout.setVisibility(View.GONE);
        if(listButtons != null)
        {
            for (View btn : listButtons){
                btn.setEnabled(true);
            }
        }
    }

    public boolean IsVivisible(){
        return progressLayout.getVisibility() == View.VISIBLE;
    }

    public void SetMessage(String text){
        progressText.setText(text);
    }
}
