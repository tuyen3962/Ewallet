package com.example.ewalletexample.Symbol;

import com.example.ewalletexample.ChooseBankConnectActivity;
import com.example.ewalletexample.R;
import com.example.ewalletexample.SearchUserExchangeActivity;
import com.example.ewalletexample.SelectMobileCardFunctionActivity;
import com.example.ewalletexample.ServiceWalletActivity;
import com.example.ewalletexample.ShowQrcodeActivity;
import com.example.ewalletexample.UserBankCardActivity;

import java.util.ArrayList;
import java.util.List;

public enum Service {
    SCAN_CODE_QR(-3, "Quét mã", R.drawable.id_card, false, ShowQrcodeActivity.class),
    WATCH_CODE_QR(-3,"Mã QR", R.drawable.id_card, false, ShowQrcodeActivity.class),
    WALLET_TYPE(-2,"Ví tiền", R.drawable.id_card, false, UserBankCardActivity.class),

    WITHDRAW_SERVICE_TYPE(1,"Rút tiền", R.drawable.withdraw,false, ServiceWalletActivity.class),
    TOPUP_SERVICE_TYPE(2, "Nạp tiền", R.drawable.icon_top_up ,true, ServiceWalletActivity.class),
    EXCHANGE_SERVICE_TYPE(3,"Chuyển tiền", R.drawable.exchange,false, SearchUserExchangeActivity.class),
    MOBILE_CARD_SERVICE_TYPE(4,"Mua thẻ", R.drawable.mobile_card , false, SelectMobileCardFunctionActivity.class),
    LINK_CARD(5, "linkCard",R.drawable.transfer, false,ChooseBankConnectActivity.class),
    UN_LINK_CARD(6, "unLinkCard",R.drawable.mobile_card, false,ChooseBankConnectActivity.class),
    VERIFY_USER(7, "verifyUser",R.drawable.mobile_card, false,ChooseBankConnectActivity.class);

    private int numberCode;
    private String nameCode;
    private int imgId;
    private boolean positive;
    private Class transitionClass;

    Service(int numberCode, String nameCode, int imgId, boolean positive, Class transitionClass){
        this.numberCode = numberCode;
        this.nameCode = nameCode;
        this.imgId = imgId;
        this.positive = positive;
        this.transitionClass = transitionClass;
    }

    public int GetCode(){
        return numberCode;
    }

    public String GetName(){
        return nameCode;
    }

    public boolean GetPositive(){
        return this.positive;
    }

    public int GetImageId(){
        return imgId;
    }

    public Class GetClassTransition(){
        return transitionClass;
    }

    public static List<Service> GetList(){
        List<Service> serviceList = new ArrayList<>();

        for (Service service : Service.values()){
            if (service.GetCode() >= 0){
                serviceList.add(service);
            }
        }

        return serviceList;
    }

    public static List<Service> GetMainService(){
        List<Service> serviceList = new ArrayList<>();
        serviceList.add(TOPUP_SERVICE_TYPE);
        serviceList.add(SCAN_CODE_QR);
        serviceList.add(WATCH_CODE_QR);
        serviceList.add(WALLET_TYPE);
        return serviceList;
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
