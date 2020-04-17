package com.example.ewalletexample.Symbol;

public enum SourceFund {
    WALLET_SOURCE_FUND("1", "Ví"),
    ATM_SOURCE_FUND("2", "Thẻ ATM");

    private short numberCode;
    private String nameCode;

    SourceFund(String numberCode, String nameCode){
        this.numberCode = Short.parseShort(numberCode);
        this.nameCode = nameCode;
    }

    public short GetCode(){
        return numberCode;
    }

    public static SourceFund Find(short code){
        for(SourceFund source : SourceFund.values()){
            if(source.GetCode() == code){
                return source;
            }
        }

        return null;
    }
}
