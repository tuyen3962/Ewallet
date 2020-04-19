package com.example.ewalletexample.model;

import com.example.ewalletexample.Symbol.Service;

public class Statistic {
    private long totalTransaction;
    private long totalCost;
    private long totalIncome;
    private long totalOutcome;

    public Statistic(){
        this.totalTransaction = 0;
        this.totalIncome = 0;
        this.totalOutcome = 0;
    }

    public Statistic(long totalTransaction, long totalIncome, long totalOutcome) {
        this.totalTransaction = totalTransaction;
        this.totalIncome = totalIncome;
        this.totalOutcome = totalOutcome;
    }

    public long getTotalTransaction() {
        return totalTransaction;
    }

    public void setTotalTransaction(long totalTransaction) {
        this.totalTransaction = totalTransaction;
    }

    public long getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(long totalIncome) {
        this.totalIncome = totalIncome;
    }

    public long getTotalOutcome() {
        return totalOutcome;
    }

    public void setTotalOutcome(long totalOutcome) {
        this.totalOutcome = totalOutcome;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(long totalCost) {
        this.totalCost = totalCost;
    }

    private void increaseNumberTransaction(){
        this.totalTransaction += 1;
    }

    private void increateIncome(long income){
        this.totalIncome += income;
        updateTotalCost();
    }

    private void increaseOutCome(long outcome){
        this.totalOutcome += outcome;
        updateTotalCost();
    }

    private void updateTotalCost(){
        totalCost = totalIncome - totalOutcome;
    }

    public void updateStatistic(Service service, long amount){
        increaseNumberTransaction();
        if(service.GetPositive()){
            this.increateIncome(amount);
        } else {
            this.increaseOutCome(amount);
        }
    }

    public long positiveTotalCost(){
        long total = totalCost;
        if (total < 0)
            total *= -1;

        return total;
    }
}
