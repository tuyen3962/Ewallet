package com.example.ewalletexample.utilies;

public class Date {
    public enum DateFormat {
        DAY("dd"), MONTH("mm"), YEAR("yyyy"), HOUR("hh"), MINUTE("mm");

        private String value;
        DateFormat(String value){
            this.value = value;
        }

        public String getValue(){
            return value;
        }
    }

    private long year;
    private long month;
    private long day;
    private long hour;
    private long minute;

    public Date() {
    }

    public Date(String year, String month, String day){
        this.year = Long.valueOf(year);
        this.month = Long.valueOf(month);
        this.day = Long.valueOf(day);
    }

    public Date(String year, String month, String day, String hour, String minute) {
        this.year = Long.valueOf(year);
        this.month = Long.valueOf(month);
        this.day = Long.valueOf(day);
        this.hour = Long.valueOf(hour);
        this.minute = Long.valueOf(minute);
    }

    public String getYear() {
        return String.valueOf(year);
    }

    public void setYear(long year) {
        this.year = year;
    }

    public String getMonth() {
        return String.valueOf(month);
    }

    public void setMonth(long month) {
        this.month = month;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public long getHour() {
        return hour;
    }

    public void setHour(long hour) {
        this.hour = hour;
    }

    public long getMinute() {
        return minute;
    }

    public void setMinute(long minute) {
        this.minute = minute;
    }

    public String getDayByFormat(String format){
        return day+format+month+format+year;
    }

    public boolean isAfterByDay(Date other){
        int result = CompareTwoValue(this.year, other.year);
        if(result == 0){
            result = CompareTwoValue(this.month, other.month);
            if(result == 0) {
                result = CompareTwoValue(this.day, other.day);
            }
        }

        return result == -1;
    }

    public boolean isSameByDay(Date other) {
        int result = CompareTwoValue(this.year, other.year);
        if(result == 0){
            result = CompareTwoValue(this.month, other.month);
            if(result == 0) {
                result = CompareTwoValue(this.day, other.day);
            }
        }

        return result == 0;
    }

    public boolean isBeforeByDay(Date other) {
        int result = CompareTwoValue(this.year, other.year);
        if(result == 0){
            result = CompareTwoValue(this.month, other.month);
            if(result == 0) {
                result = CompareTwoValue(this.day, other.day);
            }
        }

        return result == 1;
    }

    public String getMonthYear(){
        return month + "/" + year;
    }

    public String getDayMonthTime(){
        return day+"/" + month + " " + hour + ":" + minute;
    }

    public boolean isAfterByMonth(Date other){
        int result = CompareTwoValue(this.year, other.year);
        if(result == 0){
            result = CompareTwoValue(this.month, other.month);
        }

        return result == -1;
    }

    public boolean isBeforeByMonth(Date other){
        int result = CompareTwoValue(this.year, other.year);
        if(result == 0){
            result = CompareTwoValue(this.month, other.month);
        }

        return result == 1;
    }

    public boolean isSameByMonth(Date other){
        int result = CompareTwoValue(this.year, other.year);
        if(result == 0){
            result = CompareTwoValue(this.month, other.month);
        }

        return result == 0;
    }

    private int CompareTwoValue(long value1, long value2){
        if(value1 == value2){
            return 0;
        }
        else if (value1 > value2) {
            return -1;
        }
        else {
            return 1;
        }
    }
}
