package com.example.ewalletexample.Symbol;

import android.graphics.Color;

public enum BankSupport {
    MSB("msb","MSB","/bank_images/msb.jpg", Color.RED),
    VIET_TIN_BANK("vtb","Viet Tin Bank","/bank_images/viettin.jpg", Color.BLUE),
    SACOM_BANK("scb","Sacombank", "/bank_images/scb.jpg", Color.GREEN);

    private String bankCode;
    private String bankName;
    private String bankLinkImage;
    private int backgroundColor;

    BankSupport(String bankCode, String bankName, String bankLinkImage, int backgroundColor){
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.bankLinkImage = bankLinkImage;
        this.backgroundColor = backgroundColor;
    }

    public String getBankCode(){
        return bankCode;
    }

    public String getBankName(){
        return bankName;
    }

    public String getBankLinkImage(){
        return bankLinkImage;
    }

    public int GetBackgroundColorCode(){
        return backgroundColor;
    }

    public static BankSupport FindBankSupport(String bankCode){
        for (BankSupport bankSupport : BankSupport.values()){
            if (bankSupport.getBankCode().equalsIgnoreCase(bankCode)){
                return bankSupport;
            }
        }

        return null;
    }
}
