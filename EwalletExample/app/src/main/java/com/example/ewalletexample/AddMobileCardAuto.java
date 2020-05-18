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

import java.util.List;
import java.util.Map;
import java.util.Random;

public class AddMobileCardAuto extends AppCompatActivity  {
    Button btnAdd;
    MobileCardOperator operator;
    MobileCardAmount amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_automatic);
        btnAdd = findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeginAdd(20);
            }
        });
    }

    private int number;
    private int index;
    private MobileCardOperator[] operators;
    private int indexOperator;
    private List<MobileCardAmount> amounts;
    private int amountIndex;
    private void BeginAdd(int number) {
        Log.d("TAG", "BeginAdd: button clicked");
        operators = MobileCard.GetInstance().GetMobileCardOperator();
        indexOperator = 0;
        amounts = MobileCard.GetInstance().GetListAmountsByMobileCardOperator(operators[indexOperator]);
        amountIndex = 0;
        index = 1;
        this.number = number;
        try {
            String cardNumber = GetRandomNumber(15);
            String seri = GetRandomNumber(14);
            Log.d("TAG", "BeginAdd: has created");
            String[] arr = new String[]{"cardnumber:" + cardNumber, "serinumber:" + seri,
                    "cardtype:" + operators[indexOperator].GetMobileCode(), "amount:" + Long.valueOf(amounts.get(amountIndex).GetAmount())};
            String json = HandlerJsonData.ExchangeToJsonString(arr);
            new CreateMobileCard().execute(ServerAPI.CREATE_MOBILE_CARD.GetUrl(), json);
        }
        catch (JSONException e){
            Log.d("TAG", "BeginAdd: " + e.getMessage());
        }
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
            try{
                Thread.sleep(2000);
                operator = MobileCardOperator.FindOperator(jsonData.getString("cardtype"));
                amount = MobileCardAmount.FindAmount(String.valueOf(jsonData.getLong("amount")));
                Log.d("TAG", "DataHandle: create successfully");
                if (index == number){
                    index = 0;
                    if(amountIndex == amounts.size() - 1){
                        amountIndex = 0;
                        if (indexOperator == operators.length - 1){
                            return;
                        } else {
                            indexOperator += 1;
                        }
                    } else {
                        amountIndex += 1;
                    }
                } else {
                    index += 1;
                }
                String cardNumber = GetRandomNumber(15);
                String seri = GetRandomNumber(14);
                Log.d("TAG", "BeginAdd: has created");
                String[] arr = new String[]{"cardnumber:" + cardNumber, "serinumber:" + seri,
                        "cardtype:" + operators[indexOperator].GetMobileCode(), "amount:" + Long.valueOf(amounts.get(amountIndex).GetAmount())};
                String json = HandlerJsonData.ExchangeToJsonString(arr);
                new CreateMobileCard().execute(ServerAPI.CREATE_MOBILE_CARD.GetUrl(), json);
            } catch (InterruptedException e){
                Log.d("TAG", "DataHandle: " + e.getMessage());
            }
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }
}
