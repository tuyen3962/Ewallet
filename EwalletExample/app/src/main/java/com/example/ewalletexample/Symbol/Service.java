package com.example.ewalletexample.Symbol;

public enum Service {
    WITHDRAW_SERVICE_TYPE(1,"Rút tiền"),
    TOPUP_SERVICE_TYPE(2, "Nạp tiền"),
    EXCHANGE_SERVICE_TYPE(3,"Chuyển tiền"),
    MOBILE_CARD_SERVICE_TYPE(4,"Mua thẻ");

    private int numberCode;
    private String nameCode;

    Service(int numberCode, String nameCode){
        this.numberCode = numberCode;
        this.nameCode = nameCode;
    }

    public int GetCode(){
        return numberCode;
    }

    public String GetMessage(){
        return nameCode;
    }

    public static Service Find(int code){
        for (Service service : Service.values()){
            if(service.GetCode() == code){
                return service;
            }
        }

        return null;
    }
}
