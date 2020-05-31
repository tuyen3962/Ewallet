package com.example.ewalletexample.service.notification;

public class BankServiceTemplate {
    private String bankCode;
    private String first6CardNo;
    private String last4CardNo;

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getFirst6CardNo() {
        return first6CardNo;
    }

    public void setFirst6CardNo(String first6CardNo) {
        this.first6CardNo = first6CardNo;
    }

    public String getLast4CardNo() {
        return last4CardNo;
    }

    public void setLast4CardNo(String last4CardNo) {
        this.last4CardNo = last4CardNo;
    }
}
