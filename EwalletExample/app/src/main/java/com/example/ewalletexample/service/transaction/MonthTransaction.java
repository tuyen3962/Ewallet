package com.example.ewalletexample.service.transaction;

import android.util.Log;

import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.data.TransactionDetail;
import com.example.ewalletexample.utilies.Date;

import java.util.List;

public class MonthTransaction {
    private Date date;
    private List<TransactionDetail> transactionList;
    private int totalCost;

    public MonthTransaction(Date date, List<TransactionDetail> transactionList){
        this.date = date;
        this.transactionList = transactionList;
    }

    public MonthTransaction(List<TransactionDetail> transactionList){
        this.transactionList = transactionList;
        if (transactionList.size() > 0){
            TransactionDetail detail = transactionList.get(0);
//            this.date = new Date(detail.getChargetime().getYear(), detail.getChargetime().getMonth());
        }
    }

    public void CalulateTotalCost(){
        totalCost = 0;
        for (TransactionDetail transaction : transactionList){
            Service service = Service.Find(transaction.getServicetype());
            if(service.GetPositive()){
                totalCost += transaction.getAmount();
            }
            else {
                totalCost -= transaction.getAmount();
            }
            Log.d("TAG", "CalulateTotalCost: " + transaction.getAmount() + " " + totalCost);
        }
    }

    public boolean IsBalancePositive(){
        if(totalCost > 0){
            return true;
        }
        else {
            return false;
        }
    }

    public long getTotalCost(){
        long cost = totalCost;
        if (cost < 0){
            cost *= -1;
        }

        return cost;
    }

    public String getMonthYear(){
        return date.getMonthYear();
    }

    public Date getDate(){
        return date;
    }
}
