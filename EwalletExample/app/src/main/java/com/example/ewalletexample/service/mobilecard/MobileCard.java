package com.example.ewalletexample.service.mobilecard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MobileCard {
    private HashMap<MobileCardOperator, List<MobileCardAmount>> mobileCardOperatorList;

    private static MobileCard Instance;

    public static MobileCard GetInstance(){
        if(Instance == null){
            Instance = new MobileCard();
        }

        return Instance;
    }

    private MobileCard(){
        mobileCardOperatorList = new HashMap<>();
        for (MobileCardOperator operator : MobileCardOperator.values()){
            List<MobileCardAmount> listAmounts = new ArrayList<>();
            for (MobileCardAmount amount : MobileCardAmount.values()){
                listAmounts.add(amount);
            }
            mobileCardOperatorList.put(operator, listAmounts);
        }
    }

    public List<MobileCardAmount> GetListAmountsByMobileCardOperator(MobileCardOperator operator){
        if (mobileCardOperatorList.containsKey(operator)){
            return mobileCardOperatorList.get(operator);
        }

        return null;
    }

    public MobileCardOperator[] GetMobileCardOperator(){
        return MobileCardOperator.values();
    }
}
