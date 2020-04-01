package com.example.ewalletexample.service;

public enum  ServerAPI {
    // support for login api
    LOGIN_API ("/um/login"),
    REGISTER_API("/um/register"),

    UPDATE_USER_API("/um/update"),

    // verify user when excuting the transformation of money
    VERIFY_PIN_API(""),

    // get the balance of user by their id
    GET_BALANCE_API("/wallet/get-balance"),

    LINK_BANK_CARD_API("/bank-mapping/link"),
    UNLINK_BANK_CARD_API("/bank-mapping/unlink"),
    GET_BANK_LINKING_API("/bank-mapping/list");

    private String baseURL = "http://192.168.15.112:8080";


    private String url;
    ServerAPI(String url){
        this.url = baseURL + url;
    }

    public String GetUrl(){
        return this.url;
    }
}
