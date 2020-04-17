package com.example.ewalletexample.service.mobilecard;

import android.graphics.Color;

import com.example.ewalletexample.R;

public enum  MobileCardOperator {
    VIETTEL("VT", "Viettle", "mobile_card_images/viettel.png", R.color.SoftGreen),
    MOBIPHONE("MOBI","Mobiphone","mobile_card_images/mobiphone.jpg", R.color.SoftBlue),
    GMOBILE("GM", "Gmobile", "mobile_card_images/gmobile.png", R.color.Yellow),
    VINAPHONE("VINA","Vinaphone", "mobile_card_images/vinaphone.jpg", R.color.Blue),
    VIETNAMOBILE("VM", "Vietnamobile", "mobile_card_images/vietnammobile.jpg", R.color.Grey);

    private String mobileCode;
    private String mobileCardName;
    private String mobileImageLink;
    private int colorId;

    MobileCardOperator(String mobileCode, String mobileCardName, String mobileImageLink, int colorId){
        this.mobileCode = mobileCode;
        this.mobileCardName = mobileCardName;
        this.mobileImageLink = mobileImageLink;
        this.colorId = colorId;
    }

    public String GetMobileCode(){
        return this.mobileCode;
    }

    public String GetMobileCardName(){
        return this.mobileCardName;
    }

    public String GetMobileLinkImage(){
        return mobileImageLink;
    }

    public int GetColorID(){
        return this.colorId;
    }

    public static MobileCardOperator FindOperator(String mobileCode){
        for (MobileCardOperator operator : MobileCardOperator.values())
            if (operator.GetMobileCode().equalsIgnoreCase(mobileCode)) {
                return operator;
            }

        return null;
    }
}
