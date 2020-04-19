package com.example.ewalletexample.Symbol;

public enum Service {
    RECEIVE_MONEY_TYPE(-1, "Nhận tiền", true),

    WITHDRAW_SERVICE_TYPE(1,"Rút tiền", false),
    TOPUP_SERVICE_TYPE(2, "Nạp tiền", true),
    EXCHANGE_SERVICE_TYPE(3,"Chuyển tiền", false),
    MOBILE_CARD_SERVICE_TYPE(4,"Mua thẻ", false);

    private int numberCode;
    private String nameCode;
    private boolean positive;

    Service(int numberCode, String nameCode, boolean positive){
        this.numberCode = numberCode;
        this.nameCode = nameCode;
        this.positive = positive;
    }

    public int GetCode(){
        return numberCode;
    }

    public String GetMessage(){
        return nameCode;
    }

    public boolean GetPositive(){
        return this.positive;
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
