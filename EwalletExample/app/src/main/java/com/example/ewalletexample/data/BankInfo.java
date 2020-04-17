package com.example.ewalletexample.data;

import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

public class BankInfo {
    private String cardName;
    private String bankCode;
    private String f6CardNo;
    private String l4CardNo;

    public BankInfo(String cardName, String bankCode, String f6CardNo, String l4CardNo) {
        this.cardName = cardName;
        this.bankCode = bankCode;
        this.f6CardNo = f6CardNo;
        this.l4CardNo = l4CardNo;
    }

    public BankInfo(String jsonData) throws JSONException {
        ReadJsonData(jsonData);
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

    public String ExchangeToJsonData() {
        String[] arr = new String[]{"cardname:"+cardName,"bankcode:"+bankCode,"f6cardno:"+f6CardNo,"l4cardno:"+l4CardNo};

        try {
            return HandlerJsonData.ExchangeToJsonString(arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void ReadJsonData(String jsonData) throws JSONException{
        JSONObject json = new JSONObject(jsonData);

        setCardName(json.getString("cardname"));
        setBankCode(json.getString("bankcode"));
        setF6CardNo(json.getString("f6cardno"));
        setL4CardNo(json.getString("l4cardno"));
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
