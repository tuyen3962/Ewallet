package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ewalletexample.Server.api.bank.BankMappingCallback;
import com.example.ewalletexample.Server.api.bank.list.ListBankConnectedAPI;
import com.example.ewalletexample.Symbol.BankSupport;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.service.UserSelectFunction;
import com.example.ewalletexample.service.recycleview.bank.ListBankConnectedRecycleView;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.websocket.WebsocketClient;
import com.example.ewalletexample.service.websocket.WebsocketResponse;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class UserBankCardActivity extends AppCompatActivity implements BankMappingCallback<List<BankInfo>>, WebsocketResponse, UserSelectFunction<BankInfo> {
    FirebaseStorageHandler storageHandler;

    ListBankConnectedAPI listBankAPI;

    ProgressBarManager progressBarManager;
    ListBankConnectedRecycleView banksConnected;
    TextView tvBalance, tvConnectBankAccount;
    Button btnConnectBankAccount;
    LinearLayout layoutConnectBank;

    RecyclerView listBankConnected;
    List<BankInfo> bankInfoList;

    WebsocketClient client;
    Gson gson;
    String userid, cmnd;
    long balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bank_card);

        LoadValueFromIntent();
        Initialize();
        LoadBankConnected();
    }

    void Initialize(){
        bankInfoList = new ArrayList<>();
        gson = new Gson();
        client = new WebsocketClient(this);
        storageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);

        tvBalance = findViewById(R.id.tvBalance);
        layoutConnectBank = findViewById(R.id.layoutConnectBank);
        tvConnectBankAccount = findViewById(R.id.tvConnectBankAccount);
        btnConnectBankAccount = findViewById(R.id.btnConnectBankAccount);

        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), tvConnectBankAccount, btnConnectBankAccount);

        listBankConnected = findViewById(R.id.listBankAccountConnect);
        listBankConnected.setHasFixedSize(true);
        listBankConnected.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        listBankAPI = new ListBankConnectedAPI(this, userid);
        SetBalance(balance);
    }

    void LoadValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        balance = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
        cmnd = intent.getStringExtra(Symbol.CMND.GetValue());
    }

    private void LoadBankConnected(){
        progressBarManager.ShowProgressBar("Loading");
        listBankAPI.GetListBank();
    }

    void SetBalance(long amount){
        tvBalance.setText(amount+"");
    }

    public void AddNewBankCard(View view){
        Intent intent = new Intent(UserBankCardActivity.this, ChooseBankConnectActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        intent.putExtra(Symbol.CMND.GetValue(), cmnd);
        startActivityForResult(intent, RequestCode.CONNECT_BANK_CODE);
    }

    public void BackToMain(View view){
        Intent intent = new Intent(UserBankCardActivity.this, MainLayoutActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        startActivity(intent);
    }

    @Override
    public void SelectModel(BankInfo model) {
        Intent intent = new Intent(UserBankCardActivity.this, BankCardDetailActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.BANK_INFO.GetValue(), gson.toJson(model));
        startActivityForResult(intent, RequestCode.UNLINK_BANK_CODE);
    }

    @Override
    public void MappingResponse(boolean response, List<BankInfo> callback) {
        if(!response){
            layoutConnectBank.setVisibility(View.VISIBLE);
        }
        else {
            if (callback != null){
                bankInfoList.clear();
                bankInfoList.addAll(callback);
                SetLayoutBankConnect();
            }
        }

        progressBarManager.HideProgressBar();
    }

    public void SetLayoutBankConnect(){
        if(bankInfoList.size() > 0){
            layoutConnectBank.setVisibility(View.GONE);
            banksConnected = new ListBankConnectedRecycleView(this, bankInfoList, storageHandler, this);
            listBankConnected.setAdapter(banksConnected);
            listBankConnected.setVisibility(View.VISIBLE);
        } else {
            layoutConnectBank.setVisibility(View.VISIBLE);
            listBankConnected.setVisibility(View.GONE);
        }
    }

    public void AddNewBankInfo(BankInfo bankInfo){
        bankInfoList.add(bankInfo);
        banksConnected = new ListBankConnectedRecycleView(this, bankInfoList, storageHandler, this);
        listBankConnected.setAdapter(banksConnected);
        if (layoutConnectBank.getVisibility() == View.VISIBLE){
            layoutConnectBank.setVisibility(View.GONE);
            listBankConnected.setVisibility(View.VISIBLE);
        }
    }

    public void RemoveBankInfo(BankInfo bankInfo){
        if (bankInfo == null)
             return;
        int index = -1;
        for (int i = 0; i < bankInfoList.size(); i++){
            if (bankInfoList.get(i).equal(bankInfo)){
                index = i;
                break;
            }
        }
        if (index >= 0){
            bankInfoList.remove(index);
            SetLayoutBankConnect();
        }
    }

    @Override
    public void UpdateWallet(String userid, long balance) {
        runOnUiThread(()->{
            if(userid.equalsIgnoreCase(this.userid)){
                this.balance = balance;
                SetBalance(balance);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.CONNECT_BANK_CODE){
            if (resultCode == RESULT_OK){
                BankInfo bank = gson.fromJson(data.getStringExtra(Symbol.BANK_INFO.GetValue()), BankInfo.class);
                if(bank != null){
                    AddNewBankInfo(bank);
                }
            }
        } else if(requestCode == RequestCode.UNLINK_BANK_CODE){
            boolean changeBalance = data.getBooleanExtra(Symbol.CHANGE_BALANCE.GetValue(), false);
            if (changeBalance){
                this.balance = data.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
            }

            if(resultCode == RESULT_OK){
                BankInfo bankInfo = gson.fromJson(data.getStringExtra(Symbol.BANK_INFO.GetValue()), BankInfo.class);
                RemoveBankInfo(bankInfo);
            }
        }
    }
}