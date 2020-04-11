package com.example.ewalletexample.Symbol;

public enum Code {
    WALLET_SOURCE_FUND(1, "Ví"),
    ATM_SOURCE_FUND(2, "Thẻ ATM"),

    WITHDRAW_SERVICE_TYPE(1,"Rút tiền"),
    TOPUP_SERVICE_TYPE(2, "Nạp tiền"),
    EXCHANGE_SERVICE_TYPE(3,"Chuyển tiền"),
    MOBILE_CARD_SERVICE_TYPE(4,"Mua thẻ");

    private int numberCode;
    private String nameCode;

    Code(int numberCode, String nameCode){
        this.numberCode = numberCode;
        this.nameCode = nameCode;
    }

    public int GetCode(){
        return numberCode;
    }
}
