package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ewalletexample.Server.bank.BankMappingCallback;
import com.example.ewalletexample.Server.bank.unlink.UnlinkBankConnectedAPI;
import com.example.ewalletexample.Server.bank.unlink.UnlinkBankResponse;
import com.example.ewalletexample.Symbol.BankSupport;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.service.websocket.WebsocketClient;
import com.example.ewalletexample.service.websocket.WebsocketResponse;

import org.json.JSONException;

public class BankCardDetailActivity extends AppCompatActivity implements BankMappingCallback<Object>, WebsocketResponse {

    String userid;
    BankInfo bankInfo;
    BankSupport bankSupport;
    TextView tvBankName, tvCardNumber, tvStatus;
    ImageView imgBank;
    UnlinkBankConnectedAPI unlinkBankAPI;
    WebsocketClient client;
    boolean changeBalance;
    long balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_detail);

        LoadDataFromIntent();
        Initialize();
        SetTextBankCard();
    }

    void Initialize(){
        changeBalance = false;
        balance = 0;
        client = new WebsocketClient(this);
        tvBankName = findViewById(R.id.tvBankName);
        tvCardNumber = findViewById(R.id.tvCardNumber);
        tvStatus = findViewById(R.id.tvStatus);
        imgBank = findViewById(R.id.imgBank);

        unlinkBankAPI = new UnlinkBankConnectedAPI(this, userid, bankInfo);
    }

    void LoadDataFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        String bankInfoString = intent.getStringExtra(Symbol.BANK_INFO.GetValue());
        try {
            bankInfo = new BankInfo(bankInfoString);
            bankSupport = BankSupport.FindBankSupport(bankInfo.getBankCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void SetTextBankCard(){
        tvBankName.setText(bankSupport.getBankName());
        tvCardNumber.setText(bankInfo.getCardNumber());
        tvStatus.setText("Đang liên kết");
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
            String bankInfoString = bankInfo.ExchangeToJsonData();
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
