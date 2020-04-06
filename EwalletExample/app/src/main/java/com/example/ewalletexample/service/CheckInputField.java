package com.example.ewalletexample.service;

import android.util.Log;

public class CheckInputField {

    private static String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    private static String PASSWORD_REGEX = "[0-9]{6}";
    private static String PHONE_REGEX = "0[8|9|3|7|5][0-9]{8}";
    private static String CMND_REGEX = "[0-9]{9}";

    public static boolean EmailIsValid(String email){
        return email.matches(EMAIL_REGEX);
    }

    public static boolean PasswordIsValid(String passowrd){
        return passowrd.matches(PASSWORD_REGEX);
    }

    public static boolean PhoneNumberIsValid(String phone){
        if(phone.matches(PHONE_REGEX)){
            String phoneCarrier = phone.substring(0,3);
            if(CarrierNumber.GetInstance().CarrierPhoneIsValid(phoneCarrier)){
                return true;
            }
        }

        return false;
    }

    public static boolean CMNDIsValid(String cmnd){
        return cmnd.matches(CMND_REGEX);
    }
}
