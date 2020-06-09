package com.example.ewalletexample.Server.api.order;

import android.os.Build;
import android.util.Log;

import com.example.ewalletexample.Server.api.VerifyPin.VerifyPinAPI;
import com.example.ewalletexample.Server.api.VerifyPin.VerifyResponse;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.model.StatisMonthTransaction;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.ResponseModelByKey;
import com.example.ewalletexample.utilies.HandleDateTime;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import androidx.annotation.RequiresApi;

public abstract class Order implements VerifyResponse , ResponseModelByKey<StatisMonthTransaction> {
    private FirebaseDatabaseHandler<StatisMonthTransaction> firebaseDatabaseHandler;
    protected String userid, amount, pin, transactionId,
            secretKeyString1, secretKeyString2, publicKeyString, encryptSecretKey1, encryptSecretKey2;
    protected long orderid, fee;
    protected Service service;
    protected SourceFund sourceFund;
    protected VerifyPinAPI verifyPinAPI;
    protected OrderResponse orderResponse;
    protected SecretKey secretKey1, secretKey2;
    protected PublicKey publicKey;
    protected long balance;

    protected Order(String userid, String pin, String amount, long fee, SourceFund sourceFund, Service service, OrderResponse orderResponse) {
        this.userid = userid;
        this.pin = pin;
        this.amount = amount;
        this.service = service;
        this.fee = fee;
        this.sourceFund = sourceFund;
        verifyPinAPI = new VerifyPinAPI(userid, pin, this);
        this.orderResponse = orderResponse;
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SetSecretKeyString(String publicKeyString, String secretKey1, String secretKey2){
        this.publicKeyString = publicKeyString;
        this.secretKeyString1 = secretKey1;
        this.secretKeyString2 = secretKey2;

        this.publicKey = SecurityUtils.generatePublicKey(publicKeyString);
        this.secretKey1 = SecurityUtils.generateAESKeyFromText(secretKeyString1);
        this.secretKey2 = SecurityUtils.generateAESKeyFromText(secretKeyString2);

        String encryptSecretKey1ByAES = SecurityUtils.encryptAES(this.secretKey2, secretKeyString1);
        encryptSecretKey1 = SecurityUtils.encryptRSA(publicKey, encryptSecretKey1ByAES);

        encryptSecretKey2 = SecurityUtils.encryptRSA(publicKey, secretKeyString2);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void StartCreateOrder(){
        verifyPinAPI.StartVerify(this.publicKeyString, this.secretKeyString1, this.secretKeyString1);
    }

    @Override
    public void VerifyPinResponse(boolean isSuccess) {
        if (isSuccess){
            try {
                VerifyPinSuccess();
            } catch (JSONException e){
                Log.d("TAG", "VerifyPinResponse: " + e.getMessage());
            }

        } else {
            orderResponse.response(false, ErrorCode.USER_PASSWORD_WRONG.GetValue(), null, 0);
        }
    }

    protected abstract void VerifyPinSuccess() throws JSONException;

    protected void CreateOrder(ServerAPI api, String json, String encryptHeader){
        new CreateOrder().execute(api.GetUrl(), json, encryptHeader);
    }

    protected abstract void CreateOrderSuccess() throws JSONException;

    protected void SubmitOrder(String json, String encryptHeader) {
        new SubmitOrder().execute(ServerAPI.SUBMIT_TRANSACTION.GetUrl(), json, encryptHeader);
    }

    protected abstract void SubmitOrderSuccess();

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void GetStausOrder(ServerAPI api){
        try {
            String[] arr = new String[]{"userid:"+userid,"orderid:"+orderid, "key:"+encryptSecretKey1,"secondKey:"+encryptSecretKey2};
            String header = userid + orderid + secretKeyString1 + secretKeyString2;
            String hashHeader = SecurityUtils.encryptHmacSha256(secretKey1, header);
            String encryptHeader = SecurityUtils.encryptAES(secretKey2, hashHeader);
            String encryptHeaderByRSA = SecurityUtils.encryptRSA(publicKey, encryptHeader);
            new GetStatusOrder().execute(api.GetUrl(), HandlerJsonData.ExchangeToJsonString(arr), encryptHeaderByRSA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected abstract void GetDataFromJsonFromStatusOrder(JSONObject json) throws JSONException;

    private void UpdateTransactionInFirebase() {
        firebaseDatabaseHandler.GetModelByKey(Symbol.CHILD_NAME_TRANSACTION, userid, StatisMonthTransaction.class, this);
    }

    @Override
    public void GetModel(StatisMonthTransaction transaction) {
        boolean hasObject = true;

        if(transaction == null){
            transaction = new StatisMonthTransaction();
            hasObject  = false;
        }
        try {
            String currentLastDay = HandleDateTime.GetCurrentStringLastDayOfMonth();

            if(transaction.hasDay(currentLastDay)){
                transaction.updateTransactionByDay(currentLastDay, service, amount);
            } else {
                transaction.addNewDay(currentLastDay, service, amount);
            }

            if(hasObject){
                Map<String, Object> map = transaction.mapObjectFirebaseDB();

                firebaseDatabaseHandler.UpdateData(Symbol.CHILD_NAME_TRANSACTION.GetValue(), userid, map);
            } else {
                firebaseDatabaseHandler.PushDataIntoDatabase(Symbol.CHILD_NAME_TRANSACTION.GetValue(), userid, transaction);
            }

            orderResponse.response(true, ErrorCode.SUCCESS.GetValue(), transactionId, balance);

        } catch (ParseException e) {
            Log.d("TAG", "GetModel: " + e.getMessage());
        } catch (Exception e){
            Log.d("TAG", "GetModel: " + e.getMessage());
        }
    }

    class CreateOrder extends RequestServerAPI implements RequestServerFunction {
        public CreateOrder(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if(code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }
            orderResponse.response(false, code, null, 0);
            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            orderid = jsonData.getLong("orderid");
            CreateOrderSuccess();
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }

    class SubmitOrder extends RequestServerAPI implements RequestServerFunction{
        public SubmitOrder(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if(code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }
            orderResponse.response(false, code, null, 0);
            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            transactionId = jsonData.getString("transactionId");
            balance = jsonData.getLong("balance");
            SubmitOrderSuccess();
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }

    class GetStatusOrder extends RequestServerAPI implements RequestServerFunction{

        public GetStatusOrder(){
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
            GetDataFromJsonFromStatusOrder(jsonData);
            UpdateTransactionInFirebase();
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }
}

