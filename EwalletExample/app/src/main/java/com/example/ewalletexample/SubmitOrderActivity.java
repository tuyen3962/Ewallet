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
import com.example.ewalletexample.Server.order.ExchangeMoneyOrder;
import com.example.ewalletexample.Server.order.MobileCardOrder;
import com.example.ewalletexample.Server.order.MobileCardOrderResponse;
import com.example.ewalletexample.Server.order.TopupOrder;
import com.example.ewalletexample.Server.order.WithdrawOrder;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.service.code.CodeEditText;
import com.example.ewalletexample.service.mobilecard.MobileCardAmount;
import com.example.ewalletexample.service.mobilecard.MobileCardOperator;

import org.json.JSONException;

public class SubmitOrderActivity extends AppCompatActivity implements BalanceResponse, MobileCardOrderResponse {

    String userid, amount, receiverphone, receiverFullname, phone;
    long fee;
    Service service;
    SourceFund source;
    ProgressBarManager progressBarManager;
    View exchangeMoneyLayout, withdrawView, topupView, sourceFundLayout, verifyPasswordLayout;
    TextView tvAmount, tvTotalAmount, tvFeeTransaction, tvBackInDetailTransaction, tvBackInSourceFund, tvTopupBankFrom, tvWithdrawBankName, tvFullName, tvPhone;
    EditText etPass01, etPass02, etPass03, etPass04, etPass05, etPass06;
    CodeEditText codePassword;
    BankInfo bankInfo;
    MobileCardOperator mobileCardOperator;
    MobileCardAmount mobileCardAmount;

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
        tvWithdrawBankName = findViewById(R.id.tvWithdrawBankName);
        tvFullName = findViewById(R.id.tvFullName);
        tvPhone = findViewById(R.id.tvPhone);

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
        service = Service.Find(intent.getIntExtra(Symbol.SERVICE_TYPE.GetValue(), 0));
        source = SourceFund.Find(intent.getShortExtra(Symbol.SOURCE_OF_FUND.GetValue(), Short.parseShort("0")));

        if(service == Service.EXCHANGE_SERVICE_TYPE){
            receiverphone = intent.getStringExtra(Symbol.RECEIVER_PHONE.GetValue());
            receiverFullname = intent.getStringExtra(Symbol.RECEIVER_FULL_NAME.GetValue());
        }
        else if(service == Service.TOPUP_SERVICE_TYPE || service == Service.WITHDRAW_SERVICE_TYPE){
            try{
                bankInfo = new BankInfo(intent.getStringExtra(Symbol.BANK_INFO.GetValue()));
            }catch (JSONException e){
                Log.w("TAG", "GetValueFromIntent: " + e.getMessage());
            }
        }
        else if(service == Service.MOBILE_CARD_SERVICE_TYPE){
            phone = intent.getStringExtra(Symbol.PHONE.GetValue());
            mobileCardOperator = MobileCardOperator.FindOperator(intent.getStringExtra(Symbol.MOBILE_CODE.GetValue()));
            mobileCardAmount = MobileCardAmount.FindAmount(intent.getStringExtra(Symbol.MOBILE_AMOUNT.GetValue()));
        }
    }

    void InitializeText(){
        if(service == Service.TOPUP_SERVICE_TYPE){
            tvTopupBankFrom.setText(bankInfo.getCardName());
        }else if(service == Service.WITHDRAW_SERVICE_TYPE){
            tvWithdrawBankName.setText(bankInfo.getCardName());
        }
        else if(service == Service.EXCHANGE_SERVICE_TYPE){
            tvPhone.setText(receiverphone);
            tvFullName.setText(receiverFullname);
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
        if(service == Service.TOPUP_SERVICE_TYPE){
            ShowTopupLayout();
        } else if(service == Service.EXCHANGE_SERVICE_TYPE){
            ShowExchangeMoneyLayout();
        } else if(service == Service.WITHDRAW_SERVICE_TYPE){
            ShowWithdrawLayout();
        } else if (service == Service.MOBILE_CARD_SERVICE_TYPE){

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

        CheckOrder(pin);
    }

    void CheckOrder(String pin) throws JSONException{
        if (service == Service.TOPUP_SERVICE_TYPE){
            TopupOrder topupOrder = new TopupOrder(userid,pin,amount,fee,source.GetCode(),bankInfo.ExchangeToJsonData(), this);
            topupOrder.StartCreateTopupOrder();
        }
        else if(service == Service.WITHDRAW_SERVICE_TYPE){
            WithdrawOrder withdrawOrder = new WithdrawOrder(userid, pin, amount, fee, source.GetCode(), bankInfo.ExchangeToJsonData(), this);
            withdrawOrder.StartCreateWithdrawOrder();
        }
        else if(service == Service.EXCHANGE_SERVICE_TYPE){
            ExchangeMoneyOrder exchangeMoneyOrder = new ExchangeMoneyOrder(userid, receiverphone, pin, amount, fee, source, this);
            exchangeMoneyOrder.StartCreateExchangeMoneyOrder();
        }
        else if(service == Service.MOBILE_CARD_SERVICE_TYPE){
            MobileCardOrder order = new MobileCardOrder(userid, pin, mobileCardAmount, mobileCardOperator, fee, phone, source.GetCode(), this);
            order.StartCreateMobileCardOrder();
        }
    }

    @Override
    public void GetBalanceResponse(long balance) {

    }

    @Override
    public void ResponseMobileCard(String cardNumber, String seriNumber) {
        Log.d("TAG", "ResponseMobileCard: " + cardNumber + " " + seriNumber);
    }
}
