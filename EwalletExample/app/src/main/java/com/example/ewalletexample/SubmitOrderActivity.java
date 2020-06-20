package com.example.ewalletexample;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.example.ewalletexample.Server.api.order.ExchangeMoneyOrder;
import com.example.ewalletexample.Server.api.order.MobileCardOrder;
import com.example.ewalletexample.Server.api.order.OrderResponse;
import com.example.ewalletexample.Server.api.order.TopupOrder;
import com.example.ewalletexample.Server.api.order.WithdrawOrder;
import com.example.ewalletexample.Server.api.transaction.single.TransactionDetailAPI;
import com.example.ewalletexample.Server.api.transaction.single.TransactionDetailResponse;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.mobilecard.MobileCardAmount;
import com.example.ewalletexample.service.mobilecard.MobileCardOperator;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.ResponseModelByKey;
import com.example.ewalletexample.utilies.Utilies;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONException;

public class SubmitOrderActivity extends AppCompatActivity implements OrderResponse, ResponseModelByKey<UserModel>, TransactionDetailResponse {
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    String userid, amount, receiverphone, receiverFullname, phone, note, secretKeyString1, secretKeyString2;
    long fee;
    Service service;
    SourceFund source;
    ProgressBarManager progressBarManager;
    View exchangeMoneyLayout, withdrawView, topupView, verifyPasswordLayout, mobileCardLayout, cardNumberLayout;
    TextView tvAmount, tvTotalAmount, tvFeeTransaction, tvTopupBankFrom, tvWithdrawBankName,
             tvFullName, tvPhone, tvMobileCard, tvQuantity, tvAmountText, tvCardNo;
    BankInfo bankInfo;
    MobileCardOperator mobileCardOperator;
    MobileCardAmount mobileCardAmount;
    PasswordFieldFragment passwordFieldFragment;
    NumberKeyboard keyboard;
    Toolbar toolbar;
    ImageButton btnBack;
    Gson gson;
    UserModel userModel;
    TransactionDetailAPI transactionDetail;
    long balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_order);

        Initialize();
        GetValueFromIntent();
        ShowLayoutByServiceCode();
        InitializeText();
    }

    @Override
    public void GetModel(UserModel data) {
        userModel = data;
    }

    void Initialize(){
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar));
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference());
        exchangeMoneyLayout = findViewById(R.id.exchangeMoneyLayout);
        withdrawView = findViewById(R.id.withdrawLayout);
        topupView = findViewById(R.id.topupLayout);
        verifyPasswordLayout = findViewById(R.id.verifyPasswordLayout);
        cardNumberLayout = findViewById(R.id.cardNumberLayout);
        tvCardNo = findViewById(R.id.tvCardno);
        tvAmount = findViewById(R.id.tvAmount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvFeeTransaction = findViewById(R.id.tvFeeTransaction);
        tvTopupBankFrom = findViewById(R.id.tvTopupBankFrom);
        tvWithdrawBankName = findViewById(R.id.tvWithdrawBankName);
        tvFullName = findViewById(R.id.tvFullName);
        tvPhone = findViewById(R.id.tvPhone);
        keyboard = findViewById(R.id.keyboard);
        mobileCardLayout = findViewById(R.id.mobileCardLayout);
        tvMobileCard = findViewById(R.id.tvMobileCardOperator);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvAmountText = findViewById(R.id.tvAmountText);
        gson = new Gson();
        passwordFieldFragment = PasswordFieldFragment.newInstance("Nhập mật khẩu");
        toolbar = findViewById(R.id.toolbarLayout);
        btnBack = findViewById(R.id.btnBackToPreviousActivity);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        keyboard.setListener(new NumberKeyboardListener() {
            @Override
            public void onNumberClicked(int i) {
                passwordFieldFragment.CheckIncreaseIndex(String.valueOf(i));
            }

            @Override
            public void onLeftAuxButtonClicked() {
                HideKeyBoard();
            }

            @Override
            public void onRightAuxButtonClicked() {
                passwordFieldFragment.CheckDecreaseIndex();
            }
        });

        Utilies.AddFragmentIntoActivity(this, R.id.passwordLayout, passwordFieldFragment, "PASSWORD");
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        amount = intent.getStringExtra(Symbol.AMOUNT_TRANSACTION.GetValue());
        fee = intent.getLongExtra(Symbol.FEE_TRANSACTION.GetValue(), 0);
        service = Service.Find(intent.getIntExtra(Symbol.SERVICE_TYPE.GetValue(), 0));
        source = SourceFund.Find(intent.getShortExtra(Symbol.SOURCE_OF_FUND.GetValue(), Short.parseShort("0")));
        secretKeyString1 = intent.getStringExtra(Symbol.SECRET_KEY_01.GetValue());
        secretKeyString2 = intent.getStringExtra(Symbol.SECRET_KEY_02.GetValue());
        firebaseDatabaseHandler.GetModelByKey(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE, userid, UserModel.class, this);
        if(service == Service.EXCHANGE_SERVICE_TYPE){
            receiverphone = intent.getStringExtra(Symbol.RECEIVER_PHONE.GetValue());
            receiverFullname = intent.getStringExtra(Symbol.RECEIVER_FULL_NAME.GetValue());
            note = intent.getStringExtra(Symbol.NOTE.GetValue());
        }
        else if(service == Service.TOPUP_SERVICE_TYPE || service == Service.WITHDRAW_SERVICE_TYPE){
            bankInfo = gson.fromJson(intent.getStringExtra(Symbol.BANK_INFO.GetValue()), BankInfo.class);
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
            tvCardNo.setText("************"+bankInfo.getL4CardNo());
        }else if(service == Service.WITHDRAW_SERVICE_TYPE){
            tvWithdrawBankName.setText(bankInfo.getCardName());
            tvCardNo.setText("************"+bankInfo.getL4CardNo());
        }
        else if(service == Service.EXCHANGE_SERVICE_TYPE){
            tvPhone.setText(receiverphone);
            tvFullName.setText(receiverFullname);
        } else if (service == Service.MOBILE_CARD_SERVICE_TYPE){
            tvMobileCard.setText(mobileCardOperator.GetMobileCardName());
            tvAmountText.setText("Mệnh giá");
        }
        tvAmount.setText(Utilies.HandleBalanceTextView(amount) + "đ");
        if(fee == 0){
            tvFeeTransaction.setText("Free");
            tvTotalAmount.setText(Utilies.HandleBalanceTextView(amount) + "đ");
        }else{
            tvFeeTransaction.setText(fee + "đ");
            long total= Long.parseLong(amount) + fee;
            tvTotalAmount.setText(total + "đ");
        }
    }

    public void ShowNumberKeyBoard(){
        keyboard.setVisibility(View.VISIBLE);
    }

    public void HideKeyBoard(){
        keyboard.setVisibility(View.GONE);
        passwordFieldFragment.DisablePasswordField();
    }

    void ShowLayoutByServiceCode(){
        if(service == Service.TOPUP_SERVICE_TYPE){
            ShowTopupLayout();
        } else if(service == Service.EXCHANGE_SERVICE_TYPE){
            ShowExchangeMoneyLayout();
        } else if(service == Service.WITHDRAW_SERVICE_TYPE){
            ShowWithdrawLayout();
        } else if (service == Service.MOBILE_CARD_SERVICE_TYPE){
            ShowMobileCardLayout();
        }
    }

    void ShowWithdrawLayout(){
        withdrawView.setVisibility(View.VISIBLE);
        topupView.setVisibility(View.GONE);
        exchangeMoneyLayout.setVisibility(View.GONE);
        mobileCardLayout.setVisibility(View.GONE);
        cardNumberLayout.setVisibility(View.VISIBLE);
    }

    void ShowTopupLayout(){
        withdrawView.setVisibility(View.GONE);
        topupView.setVisibility(View.VISIBLE);
        exchangeMoneyLayout.setVisibility(View.GONE);
        mobileCardLayout.setVisibility(View.GONE);
        cardNumberLayout.setVisibility(View.VISIBLE);
    }

    void ShowExchangeMoneyLayout(){
        withdrawView.setVisibility(View.GONE);
        topupView.setVisibility(View.GONE);
        exchangeMoneyLayout.setVisibility(View.VISIBLE);
        mobileCardLayout.setVisibility(View.GONE);
        cardNumberLayout.setVisibility(View.GONE);
    }

    void ShowMobileCardLayout(){
        exchangeMoneyLayout.setVisibility(View.GONE);
        withdrawView.setVisibility(View.GONE);
        topupView.setVisibility(View.GONE);
        mobileCardLayout.setVisibility(View.VISIBLE);
        cardNumberLayout.setVisibility(View.GONE);
    }

    public void SubmitOrderTransaction(View view){
        verifyPasswordLayout.setVisibility(View.VISIBLE);
    }

    public void RefuseVerifyPin(View view){
        verifyPasswordLayout.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void VerifyPin(View view) throws JSONException {
        progressBarManager.ShowProgressBar("Loading");
        String pin = passwordFieldFragment.getTextByImage();

        if(pin.isEmpty()){
            Toast.makeText(this, "Nhập mã pin", Toast.LENGTH_SHORT).show();
            progressBarManager.HideProgressBar();
            return;
        }

        HideKeyBoard();

        CheckOrder(pin);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void CheckOrder(String pin) {
        if (service == Service.TOPUP_SERVICE_TYPE){
            TopupOrder topupOrder = new TopupOrder(userid,pin,amount,fee,source,bankInfo, this);
            topupOrder.SetSecretKeyString(getString(R.string.public_key), secretKeyString1, secretKeyString2);
            topupOrder.StartCreateOrder();
        }
        else if(service == Service.WITHDRAW_SERVICE_TYPE){
            WithdrawOrder withdrawOrder = new WithdrawOrder(userid, pin, amount, fee, source, bankInfo, this);
            withdrawOrder.SetSecretKeyString(getString(R.string.public_key), secretKeyString1, secretKeyString2);
            withdrawOrder.StartCreateOrder();
        }
        else if(service == Service.EXCHANGE_SERVICE_TYPE){
            ExchangeMoneyOrder exchangeMoneyOrder = new ExchangeMoneyOrder(userid, receiverphone, pin, amount, fee, source, note, this);
            exchangeMoneyOrder.SetSecretKeyString(getString(R.string.public_key), secretKeyString1, secretKeyString2);
            exchangeMoneyOrder.StartCreateOrder();
        }
        else if(service == Service.MOBILE_CARD_SERVICE_TYPE){
            MobileCardOrder order = new MobileCardOrder(userid, pin, mobileCardAmount, mobileCardOperator, fee, source, this);
            order.SetSecretKeyString(getString(R.string.public_key), secretKeyString1, secretKeyString2);
            order.StartCreateOrder();
        }
    }

    @Override
    public void response(boolean isSuccess, int code, String transactionId, long balance) {

        if(isSuccess && code == ErrorCode.SUCCESS.GetValue()){
            transactionDetail = new TransactionDetailAPI(userid, transactionId, this);
            this.balance = balance;
            transactionDetail.StartRequest(getString(R.string.public_key), secretKeyString1, secretKeyString2);
        } else {
            if (code == ErrorCode.USER_PASSWORD_WRONG.GetValue()){
                progressBarManager.HideProgressBar();
                Toast.makeText(this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void TransactionResponse(String json) {
        Intent intent = new Intent(SubmitOrderActivity.this, TransactionDetailActivity.class);
        intent.putExtra(Symbol.STYLE_TRANSACTION_DETAIL.GetValue(), Symbol.RESULT.GetValue());
        intent.putExtra(Symbol.TRANSACTION_DETAIL.GetValue(), json);
        startActivityForResult(intent, RequestCode.SUBMIT_ORDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.SUBMIT_ORDER){
            setResult(resultCode);
            finish();
        }
    }
}
