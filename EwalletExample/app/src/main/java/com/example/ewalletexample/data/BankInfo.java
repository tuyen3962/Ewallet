package com.example.ewalletexample.data;

import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

public class BankInfo {
    private String cardName;
    private String bankCode;
    private String f6CardNo;
    private String l4CardNo;

    public BankInfo(){

    }

    public BankInfo(String cardName, String bankCode, String f6CardNo, String l4CardNo) {
        this.cardName = cardName;
        this.bankCode = bankCode;
        this.f6CardNo = f6CardNo;
        this.l4CardNo = l4CardNo;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getF6CardNo() {
        return f6CardNo;
    }

    public void setF6CardNo(String f6CardNo) {
        this.f6CardNo = f6CardNo;
    }

    public String getL4CardNo() {
        return l4CardNo;
    }

    public void setL4CardNo(String l4CardNo) {
        this.l4CardNo = l4CardNo;
    }

    public String getCardNumber(){
        String cardNumber = f6CardNo.substring(0, 4) + " " +
                f6CardNo.substring(4) + "** " +
                "****" +
                l4CardNo;

        return cardNumber;
    }

    public boolean equal(BankInfo info){
        if(info.getL4CardNo().equalsIgnoreCase(this.l4CardNo) && info.getF6CardNo().equalsIgnoreCase(this.f6CardNo) &&
                info.getBankCode().equalsIgnoreCase(this.bankCode) && info.getCardName().equalsIgnoreCase(this.cardName)){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "BankInfo{" +
                "cardName='" + cardName + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", f6CardNo='" + f6CardNo + '\'' +
                ", l4CardNo='" + l4CardNo + '\'' +
                '}';
    }
}
