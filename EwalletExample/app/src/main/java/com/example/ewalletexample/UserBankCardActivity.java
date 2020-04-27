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

import com.example.ewalletexample.Server.bank.BankMappingCallback;
import com.example.ewalletexample.Server.bank.list.ListBankConnectedAPI;
import com.example.ewalletexample.Symbol.BankSupport;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.websocket.WebsocketClient;
import com.example.ewalletexample.service.websocket.WebsocketResponse;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class UserBankCardActivity extends AppCompatActivity implements BankMappingCallback<List<BankInfo>>, WebsocketResponse {
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

    String userid;
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
        startActivityForResult(intent, RequestCode.CONNECT_BANK_CODE);
    }

    public void BackToMain(View view){
        Intent intent = new Intent(UserBankCardActivity.this, MainActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        startActivity(intent);
    }

    void SwitchToUnlinkBankAccount(BankInfo bankInfo){
        Intent intent = new Intent(UserBankCardActivity.this, BankCardDetailActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.BANK_INFO.GetValue(), bankInfo.ExchangeToJsonData());
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
            banksConnected = new ListBankConnectedRecycleView(this, bankInfoList);
            listBankConnected.setAdapter(banksConnected);
            listBankConnected.setVisibility(View.VISIBLE);
        } else {
            layoutConnectBank.setVisibility(View.VISIBLE);
            listBankConnected.setVisibility(View.GONE);
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

    private class ListBankConnectedRecycleView extends RecyclerView.Adapter<ListBankConnectedRecycleView.BankConnectedViewHolder>{

        private Context context;
        private LayoutInflater inflater;
        private List<BankInfo> bankInfoList;

        public ListBankConnectedRecycleView(Context context, List<BankInfo> bankInfos){
            this.context = context;
            inflater = LayoutInflater.from(context);
            bankInfoList = bankInfos;
        }

        class BankConnectedViewHolder extends RecyclerView.ViewHolder{
            TextView tvCardName;
            TextView tvF6Card;
            TextView tvL4Card;
            ImageView imgBank;
            GradientDrawable gradientDrawable;
            View layout;

            public BankConnectedViewHolder(View view){
                super(view);
                gradientDrawable = (GradientDrawable) view.getBackground();
                tvCardName = view.findViewById(R.id.tvcardName);
                tvF6Card = view.findViewById(R.id.tvF6CardNo);
                tvL4Card = view.findViewById(R.id.tvL4CardNo);
                imgBank = view.findViewById(R.id.imgBank);
                layout = view.findViewById(R.id.bankConnectLayout);
            }

            public void SetCardName(String name){
                tvCardName.setText(name);
            }

            public void SetF6CardNo(String f6Cardno){
                tvF6Card.setText(f6Cardno);
            }

            public void SetL4CardNo(String l4Cardno){
                tvL4Card.setText(l4Cardno);
            }

            public void LoadBankImage(FirebaseStorageHandler storageHandler, String imgBankLink){
                storageHandler.LoadAccountImageFromLink(imgBankLink, imgBank);
            }

            public void SetBackgroundColor(int colorId){
                gradientDrawable.setColor(colorId);
                gradientDrawable.setStroke(2, colorId);
            }

            public void SetClickEvent(final BankInfo info){
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SwitchToUnlinkBankAccount(info);
                    }
                });
            }
        }

        @NonNull
        @Override
        public ListBankConnectedRecycleView.BankConnectedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.activity_bank_card, parent, false);
            BankConnectedViewHolder holder = new BankConnectedViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ListBankConnectedRecycleView.BankConnectedViewHolder holder, int position) {
            BankInfo info = bankInfoList.get(position);
            BankSupport support = BankSupport.FindBankSupport(info.getBankCode());
            holder.SetCardName(info.getCardName());
            holder.SetF6CardNo(info.getF6CardNo());
            holder.SetL4CardNo(info.getL4CardNo());
//            holder.SetBackgroundColor(support.GetBackgroundColorCode());
            holder.LoadBankImage(storageHandler, support.getBankLinkImage());
            holder.SetClickEvent(info);
        }

        @Override
        public int getItemCount() {
            return bankInfoList.size();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.CONNECT_BANK_CODE){
            if (resultCode == RESULT_OK){
                String bankInfo = data.getStringExtra(Symbol.BANK_INFO.GetValue());
                try {
                    BankInfo bank = new BankInfo(bankInfo);
                    if(bank != null){
                        bankInfoList.add(bank);
                        banksConnected = new ListBankConnectedRecycleView(UserBankCardActivity.this, bankInfoList);
                        listBankConnected.setAdapter(banksConnected);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if(requestCode == RequestCode.UNLINK_BANK_CODE){
            boolean changeBalance = data.getBooleanExtra(Symbol.CHANGE_BALANCE.GetValue(), false);
            if (changeBalance){
                this.balance = data.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
            }

            if(resultCode == RESULT_OK){
                String bankInfoString = data.getStringExtra(Symbol.BANK_INFO.GetValue());
                try {
                    BankInfo bankInfo = new BankInfo(bankInfoString);
                    int index = -1;
                    for (int i = 0; i < bankInfoList.size(); i++){
                        BankInfo info = bankInfoList.get(i);
                        if(bankInfo.equal(info)){
                            index = i;
                            break;
                        }
                    }
                    if(index != -1){
                        bankInfoList.remove(index);
                        SetLayoutBankConnect();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}