package com.example.ewalletexample.Symbol;

public enum  Symbol {
    REASION_VERIFY("REASONVERIFY"),
    REASON_VERIFY_FOR_FORGET("FORGETPASSWORD"),
    REASON_VERIFY_FOR_REGISTER("REGISTER"),

    VERRIFY_FORGET("VERIFY_FORGET"),
    VERIFY_FORGET_BY_PHONE("PHONE"),
    VERIFY_FORGET_BY_EMAIL("EMAIL"),

    FULLNAME("FULLNAME"),
    PASSWORD("PASSWORD"),
    PHONE("PHONE"),
    EMAIL("EMAIL"),
    USER_ID("USER_ID"),

    AMOUNT("AMOUNT"),
    IMAGE_ACCOUNT_LINK("IMAGE_ACCOUNT"),

    CHILD_NAME_FIREBASE_DATABASE("users");

    private String value;

    Symbol(String value){
        this.value = value;
    }

    public String GetValue(){
        return value;
    }
}
