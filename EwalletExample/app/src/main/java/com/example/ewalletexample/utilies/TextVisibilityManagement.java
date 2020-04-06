package com.example.ewalletexample.utilies;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TextVisibilityManagement {
    private List<EditText> listEditTexts;
    private List<TextView> listTextViews;
    private boolean isTextview;

    public TextVisibilityManagement(boolean isTextview, EditText... listItems){
        listEditTexts = new ArrayList<>();
        this.isTextview = isTextview;

        for (EditText item : listItems){
            listEditTexts.add(item);
        }
    }

    public TextVisibilityManagement(boolean isTextview, TextView... listItems){
        listTextViews = new ArrayList<>();
        this.isTextview = isTextview;

        for (TextView item : listItems){
            listTextViews.add(item);
        }
    }

    public void ShowText(){
        if(isTextview){
            ShowListTextView();
        } else{
            ShowListEditText();
        }
    }

    private void ShowListTextView(){
        for (TextView textView : listTextViews){
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void ShowListEditText(){
        for (EditText editText : listEditTexts){
            editText.setVisibility(View.VISIBLE);
        }
    }

    public void HideText(){
        if(isTextview){
            HideListTextView();
        } else{
            HideListEditText();
        }
    }

    private void HideListTextView(){
        for (TextView textView : listTextViews){
            textView.setVisibility(View.GONE);
        }
    }

    private void HideListEditText(){
        for (EditText editText : listEditTexts){
            editText.setVisibility(View.GONE);
        }
    }
}
