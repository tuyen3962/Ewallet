package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ewalletexample.Server.api.bank.BankMappingCallback;
import com.example.ewalletexample.Server.api.bank.unlink.UnlinkBankConnectedAPI;
import com.example.ewalletexample.Symbol.BankSupport;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.websocket.WebsocketClient;
import com.example.ewalletexample.service.websocket.WebsocketResponse;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

public class BankCardDetailActivity extends AppCompatActivity implements BankMappingCallback<Object>, WebsocketResponse {

    FirebaseStorageHandler storageHandler;
    ImageButton ibtnBack;
    MaterialTextView tvToolbarTitle;
    String userid;
    BankInfo bankInfo;
    BankSupport bankSupport;
    TextView tvBankName, tvCardNumber, tvStatus;
    ImageView imgBank;
    Toolbar toolbar;
    UnlinkBankConnectedAPI unlinkBankAPI;
    WebsocketClient client;
    boolean changeBalance;
    long balance;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_detail);

        LoadDataFromIntent();
        Initialize();
        SetTextBankCard();
    }

    void Initialize(){
        storageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        changeBalance = false;
        balance = 0;
        client = new WebsocketClient(this);
        tvBankName = findViewById(R.id.tvBankName);
        tvCardNumber = findViewById(R.id.tvCardNumber);
        tvStatus = findViewById(R.id.tvStatus);
        imgBank = findViewById(R.id.imgBank);
        toolbar = findViewById(R.id.toolbarLayout);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        ibtnBack = findViewById(R.id.btnBackToPreviousActivity);
        setSupportActionBar(toolbar);
        unlinkBankAPI = new UnlinkBankConnectedAPI(this, userid, bankInfo);

        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReturnPreviousActivity();
            }
        });
    }

    void LoadDataFromIntent(){
        gson = new Gson();
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        bankInfo = gson.fromJson(intent.getStringExtra(Symbol.BANK_INFO.GetValue()), BankInfo.class);
        bankSupport = BankSupport.FindBankSupport(bankInfo.getBankCode());
    }

    void SetTextBankCard(){
        tvBankName.setText(bankSupport.getBankName());
        tvCardNumber.setText(bankInfo.getCardNumber());
        tvStatus.setText("Đang liên kết");
        storageHandler.LoadAccountImageFromLink(bankSupport.getBankLinkImage(), imgBank);
    }

    void ReturnPreviousActivity(){
        Intent intent = new Intent();
        intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), changeBalance);
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void UnlinkBank(View view){
        unlinkBankAPI.StartUnlink();
    }

    @Override
    public void MappingResponse(boolean response, Object callback) {
        if(response){
            String bankInfoString = gson.toJson(bankInfo);
            Intent intent = new Intent();
            intent.putExtra(Symbol.BANK_INFO.GetValue(), bankInfoString);
            intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), changeBalance);
            intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
            setResult(RESULT_OK, intent);
            finish();
        }
        else {
            Toast.makeText(this, "Huy lien ket that bai", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void UpdateWallet(String userid, long balance) {
        if(userid.equalsIgnoreCase(this.userid)){
            this.balance = balance;
            changeBalance = true;
        }
    }
}
