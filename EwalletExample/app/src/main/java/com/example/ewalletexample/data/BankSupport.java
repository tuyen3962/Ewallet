package com.example.ewalletexample.data;

public class BankSupport {
    private String bankCode;
    private String bankName;
    private String bankImage;

    public BankSupport(String bankCode, String bankName, String bankImage) {
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.bankImage = bankImage;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankImage() {
        return bankImage;
    }

    public void setBankImage(String bankImage) {
        this.bankImage = bankImage;
    }
}
