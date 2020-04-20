package com.example.ewalletexample.utilies;

import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.time.format.DateTimeFormatter;

import androidx.annotation.RequiresApi;

public class HandleDateTime {
    private static SimpleDateFormat dayFormatter = new SimpleDateFormat("dd-MM-yyyy");

    private static long GetCurrentTimeMillis(){
        return System.currentTimeMillis();
    }

    private static String GetStringCurrentDay(){
        Date date = new Date(GetCurrentTimeMillis());
        return dayFormatter.format(date);
    }

    public static long GetLongByString(String currentDate){
        try {
            Date date = dayFormatter.parse(currentDate);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String GetCurrentStringLastDayOfMonth() throws ParseException {
        String date = GetStringCurrentDay();
        Date convertedDate = dayFormatter.parse(date);
        Calendar c = Calendar.getInstance();
        c.setTime(convertedDate);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

        return c.getActualMaximum(Calendar.DAY_OF_MONTH) + "-"+(c.get(Calendar.MONTH) + 1) + "-"+c.get(Calendar.YEAR);
    }

    public static int GetMonthOfDate(String currentTime) throws ParseException {
        Date date = dayFormatter.parse(currentTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int GetYearOfDate(String currentTime) throws ParseException {
        Date date = dayFormatter.parse(currentTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }
}
