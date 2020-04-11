package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ewalletexample.Server.balance.BalanceResponse;
import com.example.ewalletexample.Server.order.TopupOrder;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.Code;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.service.code.CodeEditText;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONException;
import org.json.JSONObject;

public class SubmitOrderActivity extends AppCompatActivity implements BalanceResponse {

    String userid, amount, receiverid;
    long fee;
    int codeServiceType;
    short codeSourceFund;
    ProgressBarManager progressBarManager;
    View exchangeMoneyLayout, withdrawView, topupView, sourceFundLayout, verifyPasswordLayout;
    TextView tvAmount, tvTotalAmount, tvFeeTransaction, tvBackInDetailTransaction, tvBackInSourceFund, tvTopupBankFrom;
    EditText etPass01, etPass02, etPass03, etPass04, etPass05, etPass06;
    CodeEditText codePassword;
    BankInfo bankInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_order);

        Initialize();
        GetValueFromIntent();
        ShowLayoutByServiceCode();
        InitializeText();
    }

    void Initialize(){
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar));

        exchangeMoneyLayout = findViewById(R.id.exchangeMoneyLayout);
        withdrawView = findViewById(R.id.withdrawLayout);
        topupView = findViewById(R.id.topupLayout);
        verifyPasswordLayout = findViewById(R.id.verifyPasswordLayout);
        sourceFundLayout = findViewById(R.id.sourceFundLayout);

        tvAmount = findViewById(R.id.tvAmount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvFeeTransaction = findViewById(R.id.tvFeeTransaction);
        tvBackInDetailTransaction = findViewById(R.id.tvBackInDetailTransaction);
        tvBackInSourceFund = findViewById(R.id.tvBackInSourceFund);
        tvTopupBankFrom = findViewById(R.id.tvTopupBankFrom);

        etPass01 = findViewById(R.id.etPass01);
        etPass02 = findViewById(R.id.etPass02);
        etPass03 = findViewById(R.id.etPass03);
        etPass04 = findViewById(R.id.etPass04);
        etPass05 = findViewById(R.id.etPass05);
        etPass06 = findViewById(R.id.etPass06);

        codePassword = new CodeEditText(1, etPass01, etPass02, etPass03, etPass04, etPass05, etPass06);
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        amount = intent.getStringExtra(Symbol.AMOUNT_TRANSACTION.GetValue());
        fee = intent.getLongExtra(Symbol.FEE_TRANSACTION.GetValue(), 0);
        codeServiceType = intent.getIntExtra(Symbol.SERVICE_TYPE.GetValue(), 0);
        codeSourceFund = intent.getShortExtra(Symbol.SOURCE_OF_FUND.GetValue(), Short.parseShort("0"));

        if(codeServiceType == Code.EXCHANGE_SERVICE_TYPE.GetCode()){
            receiverid = intent.getStringExtra(Symbol.RECEIVER_ID.GetValue());
        }
        else if(codeServiceType == Code.TOPUP_SERVICE_TYPE.GetCode()){
            try{
                bankInfo = new BankInfo(intent.getStringExtra(Symbol.BANK_INFO.GetValue()));
            }catch (JSONException e){
                Log.w("TAG", "GetValueFromIntent: " + e.getMessage());
            }
        }
    }

    void InitializeText(){
        if(codeServiceType == Code.TOPUP_SERVICE_TYPE.GetCode()){
            tvTopupBankFrom.setText(bankInfo.getCardName());
        }
        tvAmount.setText(amount + "đ");
        if(fee == 0){
            tvFeeTransaction.setText("Free");
        }else{
            tvFeeTransaction.setText(fee + "đ");
        }
        tvTotalAmount.setText((amount + fee) + "đ");
    }

    void ShowLayoutByServiceCode(){
        if(codeServiceType == Code.TOPUP_SERVICE_TYPE.GetCode()){
            ShowTopupLayout();
        } else if(codeServiceType == Code.EXCHANGE_SERVICE_TYPE.GetCode()){

        } else if(codeServiceType == Code.WITHDRAW_SERVICE_TYPE.GetCode()){

        } else if (codeServiceType == Code.MOBILE_CARD_SERVICE_TYPE.GetCode()){

        }
    }

    void ShowWithdrawLayout(){
        withdrawView.setVisibility(View.VISIBLE);
        topupView.setVisibility(View.GONE);
        exchangeMoneyLayout.setVisibility(View.GONE);
        sourceFundLayout.setVisibility(View.GONE);
        tvBackInDetailTransaction.setVisibility(View.VISIBLE);
        tvBackInSourceFund.setVisibility(View.GONE);
    }

    void ShowTopupLayout(){
        withdrawView.setVisibility(View.GONE);
        topupView.setVisibility(View.VISIBLE);
        exchangeMoneyLayout.setVisibility(View.GONE);
        sourceFundLayout.setVisibility(View.VISIBLE);
        tvBackInDetailTransaction.setVisibility(View.GONE);
        tvBackInSourceFund.setVisibility(View.VISIBLE);
    }

    void ShowExchangeMoneyLayout(){
        withdrawView.setVisibility(View.GONE);
        topupView.setVisibility(View.GONE);
        exchangeMoneyLayout.setVisibility(View.VISIBLE);
        sourceFundLayout.setVisibility(View.GONE);
        tvBackInDetailTransaction.setVisibility(View.VISIBLE);
        tvBackInSourceFund.setVisibility(View.GONE);
    }

    public void SubmitOrderTransaction(View view){
        verifyPasswordLayout.setVisibility(View.VISIBLE);
    }

    public void RefuseVerifyPin(View view){
        verifyPasswordLayout.setVisibility(View.GONE);
    }

    public void VerifyPin(View view) throws JSONException {
        progressBarManager.ShowProgressBar("Loading");
        String pin = codePassword.GetCombineText();

        if(pin.isEmpty()){
            Toast.makeText(this, "Nhập mã pin", Toast.LENGTH_SHORT).show();
            progressBarManager.HideProgressBar();
            return;
        }

        TopupOrder topupOrder = new TopupOrder(userid,pin,amount,fee,codeSourceFund,bankInfo.ExchangeToJsonData(), this);
        topupOrder.StartCreateTopupOrder();
    }

    @Override
    public void GetBalanceResponse(long balance) {

    }
}
