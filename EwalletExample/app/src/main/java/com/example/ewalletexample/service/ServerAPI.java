package com.example.ewalletexample.service;

import com.example.ewalletexample.Symbol.Symbol;

public enum  ServerAPI {
    CREATE_MOBILE_CARD("/mobile-card/create"),

    // support for login api
    LOGIN_API ("/um/login"),
    REGISTER_API("/um/register"),

    UPDATE_USER_API("/um/update-user"),

    // verify user when excuting the transformation of money
    VERIFY_PIN_API("/um/verify-pin"),

    // get the balance of user by their id
    GET_BALANCE_API("/wallet/get-balance"),

    HISTORY_TRANSACTION("/charge-order/history"),

    LINK_BANK_CARD_API("/bank-mapping/link"),
    UNLINK_BANK_CARD_API("/bank-mapping/unlink"),
    GET_BANK_LINKING_API("/bank-mapping/list"),
    GET_LIST_BANK_SUPPORT("/bank-mapping/support-bank"),

    CREATE_TOPUP_ORDER("/wallet-topup/create-order"),
    GET_STATUS_TOPUP_ORDER("/wallet-topup/order-status"),

    CREATE_WITHDRAW_ORDER("/withdraw/create-order"),
    GET_STATUS_WITHDRAW_ORDER("/withdraw/order-status"),

    CREATE_EXCHANGE_MONEY_ORDER("/p2p-transfer/create-order"),
    GET_STATUS_EXCHANGE_MONEY_ORDER("/p2p-transfer/order-status"),

    CREATE_MOBILE_CARD_ORDER("/mobile-card/create-order"),
    GET_STATUS_MOBILE_CARD_ORDER("/mobile-card/order-status"),

    SUBMIT_TRANSACTION("/charge-order/submit-trans");

    private String baseURL = "http://" + Symbol.BASE_ADDRESS.GetValue() + ":8080";


    private String url;
    ServerAPI(String url){
        this.url = baseURL + url;
    }

    public String GetUrl(){
        return this.url;
    }
}
