package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserBankCardActivity extends AppCompatActivity {

    ProgressBarManager progressBarManager;
    ListBankConnected banksConnected;
    ListView listBankConnected;
    TextView tvBalance, tvConnectBankAccount;
    Button btnConnectBankAccount;
    LinearLayout layoutConnectBank;
    List<BankInfo> bankInfoList;
    String userid;
    long amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bank_card);

        Initialize();
        LoadValueFromIntent();
        LoadBankConnected();
    }

    void Initialize(){
        tvBalance = findViewById(R.id.tvBalance);
        layoutConnectBank = findViewById(R.id.layoutConnectBank);
        tvConnectBankAccount = findViewById(R.id.tvConnectBankAccount);
        btnConnectBankAccount = findViewById(R.id.btnConnectBankAccount);
        listBankConnected = findViewById(R.id.listBankAccountConnect);
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), tvConnectBankAccount, btnConnectBankAccount);
    }

    void LoadValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        amount = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);

        SetBalance(amount);
    }

    private void LoadBankConnected(){
        try {
            progressBarManager.ShowProgressBar("Loading");
            String json = HandlerJsonData.ExchangeToJsonString(new String[]{"userid:"+ userid});
            new GetListBankConnected().execute(ServerAPI.GET_BANK_LINKING_API.GetUrl(), json);
        } catch (JSONException e) {
            e.printStackTrace();
            progressBarManager.HideProgressBar();
        }
    }

    void SetBalance(long amount){
        tvBalance.setText(amount+"");
    }

    public void AddNewBankCard(View view){
        Intent intent = new Intent(UserBankCardActivity.this, ChooseBankConnectActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.AMOUNT.GetValue(), amount);
        startActivity(intent);
    }

    public void BackToMain(View view){
        Intent intent = new Intent(UserBankCardActivity.this, MainActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        startActivity(intent);
    }

    class GetListBankConnected extends RequestServerAPI implements RequestServerFunction{
        public GetListBankConnected(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if(code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }
            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            bankInfoList = new ArrayList<>();
            JSONArray cardArray = jsonData.getJSONArray("cards");
            if(cardArray.length() == 0){
                layoutConnectBank.setVisibility(View.VISIBLE);
                progressBarManager.HideProgressBar();
                return;
            }

            layoutConnectBank.setVisibility(View.GONE);

            for(int i = 0; i < cardArray.length(); i++){
                JSONObject card = cardArray.getJSONObject(i);
                String cardName = card.getString("cardname");
                String bankCode = card.getString("bankcode");
                String f6CardNo = card.getString("f6cardno");
                String l4CardNo = card.getString("l4cardno");
                BankInfo bankInfo = new BankInfo(cardName, bankCode, f6CardNo, l4CardNo);
                bankInfoList.add(bankInfo);
            }

            banksConnected = new ListBankConnected(UserBankCardActivity.this, bankInfoList);
            listBankConnected.setAdapter(banksConnected);

            progressBarManager.HideProgressBar();
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }

    private class ListBankConnected extends BaseAdapter{
        private List<BankInfo> bankInfoList;
        private Context context;

        public ListBankConnected(Context context, List<BankInfo> bankInfos){
            this.context = context;
            this.bankInfoList = bankInfos;
        }

        @Override
        public int getCount() {
            return bankInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return bankInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return bankInfoList.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.activity_bank_card, null);
            }

            TextView tvCardName = convertView.findViewById(R.id.tvcardName);
            TextView tvF6Card = convertView.findViewById(R.id.tvF6CardNo);
            TextView tvL4Card = convertView.findViewById(R.id.tvL4CardNo);

            BankInfo bankInfo = bankInfoList.get(position);
            tvCardName.setText(bankInfo.getCardName());
            tvF6Card.setText(bankInfo.getF6CardNo());
            tvL4Card.setText(bankInfo.getL4CardNo());

            return convertView;
        }
    }
}
