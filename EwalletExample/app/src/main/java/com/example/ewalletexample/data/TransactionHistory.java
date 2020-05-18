package com.example.ewalletexample.data;

public class TransactionHistory {
    private long transactionid;
    private long orderid;
    private long amount;
    private int servicetype;
    private int sourceoffund;
    private String timemilliseconds;
    private int status;
    private String txndetail;

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

    public int getSourceoffund() {
        return sourceoffund;
    }

    public void setSourceoffund(int sourceoffund) {
        this.sourceoffund = sourceoffund;
    }

    public String getTimemilliseconds() {
        return timemilliseconds;
    }

    public void setTimemilliseconds(String timemilliseconds) {
        this.timemilliseconds = timemilliseconds;
    }

    public String getTxndetail() {
        return txndetail;
    }

    public void setTxndetail(String txndetail) {
        this.txndetail = txndetail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
