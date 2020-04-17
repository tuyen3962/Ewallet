package com.example.ewalletexample.service.mobilecard;

public enum  MobileCardAmount {
    CARD_20K("20000"),
    CARD_50K("50000"),
    CARD_100K("100000"),
    CARD_200K("200000"),
    CARD_300K("300000"),
    CARD_400K("400000"),
    CARD_500K("500000");

    private String amount;
    MobileCardAmount(String amount){
        this.amount = amount;
    }

    public String GetAmount(){
        return amount;
    }

    public static MobileCardAmount FindAmount(String amount){
        for (MobileCardAmount mobileCardAmount : MobileCardAmount.values())
            if (mobileCardAmount.GetAmount().equalsIgnoreCase(amount))
                return mobileCardAmount;

        return null;
    }
}
