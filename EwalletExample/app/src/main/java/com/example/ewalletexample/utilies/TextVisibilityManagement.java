package com.example.ewalletexample.utilies;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TextVisibilityManagement {
    private List<View> listViewItems;

    public TextVisibilityManagement(View... listItems){
        listViewItems = new ArrayList<>();

        for (View item : listItems){
            listViewItems.add(item);
        }
    }

    public boolean IsVisible(){
        if (listViewItems.size() > 0){
            return listViewItems.get(0).getVisibility() == View.VISIBLE;
        }

        return false;
    }

    public void ShowText(){
        for (View item : listViewItems){
            item.setVisibility(View.VISIBLE);
        }
    }

    public void HideText(){
        for (View item : listViewItems){
            item.setVisibility(View.GONE);
        }
    }
}
