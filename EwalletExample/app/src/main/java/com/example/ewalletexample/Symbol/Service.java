package com.example.ewalletexample.Symbol;

import com.example.ewalletexample.ChooseBankConnectActivity;
import com.example.ewalletexample.R;
import com.example.ewalletexample.SearchUserExchangeActivity;
import com.example.ewalletexample.SelectMobileCardFunctionActivity;
import com.example.ewalletexample.ServiceWalletActivity;
import com.example.ewalletexample.ShowQrcodeActivity;
import com.example.ewalletexample.UserBankCardActivity;
import com.example.ewalletexample.VerifyAccountActivity;

import java.util.ArrayList;
import java.util.List;

public enum Service {
    SCAN_CODE_QR(-3, "Quét mã", "Quét mã QR" , R.drawable.ic_scan_qr, false, ShowQrcodeActivity.class, RequestCode.SCAN_QR_CODE),
    WATCH_CODE_QR(-3,"Mã QR", "Xem mã QR cá nhân" , R.drawable.ic_qr, false, ShowQrcodeActivity.class, RequestCode.QR_CODE),
    WALLET_TYPE(-2,"Ví tiền", "Ví tiền" , R.drawable.id_card, false, UserBankCardActivity.class, RequestCode.WALLET_CODE),

    WITHDRAW_SERVICE_TYPE(1,"Rút tiền", "Rút tiền từ ví về ngân hàng" ,R.drawable.ic_withdraw,false, ServiceWalletActivity.class, RequestCode.SUBMIT_ORDER),
    TOPUP_SERVICE_TYPE(2, "Nạp tiền", "Nạp tiền vào ví" ,R.drawable.ic_top_up ,true, ServiceWalletActivity.class, RequestCode.SUBMIT_ORDER),
    EXCHANGE_SERVICE_TYPE(3,"Chuyển tiền", "Chuyển tiền cho số điện thoại" ,R.drawable.ic_transfer_money,false, SearchUserExchangeActivity.class, RequestCode.SUBMIT_ORDER),
    MOBILE_CARD_SERVICE_TYPE(4,"Mua thẻ", "Mua thẻ cào" ,R.drawable.ic_mobile_card , false, SelectMobileCardFunctionActivity.class, RequestCode.SUBMIT_ORDER),
    LINK_CARD(5, "Liên kết thẻ","Liên kết với thẻ ngân hàng" ,R.drawable.ic_link, false,ChooseBankConnectActivity.class, RequestCode.CONNECT_BANK_CODE),
    UN_LINK_CARD(6, "Hủy liên kết thẻ","Hủy liên kết với thẻ ngân hàng" ,R.drawable.ic_unlink, false,ChooseBankConnectActivity.class, RequestCode.UNLINK_BANK_CODE),
    VERIFY_USER(7, "Bảo mật tài khoản","Bảo mật tài khoản" ,R.drawable.mobile_card, false, VerifyAccountActivity.class, RequestCode.SECURITY_CODE);

    private int numberCode;
    private String nameCode, description;
    private int imgId;
    private boolean positive;
    private Class transitionClass;
    private int code;

    Service(int numberCode, String nameCode, String description, int imgId, boolean positive, Class transitionClass, int code){
        this.numberCode = numberCode;
        this.nameCode = nameCode;
        this.description = description;
        this.imgId = imgId;
        this.positive = positive;
        this.transitionClass = transitionClass;
        this.code = code;
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

    public int GetRequestCode(){
        return this.code;
    }

    public String GetDescription(){
        return this.description;
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
