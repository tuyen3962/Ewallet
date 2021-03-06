package com.example.ewalletexample.Symbol;

public enum  Symbol {
    BASE_ADDRESS("192.168.1.35"),

    PIN("PIN"),
    STYLE_TRANSACTION_DETAIL("STYLE_TRANSACTION_DETAIL"),
    RESULT("RESULT"),
    REVIEW("REVIEW"),
    SECRET_KEY_01("SECRET_KEY_01"),
    SECRET_KEY_02("SECRET_KEY_02"),

    UPDATE_SYMBOL("UPDATE"),
    UPDATE_FOR_REGISTER("UPDATE_REGISTER"),
    UPDATE_FOR_INFORMATION("UPDATE_INFORMATION"),

    NAME_PREFERENCES("MyPreference"),
    KEY_PHONES("phones"),
    KEY_PHONE("phone"),
    KEY_USERID("userid"),
    KEY_FULL_NAME("fullname"),

    REASION_VERIFY("REASONVERIFY"),
    REASON_VERIFY_FOR_FORGET("FORGETPASSWORD"),
    REASON_VERIFY_FOR_REGISTER("REGISTER"),
    REASON_VERIFY_FOR_LOGIN("LOGIN"),

    VERRIFY_FORGET("VERIFY_FORGET"),
    VERIFY_FORGET_BY_PHONE("PHONE"),
    VERIFY_FORGET_BY_EMAIL("EMAIL"),

    TRANSACTION_ID("TRANSACTION_ID"),
    TRANSACTION_DETAIL("TRANSACTION_DETAIL"),
    CHARGE_TIME("CHARGE_TIME"),
    FRIEND_ID("FRIEND_ID"),
    USER("USER"),
    FULLNAME("FULLNAME"),
    PASSWORD("PASSWORD"),
    PHONE("PHONE"),
    EMAIL("EMAIL"),
    USER_ID("USER_ID"),
    ADDRESS("ADDRESS"),
    CMND("CMND"),
    DOB("DATE_OF_BIRTH"),

    IMAGE_CMND_FRONT("CMND_FRONT"),
    IMAGE_CMND_BACK("CMND_BACK"),

    TRANSACTION_STATUS("TRANSACTION_STATUS"),

    SEARCH_USER_EXCHANGNE("SEARCH_FRIEND_EXCHANGE"),
    HAS_USER_EXCHANGE("HAS_USER_EXCHANGE"),
    NO_USER_EXCHANGE("NO_USER_EXCHANGE"),

    CHANGE_BALANCE("CHANGE_BALANCE"),

    MOBILE_CARD("MOBILE_CARD"),

    MOBILE_CODE("MOBILE_CODE"),
    MOBILE_AMOUNT("MOBILE_AMOUNT"),

    SENDER_FULL_NAME("SENDER_FULL_NAME"),
    NOTE("NOTE"),

    AMOUNT("AMOUNT"),
    IMAGE_ACCOUNT_LINK("IMAGE_ACCOUNT"),
    QUANTITY("QUANTITY"),

    SOURCE_OF_FUND("SOURCE_OF_FUND"),
    SERVICE_TYPE("SERVICE_TYPE"),
    FEE_TRANSACTION("FEE"),
    AMOUNT_TRANSACTION("AMOUNT_TRANSACTION"),
    RECEIVER_PHONE("RECEIVER_PHONE"),
    RECEIVER_FULL_NAME("RECEIVER_FULL_NAME"),
    BANK_INFO("BANK_INFO"),

    BANK_INFO_CONNECTION("BANK_INFO_CONNECTION"),
    CONNECT_BANK("CONNECT_BANK"),
    REMOVE_BANK("REMOVE_BANK"),

    QRCODE("QRCODE"),
    GENERATE("GENERATE"),
    SCAN("SCANE"),

    CHILD_NAME_USERS_FIREBASE_DATABASE("users"),
    CHILD_NAME_CARDS_FIREBASE_DATABASE("cards"),
    CHILD_NAME_TRANSACTION("transaction");

    private String value;

    Symbol(String value){
        this.value = value;
    }

    public String GetValue(){
        return value;
    }
}
