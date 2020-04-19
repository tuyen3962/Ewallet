package com.example.ewalletexample.model;

import android.icu.text.Edits;

import com.example.ewalletexample.Symbol.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StatisMonthTransaction {
    private HashMap<String, Statistic> statisticMonth;

    public StatisMonthTransaction(){
        statisticMonth = new HashMap<>();
    }

    public HashMap<String, Statistic> getStatisticMonth() {
        return statisticMonth;
    }

    public void setStatisticMonth(HashMap<String, Statistic> statisticMonth) {
        this.statisticMonth = statisticMonth;
    }

    public boolean hasDay(String newDate){
        return statisticMonth.containsKey(newDate);
    }

    public void addNewDay(String date, Service service, String amount){
        Statistic statistic = new Statistic();
        statistic.updateStatistic(service, Long.valueOf(amount));
        statisticMonth.put(date, statistic);
    }

    public void updateTransactionByDay(String day, Service service, String amount) {
        Statistic statistic = statisticMonth.get(day);
        statistic.updateStatistic(service, Long.valueOf(amount));
        statisticMonth.put(day, statistic);
    }

    public Statistic getStatisByDay(String date){
        return statisticMonth.get(date);
    }

    public List<String> listDay(){
        List<String> listDay = new ArrayList<>();
        Iterator days = statisticMonth.keySet().iterator();
        while (days.hasNext()){
            String day = (String) days.next();
            listDay.add(day);
        }

        return listDay;
    }

    public Map<String, Object> mapObjectFirebaseDB(){
        Map<String, Object> map = new HashMap<>();
        map.put("statisticMonth", statisticMonth);
        return map;
    }
}
