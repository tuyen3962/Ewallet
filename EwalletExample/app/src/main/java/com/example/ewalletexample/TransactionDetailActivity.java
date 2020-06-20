package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ewalletexample.Server.api.transaction.model.BankInfo;
import com.example.ewalletexample.Server.api.transaction.model.ExchangeInfo;
import com.example.ewalletexample.Server.api.transaction.model.MobileCardInfo;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.TransactionHistory;
import com.example.ewalletexample.service.mobilecard.MobileCardOperator;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.toolbar.CustomToolbarContext;
import com.example.ewalletexample.service.toolbar.ToolbarEvent;
import com.example.ewalletexample.utilies.HandleDateTime;
import com.example.ewalletexample.utilies.Utilies;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

public class TransactionDetailActivity extends AppCompatActivity implements ToolbarEvent {

    FirebaseStorageHandler storageHandler;
    MaterialTextView tvService, tvAmount, tvStatusTransaction, tvSourceFund, tvFeeTransaction,
            tvTransactionId, tvChargeTime, tvCardNumber, tvSeriNumber, tvBankName, tvCardNo,
            tvSenderName, tvReceiverName, tvNote, tvSupplier, tvCustomerName, tvIdCustomer, tvTermTransaction,
            tvMobileCardOperator, tvMobileCardAmount, tvBuyOneMoreCard;
    ImageView imgCard;
    String transaction_detail;
    View mobileCardLayout, exchanegMoneyLayout, supportInformationLayout, bankCardLayout;
    Button btnMainActivity, btnServiceAgain;
    TransactionHistory transactionHistory;
    CustomToolbarContext customToolbarContext;
    Gson gson;
    Service service;
    SourceFund sourceFund;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        Initialize();
        GetValueFromIntent();
        FillTransactionDetail();
    }

    void Initialize(){
        storageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        gson = new Gson();
        tvService = findViewById(R.id.tvService);
        tvAmount = findViewById(R.id.tvAmount);
        tvStatusTransaction = findViewById(R.id.tvStatusTransaction);
        tvSourceFund = findViewById(R.id.tvSourceFund);
        tvFeeTransaction = findViewById(R.id.tvFeeTransaction);
        tvTransactionId = findViewById(R.id.tvTransactionId);
        tvChargeTime = findViewById(R.id.tvChargeTime);
        tvCardNumber = findViewById(R.id.tvCardNumber);
        tvSeriNumber = findViewById(R.id.tvSeriNumber);
        tvBankName = findViewById(R.id.tvBankName);
        tvCardNo = findViewById(R.id.tvCardNo);
        tvSenderName = findViewById(R.id.tvSenderName);
        tvReceiverName = findViewById(R.id.tvReceiverName);
        tvNote = findViewById(R.id.tvNote);
        tvSupplier = findViewById(R.id.tvSupplier);
        tvTermTransaction = findViewById(R.id.tvTermTransaction);
        tvIdCustomer = findViewById(R.id.tvIdCustomer);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        mobileCardLayout = findViewById(R.id.cardLayout);
        exchanegMoneyLayout = findViewById(R.id.exchangeMoneyLayout);
        supportInformationLayout = findViewById(R.id.supportInformationLayout);
        bankCardLayout = findViewById(R.id.layoutBankCardInfo);
        tvMobileCardAmount = findViewById(R.id.tvMobileCardAmount);
        tvMobileCardOperator = findViewById(R.id.tvMobileCardOperator);
        imgCard = findViewById(R.id.imgMobileCard);
        tvBuyOneMoreCard = findViewById(R.id.tvBuyOneMoreCard);
        btnMainActivity = findViewById(R.id.btnMainActivity);
        btnServiceAgain = findViewById(R.id.btnServiceAgain);
        customToolbarContext = new CustomToolbarContext(this, this::BackToPreviousActivity);
        customToolbarContext.SetTitle("Chi tiết giao dịch");
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        transaction_detail = intent.getStringExtra(Symbol.STYLE_TRANSACTION_DETAIL.GetValue());
        if (transaction_detail.equalsIgnoreCase(Symbol.REVIEW.GetValue())){
            tvBuyOneMoreCard.setVisibility(View.GONE);
            btnMainActivity.setVisibility(View.GONE);
            btnServiceAgain.setVisibility(View.GONE);
        }
        transactionHistory = gson.fromJson(intent.getStringExtra(Symbol.TRANSACTION_DETAIL.GetValue()), TransactionHistory.class);
        service = Service.Find(transactionHistory.getServicetype());
        sourceFund = SourceFund.Find(Short.valueOf(String.valueOf(transactionHistory.getSourceoffund())));
        if (service == Service.TOPUP_SERVICE_TYPE){
            ShowServiceTopupLayout();
        } else if (service == Service.WITHDRAW_SERVICE_TYPE){
            ShowServiceWithdrawLayout();
        } else if (service == Service.EXCHANGE_SERVICE_TYPE) {
            ShowServiceExchangeMoneyLayout();
        } else if (service == Service.MOBILE_CARD_SERVICE_TYPE){
            ShowServiceMobileCardLayout();
        }
    }

    void FillTransactionDetail(){
        tvService.setText(service.GetName());
        tvSourceFund.setText(sourceFund.GetName());
        tvFeeTransaction.setText("Miễn phí");
        tvTransactionId.setText(transactionHistory.getTransactionid() + "");
        tvChargeTime.setText(HandleDateTime.FormatStringMillisecondIntoDate(transactionHistory.getTimemilliseconds()));

        if (transactionHistory.getStatus() == ErrorCode.SUCCESS.GetValue()){
            tvStatusTransaction.setText("Giao dịch thành công");
        } else if(transactionHistory.getStatus() == ErrorCode.PROCESSING.GetValue()){
            tvStatusTransaction.setText("Giao dịch đang xử lý");
        } else if (transactionHistory.getStatus() == ErrorCode.EXCEPTION.GetValue()) {
            tvStatusTransaction.setText("Giao dịch thất bại");
        }

//        if (transactionHistory.get == 0){
//            tvFeeTransaction.setText("Miễn phí");
//        } else {
//            tvFeeTransaction.setText(Utilies.HandleBalanceTextView(String.valueOf(fee)) + " đ");
//        }
        Log.d("TAG", "FillTransactionDetail: " + transactionHistory.getAmount());
        tvAmount.setText(Utilies.HandleBalanceTextView(String.valueOf(transactionHistory.getAmount())) +" đ");
    }

    void ShowServiceMobileCardLayout(){
        exchanegMoneyLayout.setVisibility(View.GONE);
        mobileCardLayout.setVisibility(View.VISIBLE);
        supportInformationLayout.setVisibility(View.GONE);
        bankCardLayout.setVisibility(View.GONE);
        MobileCardInfo info = gson.fromJson(transactionHistory.getTxndetail(), MobileCardInfo.class);
        tvCardNumber.setText(info.getCardNumber());
        tvSeriNumber.setText(info.getSeriNumber());
        MobileCardOperator operator = MobileCardOperator.FindOperator(info.getCardType());
        tvMobileCardOperator.setText(operator.GetMobileCardName());
        tvMobileCardAmount.setText(Utilies.HandleBalanceTextView(info.getAmount()));
        storageHandler.LoadAccountImageFromLink(operator.GetMobileLinkImage(), imgCard);
        btnServiceAgain.setVisibility(View.GONE);
    }

    void ShowServiceExchangeMoneyLayout(){
        exchanegMoneyLayout.setVisibility(View.VISIBLE);
        mobileCardLayout.setVisibility(View.GONE);
        supportInformationLayout.setVisibility(View.GONE);
        bankCardLayout.setVisibility(View.GONE);
        ExchangeInfo info = gson.fromJson(transactionHistory.getTxndetail(), ExchangeInfo.class);
        tvSenderName.setText(info.getSenderName());
        tvReceiverName.setText(info.getReceiverName());
        tvNote.setText(info.getNote());
        btnServiceAgain.setText("Chuyển tiền tiếp");
    }

    void ShowServiceTopupLayout(){
        exchanegMoneyLayout.setVisibility(View.GONE);
        mobileCardLayout.setVisibility(View.GONE);
        supportInformationLayout.setVisibility(View.GONE);
        bankCardLayout.setVisibility(View.VISIBLE);
        BankInfo info = gson.fromJson(transactionHistory.getTxndetail(), BankInfo.class);
        tvBankName.setText(info.getBankName());
        tvCardNo.setText(info.getCardNo());
        btnServiceAgain.setText("Nạp tiếp");
    }

    void ShowServiceWithdrawLayout(){
        exchanegMoneyLayout.setVisibility(View.GONE);
        mobileCardLayout.setVisibility(View.GONE);
        supportInformationLayout.setVisibility(View.GONE);
        bankCardLayout.setVisibility(View.VISIBLE);
        BankInfo info = gson.fromJson(transactionHistory.getTxndetail(), BankInfo.class);
        tvBankName.setText(info.getBankName());
        tvCardNo.setText(info.getCardNo());
        btnServiceAgain.setText("Rút tiếp");
    }

    public void BuyNewMobileCard(View view){
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void BackToPreviousActivity() {
        setResult(RESULT_OK);
        finish();
    }
}
