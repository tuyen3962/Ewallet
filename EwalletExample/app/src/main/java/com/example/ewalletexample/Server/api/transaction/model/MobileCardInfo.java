package com.example.ewalletexample.Server.api.transaction.model;

public class MobileCardInfo {
    private String cardType;
    private String cardNumber;
    private String seriNumber;
    private String amount;

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
