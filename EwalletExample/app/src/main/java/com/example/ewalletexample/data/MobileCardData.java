package com.example.ewalletexample.data;

public class MobileCardData {
    private String mobileCode;
    private String cardNumber;
    private String seriNumber;

    public MobileCardData() {
    }

    public MobileCardData(String mobileCode, String cardNumber, String seriNumber) {
        this.mobileCode = mobileCode;
        this.cardNumber = cardNumber;
        this.seriNumber = seriNumber;
    }

    public String getMobileCode() {
        return mobileCode;
    }

    public void setMobileCode(String mobileCode) {
        this.mobileCode = mobileCode;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getSeriNumber() {
        return seriNumber;
    }

    public void setSeriNumber(String seriNumber) {
        this.seriNumber = seriNumber;
    }
}
