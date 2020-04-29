package com.example.ewalletexample.service;

import com.example.ewalletexample.service.mobilecard.MobileNetworkOperator;

import java.util.HashMap;

public class CarrierNumber {

    private static HashMap<String, MobileNetworkOperator> listCarrierNumber = new HashMap<>();
    private static CarrierNumber instance;

    private CarrierNumber(){
        SetListCarrierNumberByDefault();
    }

    public static CarrierNumber GetInstance(){
        if(instance == null){
            instance = new CarrierNumber();
        }

        return instance;
    }

    private void SetListCarrierNumberByDefault(){
        listCarrierNumber.put("086",MobileNetworkOperator.VIETTEL);
        listCarrierNumber.put("096",MobileNetworkOperator.VIETTEL);
        listCarrierNumber.put("097",MobileNetworkOperator.VIETTEL);
        listCarrierNumber.put("098",MobileNetworkOperator.VIETTEL);
        listCarrierNumber.put("032",MobileNetworkOperator.VIETTEL);
        listCarrierNumber.put("033",MobileNetworkOperator.VIETTEL);
        listCarrierNumber.put("034",MobileNetworkOperator.VIETTEL);
        listCarrierNumber.put("035",MobileNetworkOperator.VIETTEL);
        listCarrierNumber.put("036",MobileNetworkOperator.VIETTEL);
        listCarrierNumber.put("037",MobileNetworkOperator.VIETTEL);
        listCarrierNumber.put("038",MobileNetworkOperator.VIETTEL);
        listCarrierNumber.put("039",MobileNetworkOperator.VIETTEL);

        listCarrierNumber.put("091",MobileNetworkOperator.VINAPHONE);
        listCarrierNumber.put("094",MobileNetworkOperator.VINAPHONE);
        listCarrierNumber.put("088",MobileNetworkOperator.VINAPHONE);
        listCarrierNumber.put("083",MobileNetworkOperator.VINAPHONE);
        listCarrierNumber.put("084",MobileNetworkOperator.VINAPHONE);
        listCarrierNumber.put("085",MobileNetworkOperator.VINAPHONE);
        listCarrierNumber.put("081",MobileNetworkOperator.VINAPHONE);
        listCarrierNumber.put("082",MobileNetworkOperator.VINAPHONE);

        listCarrierNumber.put("089",MobileNetworkOperator.MOBIPHONE);
        listCarrierNumber.put("090",MobileNetworkOperator.MOBIPHONE);
        listCarrierNumber.put("093",MobileNetworkOperator.MOBIPHONE);
        listCarrierNumber.put("070",MobileNetworkOperator.MOBIPHONE);
        listCarrierNumber.put("079",MobileNetworkOperator.MOBIPHONE);
        listCarrierNumber.put("077",MobileNetworkOperator.MOBIPHONE);
        listCarrierNumber.put("076",MobileNetworkOperator.MOBIPHONE);
        listCarrierNumber.put("078",MobileNetworkOperator.MOBIPHONE);

        listCarrierNumber.put("092",MobileNetworkOperator.VIETNAMMOBILE);
        listCarrierNumber.put("056",MobileNetworkOperator.VIETNAMMOBILE);
        listCarrierNumber.put("058",MobileNetworkOperator.VIETNAMMOBILE);

        listCarrierNumber.put("099",MobileNetworkOperator.GMOBILE);
        listCarrierNumber.put("059",MobileNetworkOperator.GMOBILE);
    }

    public boolean CarrierPhoneIsValid(String carrierPhone){
        return listCarrierNumber.containsKey(carrierPhone);
    }

    public MobileNetworkOperator GetMobileNetworkOperatorFromPhoneCarrier(String carrierPhone){
        if(listCarrierNumber.containsKey(carrierPhone)){
            return listCarrierNumber.get(carrierPhone);
        }

        return MobileNetworkOperator.NONE;
    }
}
