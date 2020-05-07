package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aldoapps.autoformatedittext.AutoFormatEditText;
import com.example.ewalletexample.Server.api.bank.BankMappingCallback;
import com.example.ewalletexample.Server.api.bank.list.ListBankConnectedAPI;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.service.recycleview.listbank.RecycleViewListBankConnected;
import com.example.ewalletexample.service.recycleview.listbank.UserSelectBankConnect;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class ServiceWalletActivity extends AppCompatActivity implements BankMappingCallback<List<BankInfo>>, UserSelectBankConnect {
    private static final int currentHolderChosenColor = R.color.Grey, defaultHolderColor = R.color.White;

    FirebaseStorageHandler firebaseStorageHandler;
    RecycleViewListBankConnected recycleViewListBankConnected;
    String userid;
    long userAmount;
    View sourceFundLayout;
    List<BankInfo> bankInfoList;
    ProgressBarManager progressBarManager;
    RecyclerView rvListBankConnected;
    AutoFormatEditText etBalance;
    TextView tvErrorBalance;
    BankInfo currentBankConnect;
    Service service;
    ListBankConnectedAPI listBankConnectedAPI;
    RecycleViewListBankConnected.ListBankConnectViewHolder currentHolder;

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
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        userAmount = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
        int serviceType = intent.getIntExtra(Symbol.SERVICE_TYPE.GetValue(), 0);
        service = Service.Find(serviceType);
        getSupportActionBar().setTitle(service.GetMessage());
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

        intent.putExtra(Symbol.BANK_INFO.GetValue(), currentBankConnect.ExchangeToJsonData());

        startActivity(intent);
    }

    public void ClearAmounTransactionText(View view){
        etBalance.setText("");
    }

    public void BackToMain(View view){
        setResult(RESULT_CANCELED);
        finish();
    }

    void LoadListBankConnected(){
        progressBarManager.ShowProgressBar("Loading");
        listBankConnectedAPI.GetListBank();
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
        if(currentHolder == null || (currentHolder != null && !currentHolder.equals(holder))){
            currentHolder.SetBackgroundColor(defaultHolderColor);
        }

        this.currentHolder = holder;
        this.currentHolder.SetBackgroundColor(currentHolderChosenColor);
        this.currentBankConnect = info;
    }
}
