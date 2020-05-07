package com.example.ewalletexample.service.mobilecard;

import com.example.ewalletexample.service.mobilecard.recycleViewHolder.MobileCardAmountAdapter;
import com.example.ewalletexample.service.mobilecard.recycleViewHolder.MobileCardOperatorAdapter;

public interface SelectMobileCardFunction {
    void SelectMobileCardOperator(MobileCardOperatorAdapter.MobileCardOperatorViewHolder mobileCardOperatorViewHolder, MobileCardOperator mobileCardOperator);

    MobileCardOperator GetCurrentMobileCardOperator();

    void SelectMobileCardAmount(MobileCardAmountAdapter.MobileCardAmountViewHolder currentAmountView, MobileCardAmount mobileCardAmount);
}
