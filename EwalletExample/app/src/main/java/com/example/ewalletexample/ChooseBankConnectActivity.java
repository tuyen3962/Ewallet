package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.BankSupport;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.AnimationManager;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.service.UserSelectFunction;
import com.example.ewalletexample.service.code.CodeEditText;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.ResponseModelByKey;
import com.example.ewalletexample.service.recycleview.bank.ListBankSupportRecycleView;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class ChooseBankConnectActivity extends AppCompatActivity implements ResponseModelByKey<UserModel>, UserSelectFunction<BankSupport> {
    ProgressBarManager progressBarManager;
    FirebaseDatabaseHandler firebaseDatabaseHandler;
    FirebaseStorageHandler firebaseStorageHandler;

    RecyclerView rvBankSupportLayout;
    ListBankSupportRecycleView listBankSupportRecycleView;

    LinearLayout listBankLayout, layoutBankAccountDetail;

    EditText etFullNameCard, etCardNo0, etCardNo1, etCardNo2, etCardNo3;
    CodeEditText codeEditText;
    TextView tvBack;

    AnimationManager animationManager;

    BankSupport[] bankSupportList;
    BankSupport chosenBankConnect;
    String userid, cmnd;
    UserModel model;
    long amount;
    Gson gson;
    boolean isShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bank_connect);

        Initialize();
        GetValueFromIntent();

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReturnToUserBankActivity();
            }
        });
    }

    void Initialize(){
        gson = new Gson();
        firebaseDatabaseHandler = new FirebaseDatabaseHandler(FirebaseDatabase.getInstance().getReference());
        firebaseStorageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        animationManager = new AnimationManager(this);

        layoutBankAccountDetail = findViewById(R.id.layoutBankAccountDetail);
        layoutBankAccountDetail.setVisibility(View.GONE);
        listBankLayout = findViewById(R.id.listBankLayout);

        rvBankSupportLayout = findViewById(R.id.rvListBankSupportLayout);
        bankSupportList = BankSupport.values();
        listBankSupportRecycleView = new ListBankSupportRecycleView(this, firebaseStorageHandler, this, bankSupportList);
        rvBankSupportLayout.setHasFixedSize(true);
        rvBankSupportLayout.setLayoutManager(new GridLayoutManager(this, 3));
        rvBankSupportLayout.setAdapter(listBankSupportRecycleView);

        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), findViewById(R.id.btnLinkAccount));

        etFullNameCard = findViewById(R.id.etFullNameBank);
        tvBack = findViewById(R.id.tvBack);

        etCardNo0 = findViewById(R.id.etCardNo0);
        etCardNo1 = findViewById(R.id.etCardNo1);
        etCardNo2 = findViewById(R.id.etCardNo2);
        etCardNo3 = findViewById(R.id.etCardNo3);

        codeEditText = new CodeEditText(4, etCardNo0, etCardNo1, etCardNo2, etCardNo3);
        isShowing = false;
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        amount = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
        cmnd = intent.getStringExtra(Symbol.CMND.GetValue());
        firebaseDatabaseHandler.GetModelByKey(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE, userid, UserModel.class, this);
    }

    @Override
    public void SelectModel(BankSupport model) {
        if(!isShowing){
            isShowing = true;
            animationManager.ShowAnimationView(layoutBankAccountDetail);
            HideGridView();
            chosenBankConnect = model;
        }
    }

    private void HideGridView(){
        rvBankSupportLayout.setEnabled(false);
    }

    public void ShowGridView(View view){
        rvBankSupportLayout.setEnabled(true);
        animationManager.HideAnimationView(layoutBankAccountDetail);
        isShowing = false;
        chosenBankConnect = null;
    }

    public void CreateLinkAccountWithBank(View view){
        String cardNo = codeEditText.GetCombineText();
        String fullname = etFullNameCard.getText().toString();

        if(cardNo.isEmpty() || fullname.isEmpty()){
            return;
        }
        SendRequestForLinkingBank(cardNo, fullname, cmnd);
    }

    private void SendRequestForLinkingBank(String cardID, String fullNameCard, String cmndCard){
        progressBarManager.ShowProgressBar("Loading");
        String[] arr = new String[]{"userid:"+userid,"bankcode:"+chosenBankConnect.getBankCode(),
                "cardno:"+cardID, "fullname:"+fullNameCard,"cmnd:"+cmndCard, "phone:"+model.getPhone()};

        try {
            String jsonData = HandlerJsonData.ExchangeToJsonString(arr);
            new LinkAccountWithBank().execute(ServerAPI.LINK_BANK_CARD_API.GetUrl(), jsonData);
        } catch (JSONException e) {
            progressBarManager.HideProgressBar();
            e.printStackTrace();
        }
    }

    public void ReturnToUserBankActivity(){
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void GetModel(UserModel data) {
        this.model = data;
    }

    class LinkAccountWithBank extends RequestServerAPI implements RequestServerFunction{
        public LinkAccountWithBank(){
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
            int bankReturnCode = jsonData.getInt("bankreturncode");

            if (bankReturnCode == 1){
                BankInfo bankInfo = new BankInfo();
                bankInfo.setBankCode(chosenBankConnect.getBankCode());
                bankInfo.setCardName(chosenBankConnect.getBankName());
                String cardNo = codeEditText.GetCombineText();
                bankInfo.setF6CardNo(cardNo.substring(0, 6));
                bankInfo.setL4CardNo(cardNo.substring(12));
                Intent intent = new Intent();
                intent.putExtra(Symbol.BANK_INFO_CONNECTION.GetValue(), Symbol.CONNECT_BANK.GetValue());
                intent.putExtra(Symbol.BANK_INFO.GetValue(), gson.toJson(bankInfo));
                setResult(RESULT_OK, intent);
                finish();
            } else {

            }
        }

        @Override
        public void ShowError(int errorCode, String message) {
            progressBarManager.HideProgressBar();
            Toast.makeText(ChooseBankConnectActivity.this, "Khong the lien ket voi tai khoan ngan hang", Toast.LENGTH_SHORT).show();
        }
    }
}
