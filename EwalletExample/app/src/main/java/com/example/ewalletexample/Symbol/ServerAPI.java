package com.example.ewalletexample.Symbol;

public enum  ServerAPI {
    LOGIN_API ("/um/login"),
    REGISTER_API("/um/register"),
    VERIFY_PIN_API(""),
    GET_BALANCE_API("/wallet/get-balance"),
    ADD_CASH("/wallet/add-cash"),
    SUBSTRACT_CASH("/wallet/subtract-cash"),
    LINK_BANK_CARD_API("/bank-mapping/link"),
    UNLINK_BANK_CARD_API("/bank-mapping/unlink"),
    GET_BANK_LINKING_API("/bank-mapping/list");

    private String baseURL = "http://192.168.1.6:8080";


    private String url;
    ServerAPI(String url){
        this.url = baseURL + url;
    }

    public String GetUrl(){
        return this.url;
    }
}
