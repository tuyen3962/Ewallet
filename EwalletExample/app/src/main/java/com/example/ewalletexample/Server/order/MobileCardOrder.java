package com.example.ewalletexample.Server.order;

import android.util.Log;

import com.example.ewalletexample.Server.VerifyPin.VerifyPinAPI;
import com.example.ewalletexample.Server.VerifyPin.VerifyResponse;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.Symbol;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MobileCardOrder implements VerifyResponse , HandleDataFromFirebaseDatabase<MobileCardModel> {
    private MobileCardOrderResponse mobileCardOrderResponse;
    private String userid, pin, phone, cardnumber, serinumber;
    private long orderid, fee;
    private short codeSourceFund;
    private FirebaseDatabaseHandler<MobileCardModel> firebaseDatabaseHandler;
    private VerifyPinAPI verifyPinAPI;
    private MobileCardOperator operator;
    private MobileCardAmount amount;

    public MobileCardOrder(String userid, String pin, MobileCardAmount amount, MobileCardOperator mobileCode, long fee, String phone, short codeSourceFund, MobileCardOrderResponse response) {
        this.userid = userid;
        this.pin = pin;
        this.phone = phone;
        this.amount = amount;
        this.operator = mobileCode;
        this.fee = fee;
        this.codeSourceFund = codeSourceFund;
        verifyPinAPI = new VerifyPinAPI(userid, pin, this);
        this.mobileCardOrderResponse = response;

        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference(), this);
    }

    public void StartCreateMobileCardOrder(){
        verifyPinAPI.StartVerify();
    }

    @Override
    public void VerifyPinResponse(boolean isSuccess) {
        if(isSuccess){
            try {
                String[] arr = new String[]{"userid:"+userid,"amount:"+Long.valueOf(amount.GetAmount()), "phone:"+ phone, "cardtype:"+operator.GetMobileCode()};
                String json = HandlerJsonData.ExchangeToJsonString(arr);
                new CreateMobileCardOrder().execute(ServerAPI.CREATE_MOBILE_CARD_ORDER.GetUrl(), json);
            } catch (JSONException e){

            }
        }
    }

    @Override
    public void HandleDataModel(MobileCardModel model) {
        if (model != null){
            model.DecreaseMobileCardAvailable(amount);
            Map<String, Object> map = model.GetMapObject();
            firebaseDatabaseHandler.UpdateData(Symbol.CHILD_NAME_CARDS_FIREBASE_DATABASE.GetValue(),
                    operator.GetMobileCode(),
                    map);

            mobileCardOrderResponse.ResponseMobileCard(cardnumber, serinumber);
            return;
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

    class CreateMobileCardOrder extends RequestServerAPI implements RequestServerFunction {
        public CreateMobileCardOrder(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if(code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }

            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            orderid = jsonData.getLong("orderid");
            try {
                String[] arr = new String[]{"userid:"+userid,"orderid:"+orderid,"sourceoffund:"+codeSourceFund,
                        "bankcode:", "f6cardno:", "l4cardno:","amount:"+amount.GetAmount(),"pin:"+pin,
                        "servicetype:"+ Service.MOBILE_CARD_SERVICE_TYPE.GetCode()};

                String json = HandlerJsonData.ExchangeToJsonString(arr);
                new SubmitMobileCardOrder().execute(ServerAPI.SUBMIT_TRANSACTION.GetUrl(), json);
            } catch (JSONException e){

            }
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }

    class SubmitMobileCardOrder extends RequestServerAPI implements RequestServerFunction{
        public SubmitMobileCardOrder(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if(code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }

            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            int bankreturncode = jsonData.getInt("bankreturncode");
//            long transactionid = jsonData.getLong("transactionid");
            try {
                String[] arr = new String[]{"userid:"+userid,"orderid:"+orderid};

                String json = HandlerJsonData.ExchangeToJsonString(arr);
                new GetStatusMobileCardOrder().execute(ServerAPI.GET_STATUS_MOBILE_CARD_ORDER.GetUrl(), json);
            } catch (JSONException e){

            }
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }

    class GetStatusMobileCardOrder extends RequestServerAPI implements RequestServerFunction{

        public GetStatusMobileCardOrder(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if(code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }
            else if(code > ErrorCode.SUCCESS.GetValue()){
                return false;
            }

            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            cardnumber = jsonData.getString("cardnumber");
            serinumber = jsonData.getString("serinumber");
            firebaseDatabaseHandler.RegisterDataListener();
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }
}
