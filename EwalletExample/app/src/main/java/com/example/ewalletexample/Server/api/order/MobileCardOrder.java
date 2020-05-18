package com.example.ewalletexample.Server.api.order;

import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.MobileCardData;
import com.example.ewalletexample.model.MobileCardModel;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.service.mobilecard.MobileCardAmount;
import com.example.ewalletexample.service.mobilecard.MobileCardOperator;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.HandleDataFromFirebaseDatabase;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MobileCardOrder extends Order {
    private String cardnumber, serinumber;
    private MobileCardOperator operator;
    private MobileCardAmount mobileCardAmount;

    public MobileCardOrder(String userid, String pin, MobileCardAmount mobileCardAmount, MobileCardOperator mobileCode, long fee, SourceFund codeSourceFund, OrderResponse response) {
        super(userid, pin, mobileCardAmount.GetAmount(), fee, codeSourceFund, Service.MOBILE_CARD_SERVICE_TYPE, response);
        this.mobileCardAmount = mobileCardAmount;
        this.operator = mobileCode;
    }

    public void StartCreateMobileCardOrder(){
        verifyPinAPI.StartVerify();
    }

    @Override
    protected void VerifyPinSuccess() throws JSONException {
        String[] arr = new String[]{"userid:"+userid,"amount:"+Long.valueOf(mobileCardAmount.GetAmount()), "cardtype:"+operator.GetMobileCode()};
        String json = HandlerJsonData.ExchangeToJsonString(arr);
        CreateOrder(ServerAPI.CREATE_MOBILE_CARD_ORDER, json);
    }

    @Override
    protected void CreateOrderSuccess() throws JSONException {
        String[] arr = new String[]{"userid:"+userid,"orderid:"+orderid,"sourceoffund:"+sourceFund.GetCode(),
                "bankcode:", "f6cardno:", "l4cardno:","amount:"+ mobileCardAmount.GetAmount(),"pin:"+pin,
                "servicetype:"+ Service.MOBILE_CARD_SERVICE_TYPE.GetCode()};

        String json = HandlerJsonData.ExchangeToJsonString(arr);
        SubmitOrder(json);
    }

    @Override
    protected void SubmitOrderSuccess() {
        GetStausOrder(ServerAPI.GET_STATUS_MOBILE_CARD_ORDER);
    }

    @Override
    protected void GetDataFromJsonFromStatusOrder(JSONObject json) throws JSONException {
        cardnumber = json.getString("cardnumber");
        serinumber = json.getString("serinumber");
        MobileCardData data = new MobileCardData();
        data.setMobileCode(operator.GetMobileCode());
        data.setCardNumber(cardnumber);
        data.setSeriNumber(serinumber);
        new ReduceNumberCardThread(operator, mobileCardAmount).run();
    }

    class ReduceNumberCardThread extends Thread implements HandleDataFromFirebaseDatabase<MobileCardModel>{
        private MobileCardOperator operator;
        private MobileCardAmount mobileCardAmount;
        private FirebaseDatabaseHandler<MobileCardModel> firebaseDatabaseHandler;

        public ReduceNumberCardThread(MobileCardOperator operator, MobileCardAmount amount){
            this.operator = operator;
            this.mobileCardAmount = amount;
            firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference(), this);
        }

        @Override
        public void run(){
            this.firebaseDatabaseHandler.RegisterDataListener();
        }

        @Override
        public void HandleDataModel(MobileCardModel model) {
            if (model != null){
                model.DecreaseMobileCardAvailable(mobileCardAmount);
                Map<String, Object> map = model.GetMapObject();
                firebaseDatabaseHandler.UpdateData(Symbol.CHILD_NAME_CARDS_FIREBASE_DATABASE.GetValue(),
                        operator.GetMobileCode(),
                        map);
            }
        }

        @Override
        public void HandleDataSnapShot(DataSnapshot dataSnapshot) {
            if(dataSnapshot.child(Symbol.CHILD_NAME_CARDS_FIREBASE_DATABASE.GetValue()).hasChild(operator.GetMobileCode())){
                MobileCardModel model = dataSnapshot.child(Symbol.CHILD_NAME_CARDS_FIREBASE_DATABASE.GetValue()).child(operator.GetMobileCode()).getValue(MobileCardModel.class);
                firebaseDatabaseHandler.UnregisterValueListener(model);
                return;
            }

            firebaseDatabaseHandler.UnregisterValueListener(null);
        }

        @Override
        public void HandlerDatabaseError(DatabaseError databaseError) {

        }
    }
}
