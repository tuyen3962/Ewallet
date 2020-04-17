package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.model.MobileCardModel;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.service.mobilecard.MobileCard;
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
import java.util.Random;

public class AddMobileCardAuto extends AppCompatActivity implements HandleDataFromFirebaseDatabase<MobileCardModel> {

    FirebaseDatabaseHandler<MobileCardModel> firebaseDatabaseHandler;
    Button btnAdd;
    MobileCardOperator operator;
    MobileCardAmount amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_automatic);
        btnAdd = findViewById(R.id.btnAdd);
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference(), this);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Add();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void Add() throws InterruptedException {
        BeginAdd(20);
    }

    private void BeginAdd(int number) throws InterruptedException {
        for (MobileCardOperator operator : MobileCard.GetInstance().GetMobileCardOperator()){
            for (MobileCardAmount amount : MobileCard.GetInstance().GetListAmountsByMobileCardOperator(operator)){
                for(int i = 0; i < number ; i++){
                    String cardNumber = GetRandomNumber(15);
                    String seri = GetRandomNumber(14);
                    Log.d("TAG", "BeginAdd: has created");
                    try {
                        String[] arr = new String[]{"cardnumber:" + cardNumber, "serinumber:" + seri, "cardtype:" + operator.GetMobileCode(), "amount:" + Long.valueOf(amount.GetAmount())};
                        String json = HandlerJsonData.ExchangeToJsonString(arr);
                        new CreateMobileCard().execute(ServerAPI.CREATE_MOBILE_CARD.GetUrl(), json);
                        Thread.sleep(1000);
                    }
                    catch (JSONException e){
                        Log.d("TAG", "BeginAdd: " + e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void HandleDataModel(MobileCardModel data) {
        if (amount == null || operator == null)
            return;

        if (data == null){
            data = new MobileCardModel(MobileCard.GetInstance().GetListAmountsByMobileCardOperator(MobileCardOperator.GMOBILE), amount);
            firebaseDatabaseHandler.PushDataIntoDatabase(Symbol.CHILD_NAME_CARDS_FIREBASE_DATABASE.GetValue(), operator.GetMobileCode(), data);
        }
        else {
            data.IncreaseMobileCardAvailable(amount);
            Map<String,Object> map = data.GetMapObject();

            firebaseDatabaseHandler.UpdateData(Symbol.CHILD_NAME_CARDS_FIREBASE_DATABASE.GetValue(), operator.GetMobileCode(), map);
        }

        Log.d("TAG", "HandleDataModel: upload firebase successfully ");
    }

    @Override
    public void HandleDataSnapShot(DataSnapshot dataSnapshot) {
        if (amount == null || operator == null)
            return;

        if(dataSnapshot.hasChild(Symbol.CHILD_NAME_CARDS_FIREBASE_DATABASE.GetValue())
                && dataSnapshot.child(Symbol.CHILD_NAME_CARDS_FIREBASE_DATABASE.GetValue()).hasChild(operator.GetMobileCode())){

            MobileCardModel model = dataSnapshot.child(Symbol.CHILD_NAME_CARDS_FIREBASE_DATABASE.GetValue()).child(operator.GetMobileCode()).getValue(MobileCardModel.class);
            firebaseDatabaseHandler.UnregisterValueListener(model);
            return;
        }

        firebaseDatabaseHandler.UnregisterValueListener(null);
    }

    @Override
    public void HandlerDatabaseError(DatabaseError databaseError) {

    }

    String GetRandomNumber(int length){
        String number = "";
        Random random = new Random();
        for (int i = 0; i < length; i++){
            number += String.valueOf(random.nextInt(10));
        }

        return number;
    }

    class CreateMobileCard extends RequestServerAPI implements RequestServerFunction {
        public CreateMobileCard(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if (code == ErrorCode.SUCCESS.GetValue()){
                Log.d("TAG", "CheckReturnCode: success");
                return true;
            }
            Log.d("TAG", "CheckReturnCode: fail ");

            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            operator = MobileCardOperator.FindOperator(jsonData.getString("cardtype"));
            amount = MobileCardAmount.FindAmount(String.valueOf(jsonData.getLong("amount")));
            Log.d("TAG", "DataHandle: create successfully");
//            firebaseDatabaseHandler.RegisterDataListener();
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }
}
