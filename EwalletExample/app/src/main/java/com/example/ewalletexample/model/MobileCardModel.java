package com.example.ewalletexample.model;

import android.util.Log;

import com.example.ewalletexample.service.mobilecard.MobileCardAmount;
import com.example.ewalletexample.service.mobilecard.MobileCardOperator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobileCardModel {
    public Map<String, Integer> cardAvailable;

    public MobileCardModel(){
        cardAvailable = new HashMap<>();
    }

    public MobileCardModel(List<MobileCardAmount> listAmounts){
        cardAvailable = new HashMap<>();
        for (MobileCardAmount amount : listAmounts){
            cardAvailable.put(amount.GetAmount(), 0);
        }
    }

    public MobileCardModel(List<MobileCardAmount> listAmounts, MobileCardAmount mobileCardAmount){
        cardAvailable = new HashMap<>();
        for (MobileCardAmount amount : listAmounts){
            cardAvailable.put(amount.GetAmount(), 0);
        }

        IncreaseMobileCardAvailable(mobileCardAmount);
    }

    public Map<String, Integer> getCardAvailable() {
        return cardAvailable;
    }

    public void setCardAvailable(Map<String, Integer> cardAvailable) {
        this.cardAvailable = cardAvailable;
    }

    public void IncreaseMobileCardAvailable(MobileCardAmount amount){
        int number = cardAvailable.get(amount.GetAmount());
        number += 1;
        cardAvailable.put(amount.GetAmount(), number);
    }

    public void DecreaseMobileCardAvailable(MobileCardAmount amount){
        int number = cardAvailable.get(amount.GetAmount());
        number -= 1;
        cardAvailable.put(amount.GetAmount(), number);
    }

    public boolean IsCardAmountAvailable(MobileCardAmount amount){
        if(cardAvailable.get(amount.GetAmount()) > 0){
            return true;
        }
        else {
            return false;
        }
    }

    public Map<String, Object> GetMapObject(){
        Map<String, Object> map = new HashMap<>();
        map.put("cardAvailable", cardAvailable);
        return map;
    }
}
