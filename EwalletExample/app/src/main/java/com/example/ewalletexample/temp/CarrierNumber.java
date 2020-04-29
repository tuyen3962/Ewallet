package com.example.ewalletexample.temp;

import com.example.ewalletexample.service.mobilecard.MobileNetworkOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CarrierNumber {
    HashMap<Integer, MobileNetworkOperator> mobileNetworkOperatorHashMap;
    private static HashMap<MobileNetworkOperator, String> listCarrierNumber = new HashMap<>();
    private List<String> viettel;
    private List<String> vina;
    private List<String> mobi;
    private List<String> vietnam;
    private List<String> gmobile;
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
        mobileNetworkOperatorHashMap = new HashMap<>();
        mobileNetworkOperatorHashMap.put(0,MobileNetworkOperator.VIETTEL);
        mobileNetworkOperatorHashMap.put(1, MobileNetworkOperator.GMOBILE);
        mobileNetworkOperatorHashMap.put(2,MobileNetworkOperator.MOBIPHONE);
        mobileNetworkOperatorHashMap.put(3, MobileNetworkOperator.VIETNAMMOBILE);
        mobileNetworkOperatorHashMap.put(4, MobileNetworkOperator.VINAPHONE);

        viettel = new ArrayList<>();
        viettel.add("086");
        viettel.add("039");
        viettel.add("038");
        viettel.add("037");
        viettel.add("036");
        viettel.add("035");
        viettel.add("034");
        viettel.add("033");
        viettel.add("032");
        viettel.add("098");
        viettel.add("097");
        viettel.add("096");
        listCarrierNumber.put(MobileNetworkOperator.VIETTEL, "086");
        listCarrierNumber.put(MobileNetworkOperator.VIETTEL,"096");
        listCarrierNumber.put(MobileNetworkOperator.VIETTEL,"097");
        listCarrierNumber.put(MobileNetworkOperator.VIETTEL,"098");
        listCarrierNumber.put(MobileNetworkOperator.VIETTEL,"032");
        listCarrierNumber.put(MobileNetworkOperator.VIETTEL,"033");
        listCarrierNumber.put(MobileNetworkOperator.VIETTEL,"034");
        listCarrierNumber.put(MobileNetworkOperator.VIETTEL,"035");
        listCarrierNumber.put(MobileNetworkOperator.VIETTEL,"036");
        listCarrierNumber.put(MobileNetworkOperator.VIETTEL,"037");
        listCarrierNumber.put(MobileNetworkOperator.VIETTEL,"038");
        listCarrierNumber.put(MobileNetworkOperator.VIETTEL,"039");

        vina = new ArrayList<>();
        vina.add("091");
        vina.add("094");
        vina.add("088");
        vina.add("083");
        vina.add("084");
        vina.add("085");
        vina.add("081");
        vina.add("082");
        listCarrierNumber.put(MobileNetworkOperator.VINAPHONE,"091");
        listCarrierNumber.put(MobileNetworkOperator.VINAPHONE,"094");
        listCarrierNumber.put(MobileNetworkOperator.VINAPHONE,"088");
        listCarrierNumber.put(MobileNetworkOperator.VINAPHONE,"083");
        listCarrierNumber.put(MobileNetworkOperator.VINAPHONE,"084");
        listCarrierNumber.put(MobileNetworkOperator.VINAPHONE,"085");
        listCarrierNumber.put(MobileNetworkOperator.VINAPHONE,"081");
        listCarrierNumber.put(MobileNetworkOperator.VINAPHONE,"082");

        mobi = new ArrayList<>();
        mobi.add("089");
        mobi.add("090");
        mobi.add("093");
        mobi.add("070");
        mobi.add("079");
        mobi.add("077");
        mobi.add("076");
        mobi.add("078");
        listCarrierNumber.put(MobileNetworkOperator.MOBIPHONE,"089");
        listCarrierNumber.put(MobileNetworkOperator.MOBIPHONE,"090");
        listCarrierNumber.put(MobileNetworkOperator.MOBIPHONE,"093");
        listCarrierNumber.put(MobileNetworkOperator.MOBIPHONE,"070");
        listCarrierNumber.put(MobileNetworkOperator.MOBIPHONE,"079");
        listCarrierNumber.put(MobileNetworkOperator.MOBIPHONE,"077");
        listCarrierNumber.put(MobileNetworkOperator.MOBIPHONE,"076");
        listCarrierNumber.put(MobileNetworkOperator.MOBIPHONE,"078");

        vietnam = new ArrayList<>();
        vietnam.add("092");
        vietnam.add("056");
        vietnam.add("058");
        listCarrierNumber.put(MobileNetworkOperator.VIETNAMMOBILE,"092");
        listCarrierNumber.put(MobileNetworkOperator.VIETNAMMOBILE,"056");
        listCarrierNumber.put(MobileNetworkOperator.VIETNAMMOBILE,"058");

        gmobile = new ArrayList<>();
        gmobile.add("099");
        gmobile.add("059");
        listCarrierNumber.put(MobileNetworkOperator.GMOBILE,"099");
        listCarrierNumber.put(MobileNetworkOperator.GMOBILE,"059");
    }

    public String GetRandomCarrierViettel(){
        return viettel.get(new Random().nextInt(viettel.size()));
    }

    public String GetRandomCarrierGmobile(){
        return gmobile.get(new Random().nextInt(gmobile.size()));
    }

    public String GetRandomCarrierVietnammobile(){
        return vietnam.get(new Random().nextInt(vietnam.size()));
    }

    public String GetRandomCarrierMobi(){
        return mobi.get(new Random().nextInt(mobi.size()));
    }

    public String GetRandomCarrierVinaphone(){
        return vina.get(new Random().nextInt(vina.size()));
    }

    public String GetPhoneNumber(){
        Random random = new Random();
        MobileNetworkOperator networkOperator = mobileNetworkOperatorHashMap.get(random.nextInt(mobileNetworkOperatorHashMap.size()));
        String carrierNumber;
        if (networkOperator == MobileNetworkOperator.GMOBILE){
            carrierNumber = GetRandomCarrierGmobile();
        }
        else if(networkOperator == MobileNetworkOperator.MOBIPHONE){
            carrierNumber = GetRandomCarrierMobi();
        }
        else if(networkOperator == MobileNetworkOperator.VIETNAMMOBILE){
            carrierNumber = GetRandomCarrierVietnammobile();
        }
        else if(networkOperator == MobileNetworkOperator.VIETTEL){
            carrierNumber = GetRandomCarrierViettel();
        }
        else{
            carrierNumber = GetRandomCarrierVinaphone();
        }

        String body = "";

        for (int i = 0; i < 7; i++){
            int value = random.nextInt(10);
            body += String.valueOf(value);
        }

        return carrierNumber + body;
    }
}
