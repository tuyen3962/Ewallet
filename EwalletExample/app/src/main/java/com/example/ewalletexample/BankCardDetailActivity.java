package com.example.ewalletexample;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
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
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

public class BankCardDetailActivity extends AppCompatActivity implements BankMappingCallback<Object> {

    FirebaseStorageHandler storageHandler;
    ImageButton ibtnBack;
    MaterialTextView tvToolbarTitle;
    String userid, secretKeyString1, secretKeyString2;
    BankInfo bankInfo;
    BankSupport bankSupport;
    TextView tvBankName, tvCardNumber, tvStatus;
    ImageView imgBank;
    Toolbar toolbar;
    UnlinkBankConnectedAPI unlinkBankAPI;
    WebsocketClient client;
    Gson gson;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_detail);

        LoadDataFromIntent();
        Initialize();
        SetTextBankCard();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void Initialize(){
        storageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        client = new WebsocketClient(this, userid);
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
        secretKeyString1 = intent.getStringExtra(Symbol.SECRET_KEY_01.GetValue());
        secretKeyString2 = intent.getStringExtra(Symbol.SECRET_KEY_02.GetValue());
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
        intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), client.IsUpdateBalance());
        intent.putExtra(Symbol.AMOUNT.GetValue(), client.getNewBalance());
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void UnlinkBank(View view){
        unlinkBankAPI.StartUnlink(getString(R.string.public_key), secretKeyString1, secretKeyString2);
    }

    @Override
    public void MappingResponse(boolean response, Object callback) {
        if(response){
            String bankInfoString = gson.toJson(bankInfo);
            Intent intent = new Intent();
            intent.putExtra(Symbol.BANK_INFO.GetValue(), bankInfoString);
            intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), client.IsUpdateBalance());
            intent.putExtra(Symbol.AMOUNT.GetValue(), client.getNewBalance());
            setResult(RESULT_OK, intent);
            finish();
        }
        else {
            Toast.makeText(this, "Huy lien ket that bai", Toast.LENGTH_SHORT).show();
        }
    }
}
