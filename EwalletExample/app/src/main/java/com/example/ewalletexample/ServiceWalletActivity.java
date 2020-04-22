package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.UserSearchModel;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.service.TextBalanceFormat;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ServiceWalletActivity extends AppCompatActivity {

    FirebaseStorageHandler firebaseStorageHandler;
    ListBankConnected banksConnected;
    String userid;
    long userAmount;
    View userProfileLayout, sourceFundLayout;
    CircleImageView imgUserReceiver;
    List<BankInfo> bankInfoList;
    ProgressBarManager progressBarManager;
    ListView listBankConnected;
    EditText etBalance;
    TextView tvErrorBalance;
    BankInfo bankChoose;
    TextBalanceFormat textBalanceFormat;
    Service service;
    UserSearchModel searchModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_wallet);
        Initialize();
        GetValueFromIntent();
        LoadListBankConnected();
        listBankConnected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bankChoose = bankInfoList.get(position);
            }
        });
    }

    void Initialize(){
        listBankConnected = findViewById(R.id.listBankAccountConnect);
        etBalance = findViewById(R.id.etBalance);
        tvErrorBalance = findViewById(R.id.tvErrorBalance);
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar));
        userProfileLayout = findViewById(R.id.userProfileLayout);
        sourceFundLayout = findViewById(R.id.sourceFundLayout);
        imgUserReceiver = findViewById(R.id.imgUserReceiver);
        firebaseStorageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
//        textBalanceFormat = new TextBalanceFormat(false, etBalance);
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        userAmount = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
        int serviceType = intent.getIntExtra(Symbol.SERVICE_TYPE.GetValue(), 0);
        service = Service.Find(serviceType);
        getSupportActionBar().setTitle(service.GetMessage());
        if (service == Service.TOPUP_SERVICE_TYPE || service == Service.WITHDRAW_SERVICE_TYPE){
            ShowTopupAndWithdrawLayout();
        }
        else {
            searchModel = new UserSearchModel(intent.getStringExtra(Symbol.USER_SEARCH_MODEL.GetValue()));
            if(searchModel.getImgLink().equalsIgnoreCase("")){
                imgUserReceiver.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_account, null));
            } else {
                firebaseStorageHandler.LoadAccountImageFromLink(searchModel.getImgLink(), imgUserReceiver);
            }
            ShowExchangeMoneyLayout();
        }
    }

    void ShowExchangeMoneyLayout(){
        userProfileLayout.setVisibility(View.VISIBLE);
        sourceFundLayout.setVisibility(View.GONE);
    }

    void ShowTopupAndWithdrawLayout(){
        userProfileLayout.setVisibility(View.GONE);
        sourceFundLayout.setVisibility(View.VISIBLE);
    }

    public void StartTransaction(View view){
        HideErrorBalance();
        String amountTransaction = etBalance.getText().toString();
        if (amountTransaction.isEmpty()){
            ShowErrorBalance("Nhập số tiền bạn muốn thực hiện giao dịch");
            return;
        }

        if(bankChoose == null && service != Service.EXCHANGE_SERVICE_TYPE){
            ShowErrorBalance("Chọn ngân hàng để thực hiện giao dịch");
            return;
        }

        Intent intent = new Intent(ServiceWalletActivity.this, SubmitOrderActivity.class);
        intent.putExtra(Symbol.AMOUNT_TRANSACTION.GetValue(), amountTransaction);
        intent.putExtra(Symbol.FEE_TRANSACTION.GetValue(), 0);
        intent.putExtra(Symbol.SERVICE_TYPE.GetValue(), service.GetCode());

        if(service == Service.EXCHANGE_SERVICE_TYPE){
            intent.putExtra(Symbol.RECEIVER_PHONE.GetValue(), searchModel.getPhone());
            intent.putExtra(Symbol.RECEIVER_FULL_NAME.GetValue(), searchModel.getFullName());
            intent.putExtra(Symbol.SOURCE_OF_FUND.GetValue(), SourceFund.WALLET_SOURCE_FUND.GetCode());
        } else if(service == Service.TOPUP_SERVICE_TYPE){
            intent.putExtra(Symbol.SOURCE_OF_FUND.GetValue(), SourceFund.ATM_SOURCE_FUND.GetCode());
            intent.putExtra(Symbol.BANK_INFO.GetValue(), bankChoose.ExchangeToJsonData());
        } else if(service == Service.WITHDRAW_SERVICE_TYPE){
            intent.putExtra(Symbol.SOURCE_OF_FUND.GetValue(), SourceFund.WALLET_SOURCE_FUND.GetCode());
            intent.putExtra(Symbol.BANK_INFO.GetValue(), bankChoose.ExchangeToJsonData());
        }

        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        startActivity(intent);
    }

    void LoadListBankConnected(){
        progressBarManager.ShowProgressBar("Loading");
        String[] arr = new String[]{"userid:"+userid};

        try {
            String jsonData = HandlerJsonData.ExchangeToJsonString(arr);
            new GetListBankConnected().execute(ServerAPI.GET_BANK_LINKING_API.GetUrl(), jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void ShowErrorBalance(String mess){
        tvErrorBalance.setText(mess);
        tvErrorBalance.setVisibility(View.VISIBLE);
    }

    void HideErrorBalance(){
        tvErrorBalance.setVisibility(View.GONE);
    }

    class GetListBankConnected extends RequestServerAPI implements RequestServerFunction {
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
                progressBarManager.HideProgressBar();
                return;
            }

            for(int i = 0; i < cardArray.length(); i++){
                JSONObject card = cardArray.getJSONObject(i);
                String cardName = card.getString("cardname");
                String bankCode = card.getString("bankcode");
                String f6CardNo = card.getString("f6cardno");
                String l4CardNo = card.getString("l4cardno");
                BankInfo bankInfo = new BankInfo(cardName, bankCode, f6CardNo, l4CardNo);
                bankInfoList.add(bankInfo);
            }

            banksConnected = new ListBankConnected(ServiceWalletActivity.this, bankInfoList);
            listBankConnected.setAdapter(banksConnected);

            progressBarManager.HideProgressBar();
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }

    private class ListBankConnected extends BaseAdapter {
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
