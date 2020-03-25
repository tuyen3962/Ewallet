package com.example.ewalletexample.Symbol;

public enum  ServerAPI {
    LOGIN_API ("/um/login"),
    REGISTER_API("/um/register"),
    VERIFY_PIN_API("");

    private String baseURL = "http://192.168.1.6:8080";


    private String url;
    ServerAPI(String url){
        this.url = baseURL + url;
    }

    public String GetUrl(){
        return this.url;
    }
}
