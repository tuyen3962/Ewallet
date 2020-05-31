package com.example.ewalletexample;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aldoapps.autoformatedittext.AutoFormatEditText;
import com.example.ewalletexample.Server.api.bank.BankMappingCallback;
import com.example.ewalletexample.Server.api.bank.list.ListBankConnectedAPI;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.service.recycleview.listbank.RecycleViewListBankConnected;
import com.example.ewalletexample.service.recycleview.listbank.UserSelectBankConnect;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.toolbar.CustomToolbarContext;
import com.example.ewalletexample.utilies.Utilies;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;

public class ServiceWalletActivity extends AppCompatActivity implements BankMappingCallback<List<BankInfo>>, UserSelectBankConnect {
    private static final int currentHolderChosenColor = R.color.Grey, defaultHolderColor = R.color.White;

    FirebaseStorageHandler firebaseStorageHandler;
    RecycleViewListBankConnected recycleViewListBankConnected;
    String userid, secretKeyString1, secretKeyString2;
    long userAmount;
    View sourceFundLayout;
    List<BankInfo> bankInfoList;
    ProgressBarManager progressBarManager;
    RecyclerView rvListBankConnected;
    AutoFormatEditText etBalance;
    TextView tvErrorBalance;
    View layoutHintMoney;
    MaterialTextView tvHintMoney1, tvHintMoney2, tvHintMoney3;
    BankInfo currentBankConnect;
    Service service;
    ListBankConnectedAPI listBankConnectedAPI;
    RecycleViewListBankConnected.ListBankConnectViewHolder currentHolder;
    CustomToolbarContext customToolbarContext;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if (s.length() == 0){
                if (layoutHintMoney.getVisibility() == View.VISIBLE){
                    layoutHintMoney.setVisibility(View.GONE);
                }
            } else {
                if (layoutHintMoney.getVisibility() == View.GONE){
                    layoutHintMoney.setVisibility(View.VISIBLE);
                }
            }
            PredictText(text.replaceAll(",",""));
        }
    };

    void PredictText(String money){
        if (money.length() >= 6){
            money = money.substring(0,6);
            tvHintMoney1.setText(Utilies.HandleBalanceTextView(money + "0"));
            tvHintMoney2.setText(Utilies.HandleBalanceTextView(money + "00"));
            tvHintMoney3.setText(Utilies.HandleBalanceTextView(money + "000"));
        } else if (money.length() == 5){
            tvHintMoney1.setText(Utilies.HandleBalanceTextView(money + "00"));
            tvHintMoney2.setText(Utilies.HandleBalanceTextView(money + "000"));
            tvHintMoney3.setText(Utilies.HandleBalanceTextView(money + "0000"));
        } else {
            tvHintMoney1.setText(Utilies.HandleBalanceTextView(money + "000"));
            tvHintMoney2.setText(Utilies.HandleBalanceTextView(money + "0000"));
            tvHintMoney3.setText(Utilies.HandleBalanceTextView(money + "00000"));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_wallet);
        Initialize();
        GetValueFromIntent();
        LoadListBankConnected();
    }

    void Initialize(){
        rvListBankConnected = findViewById(R.id.listBankAccountConnect);
        etBalance = findViewById(R.id.etBalance);
        tvErrorBalance = findViewById(R.id.tvErrorBalance);
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar));
        sourceFundLayout = findViewById(R.id.sourceFundLayout);
        firebaseStorageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        etBalance.addTextChangedListener(textWatcher);
        layoutHintMoney = findViewById(R.id.layoutHintMoney);
        tvHintMoney1 = findViewById(R.id.tvHintMoney1);
        tvHintMoney2 = findViewById(R.id.tvHintMoney2);
        tvHintMoney3 = findViewById(R.id.tvHintMoney3);
        layoutHintMoney.setVisibility(View.GONE);
        customToolbarContext = new CustomToolbarContext(this, this::BackToMain);
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        userAmount = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
        this.service = Service.Find(intent.getIntExtra(Symbol.SERVICE_TYPE.GetValue(), 0));
        customToolbarContext.SetTitle(this.service.GetName());
        secretKeyString1 = intent.getStringExtra(Symbol.SECRET_KEY_01.GetValue());
        secretKeyString2 = intent.getStringExtra(Symbol.SECRET_KEY_02.GetValue());
        sourceFundLayout.setVisibility(View.VISIBLE);
        listBankConnectedAPI = new ListBankConnectedAPI(this, userid);
    }

    public void StartTransaction(View view){
        String amountTransaction = etBalance.getText().toString();
        amountTransaction = amountTransaction.replaceAll(",","");
        if (amountTransaction.isEmpty()){
            etBalance.setError("Nhập số tiền bạn muốn thực hiện giao dịch");
            return;
        } else if(currentBankConnect == null){
            etBalance.setError("Chọn ngân hàng để thực hiện giao dịch");
            return;
        } else if(service == Service.WITHDRAW_SERVICE_TYPE && Long.valueOf(amountTransaction) > userAmount){
            etBalance.setError("Số tiền trong ví không đủ");
            return;
        } else if (Long.valueOf(amountTransaction) < 999){
            etBalance.setError("Giao dịch phải thực hiện trên 1,000đ");
            return;
        }

        Intent intent = new Intent(ServiceWalletActivity.this, SubmitOrderActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.AMOUNT_TRANSACTION.GetValue(), amountTransaction);
        intent.putExtra(Symbol.FEE_TRANSACTION.GetValue(), 0);
        intent.putExtra(Symbol.SERVICE_TYPE.GetValue(), service.GetCode());

        if(service == Service.TOPUP_SERVICE_TYPE){
            intent.putExtra(Symbol.SOURCE_OF_FUND.GetValue(), SourceFund.ATM_SOURCE_FUND.GetCode());
        } else if(service == Service.WITHDRAW_SERVICE_TYPE){
            intent.putExtra(Symbol.SOURCE_OF_FUND.GetValue(), SourceFund.WALLET_SOURCE_FUND.GetCode());
        }

        intent.putExtra(Symbol.BANK_INFO.GetValue(), new Gson().toJson(currentBankConnect));

        startActivityForResult(intent, RequestCode.SUBMIT_ORDER);
    }

    public void ClearAmounTransactionText(View view){
        etBalance.setText("");
    }

    public void BackToMain(){
        setResult(RESULT_CANCELED);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void LoadListBankConnected(){
        progressBarManager.ShowProgressBar("Loading");
        listBankConnectedAPI.GetListBank(getString(R.string.public_key), secretKeyString1, secretKeyString2);
    }

    @Override
    public void MappingResponse(boolean response, List<BankInfo> callback) {
        if (response){
            this.bankInfoList = callback;
            recycleViewListBankConnected = new RecycleViewListBankConnected(this, firebaseStorageHandler, this, bankInfoList);
            rvListBankConnected.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rvListBankConnected.setAdapter(recycleViewListBankConnected);
        }

        progressBarManager.HideProgressBar();
    }

    @Override
    public void SelectBankInfo(BankInfo info, RecycleViewListBankConnected.ListBankConnectViewHolder holder) {
        if(currentHolder != null && !currentHolder.equals(holder)){
            currentHolder.SetBackgroundColor(defaultHolderColor);
        }

        this.currentHolder = holder;
        this.currentHolder.SetBackgroundColor(currentHolderChosenColor);
        this.currentBankConnect = info;
    }

    public void ClickHintMoney(View view){
        if (view.getId() == tvHintMoney1.getId()){
            etBalance.setText(tvHintMoney1.getText().toString());
        } else if (view.getId() == tvHintMoney2.getId()){
            etBalance.setText(tvHintMoney2.getText().toString());
        } else{
            etBalance.setText(tvHintMoney3.getText().toString());
        }
        etBalance.setSelection(etBalance.length() - 1);
        layoutHintMoney.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.SUBMIT_ORDER){
            if (resultCode == RESULT_OK){
                setResult(resultCode);
                finish();
            }
        }
    }
}
