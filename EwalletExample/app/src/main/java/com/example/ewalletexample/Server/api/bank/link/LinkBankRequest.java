package com.example.ewalletexample.Server.api.bank.link;

public class LinkBankRequest {
    public String userid;
    public String bankcode;
    public String cardno;
    public String fullname;

    public String cmnd;
    public String phone;
    public String key;
    public String secondKey;

    public String GetString(){
        return userid + bankcode + cardno + fullname + cmnd + phone;
    }
}
