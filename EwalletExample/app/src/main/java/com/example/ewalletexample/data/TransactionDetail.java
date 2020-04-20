package com.example.ewalletexample.data;

import android.util.Log;

import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.utilies.Date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TransactionDetail {
    private long transactionid;
    private long orderid;
    private int sourceoffund;
    private Date chargetime;
    private long amount;
    private int servicetype;

    public TransactionDetail(){

    }

    public TransactionDetail(long transactionid, long orderid, int sourceoffund, String chargetime, long amount, int servicetype) {
        this.transactionid = transactionid;
        this.orderid = orderid;
        this.sourceoffund = sourceoffund;
        setChargetime(chargetime);
        this.amount = amount;
        this.servicetype = servicetype;
    }

    public long getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(long transactionid) {
        this.transactionid = transactionid;
    }

    public long getOrderid() {
        return orderid;
    }

    public void setOrderid(long orderid) {
        this.orderid = orderid;
    }

    public int getSourceoffund() {
        return sourceoffund;
    }

    public void setSourceoffund(int sourceoffund) {
        this.sourceoffund = sourceoffund;
    }

    public Date getChargetime() {
        return chargetime;
    }

    public void setChargetime(String chargeTime) {
        String[] split = chargeTime.split(" ");
        String[] date = split[0].split("-");
        String[] time = split[1].split(":");
        String year = date[0];
        String month = date[1];
        String day = date[2];
        String hour = time[0];
        String minute = time[1];
        this.chargetime = new Date(year, month, day, hour, minute);
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public int getServicetype() {
        return servicetype;
    }

    public void setServicetype(int servicetype) {
        this.servicetype = servicetype;
    }

    public String getTitle(){
        if(servicetype == Service.TOPUP_SERVICE_TYPE.GetCode()){
            return "Nap tien vao tai khoan";
        }
        else if (servicetype == Service.WITHDRAW_SERVICE_TYPE.GetCode()){
            return "Rut tien ve ngan hang";
        } else if(servicetype == Service.EXCHANGE_SERVICE_TYPE.GetCode()){
            return "Chuyen tien";
        } else {
            return "Mua the cao dien thoai";
        }
    }
}
