package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.ewalletexample.Server.api.bank.BankMappingCallback;
import com.example.ewalletexample.Server.api.bank.list.ListBankConnectedAPI;
import com.example.ewalletexample.Symbol.BankSupport;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.service.UserSelectFunction;
import com.example.ewalletexample.service.recycleview.bank.ListBankConnectedRecycleView;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.toolbar.CustomToolbarContext;
import com.example.ewalletexample.service.toolbar.ToolbarEvent;
import com.example.ewalletexample.service.websocket.WebsocketClient;
import com.example.ewalletexample.service.websocket.WebsocketResponse;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

public class UserBankCardActivity extends AppCompatActivity implements BankMappingCallback<List<BankInfo>>,
        UserSelectFunction<BankInfo>, ToolbarEvent {
    FirebaseStorageHandler storageHandler;

    ListBankConnectedAPI listBankAPI;

    ProgressBarManager progressBarManager;
    ListBankConnectedRecycleView banksConnected;
    TextView tvBalance, tvConnectBankAccount;
    Button btnConnectBankAccount;
    LinearLayout layoutConnectBank;
    CustomToolbarContext customToolbarContext;
    RecyclerView listBankConnected;
    List<BankInfo> bankInfoList;
    User user;
    WebsocketClient client;
    String secretKeyString1, secretKeyString2;
    Gson gson;
    long balance;

    @RequiresApi(api = Build.VERSION_CODES.M | Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bank_card);

        LoadValueFromIntent();
        Initialize();
        LoadBankConnected();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void Initialize(){
        bankInfoList = new ArrayList<>();
        gson = new Gson();
        client = new WebsocketClient(this, user.getUserId());
        storageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);

        tvBalance = findViewById(R.id.tvBalance);
        layoutConnectBank = findViewById(R.id.layoutConnectBank);
        tvConnectBankAccount = findViewById(R.id.tvConnectBankAccount);
        btnConnectBankAccount = findViewById(R.id.btnConnectBankAccount);

        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), tvConnectBankAccount, btnConnectBankAccount);
        customToolbarContext = new CustomToolbarContext(this, this::BackToPreviousActivity);
        customToolbarContext.SetTitle("Danh sách thẻ liên kết");

        listBankConnected = findViewById(R.id.listBankAccountConnect);
        listBankConnected.setHasFixedSize(true);
        listBankConnected.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        listBankAPI = new ListBankConnectedAPI(this, user.getUserId());
        SetBalance(balance);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void LoadValueFromIntent(){
        user = new User();
        Intent intent = getIntent();
        user.setUserId(intent.getStringExtra(Symbol.USER_ID.GetValue()));
        balance = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
        user.setCmnd(intent.getStringExtra(Symbol.CMND.GetValue()));
        user.setPhoneNumber(intent.getStringExtra(Symbol.PHONE.GetValue()));
        secretKeyString1 = intent.getStringExtra(Symbol.SECRET_KEY_01.GetValue());
        secretKeyString2 = intent.getStringExtra(Symbol.SECRET_KEY_02.GetValue());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void LoadBankConnected(){
        progressBarManager.ShowProgressBar("Loading");
        listBankAPI.GetListBank(getString(R.string.public_key), secretKeyString1, secretKeyString2);
    }

    void SetBalance(long amount){
        tvBalance.setText(amount+"");
    }

    public void AddNewBankCard(View view){
        Intent intent = new Intent(UserBankCardActivity.this, ChooseBankConnectActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), user.getUserId());
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        intent.putExtra(Symbol.CMND.GetValue(), user.getCmnd());
        intent.putExtra(Symbol.SECRET_KEY_01.GetValue(), secretKeyString1);
        intent.putExtra(Symbol.SECRET_KEY_02.GetValue(), secretKeyString2);
        startActivityForResult(intent, RequestCode.CONNECT_BANK_CODE);
    }

    @Override
    public void SelectModel(BankInfo model) {
        Intent intent = new Intent(UserBankCardActivity.this, BankCardDetailActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), user.getUserId());
        intent.putExtra(Symbol.BANK_INFO.GetValue(), gson.toJson(model));
        intent.putExtra(Symbol.SECRET_KEY_01.GetValue(), secretKeyString1);
        intent.putExtra(Symbol.SECRET_KEY_02.GetValue(), secretKeyString2);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.CONNECT_BANK_CODE){
            boolean changeBalance = data.getBooleanExtra(Symbol.CHANGE_BALANCE.GetValue(), false);
            if (changeBalance){
                this.balance = data.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
            }

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

    @Override
    public void BackToPreviousActivity() {
        Intent intent = new Intent();
        intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), client.getNewBalance());
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        setResult(RESULT_OK, intent);
        finish();
    }
}