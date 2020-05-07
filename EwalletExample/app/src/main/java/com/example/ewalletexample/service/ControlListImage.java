package com.example.ewalletexample.service;

import android.util.Log;
import android.widget.ImageView;

import com.example.ewalletexample.R;

import java.util.List;

public class ControlListImage {
    private int pointLight, pointDark;
    private List<ImageView> listImage;
    private int index;
    private String textByImage;
    private boolean canSetText;

    public ControlListImage(List<ImageView> imageViews){
        listImage = imageViews;
        index = 0;
        textByImage = "";
        pointLight = R.drawable.ic_action_point_light;
        pointDark = R.drawable.ic_action_point_dark;
    }

    public boolean CheckIncreaseIndex(String text){
        if (CheckCurrentIndex(index)) {
            SetCurrentImageIndexWithBitmap(pointDark);
            IncreaseIndex(text);
            Log.d("TAG", "CheckIncreaseIndex: " + textByImage + " " + index + " " + listImage.size());
            return true;
        }
        return false;
    }

    public void SetCanEditText(boolean isCan){
        canSetText = isCan;
    }

    public boolean CanEditText(){
        return canSetText;
    }

    public boolean CheckDecreaseIndex(){
        if (CheckCurrentIndex(index - 1)) {
            DecreaseIndex();
            SetCurrentImageIndexWithBitmap(pointLight);
            return true;
        }

        return false;
    }

    public void SetListImageViewWithBitmap(){
        for (ImageView img : listImage){
            img.setImageResource(pointLight);
        }
    }

    public void SetCurrentImageIndexWithBitmap(int bitmapId){
        listImage.get(index).setImageResource(bitmapId);
    }

    public boolean CheckCurrentIndex(int index){
        if (index >= listImage.size() || index < 0)
            return false;

        return true;
    }

    private void IncreaseIndex(String text){
        index += 1;
        textByImage += text;
    }

    private void DecreaseIndex(){
        index -= 1;
        textByImage = textByImage.substring(0, index);
    }

    public String getTextByImage(){
        return textByImage;
    }

    public void Clear(){
        textByImage = "";
        for (ImageView img : listImage){
            img.setImageResource(pointLight);
        }
    }
}
