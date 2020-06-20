package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
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

import com.example.ewalletexample.Server.api.bank.link.LinkAccountWithBankAPI;
import com.example.ewalletexample.Server.api.bank.link.LinkBankRequest;
import com.example.ewalletexample.Server.api.bank.link.LinkBankResponse;
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
import com.example.ewalletexample.service.toolbar.CustomToolbarContext;
import com.example.ewalletexample.service.toolbar.ToolbarEvent;
import com.example.ewalletexample.service.websocket.WebsocketClient;
import com.example.ewalletexample.utilies.GsonUtils;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class ChooseBankConnectActivity extends AppCompatActivity implements ResponseModelByKey<UserModel>, UserSelectFunction<BankSupport>, LinkBankResponse, ToolbarEvent {
    ProgressBarManager progressBarManager;
    FirebaseDatabaseHandler firebaseDatabaseHandler;
    FirebaseStorageHandler firebaseStorageHandler;

    RecyclerView rvBankSupportLayout;
    ListBankSupportRecycleView listBankSupportRecycleView;

    LinearLayout listBankLayout, layoutBankAccountDetail;

    EditText etFullNameCard, etCardNo0, etCardNo1, etCardNo2, etCardNo3;
    CodeEditText codeEditText;

    AnimationManager animationManager;

    BankSupport[] bankSupportList;
    BankSupport chosenBankConnect;
    String userid, cmnd, secretKeyString1, secretKeyString2;
    UserModel model;
    long amount;
    boolean isShowing;
    CustomToolbarContext customToolbarContext;
    WebsocketClient websocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bank_connect);

        Initialize();
        GetValueFromIntent();
    }

    void Initialize(){
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

        etCardNo0 = findViewById(R.id.etCardNo0);
        etCardNo1 = findViewById(R.id.etCardNo1);
        etCardNo2 = findViewById(R.id.etCardNo2);
        etCardNo3 = findViewById(R.id.etCardNo3);
        customToolbarContext = new CustomToolbarContext(this, "Chọn ngân hàng liên kết", this::BackToPreviousActivity);

        codeEditText = new CodeEditText(4, etCardNo0, etCardNo1, etCardNo2, etCardNo3);
        isShowing = false;
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        amount = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
        cmnd = intent.getStringExtra(Symbol.CMND.GetValue());
        secretKeyString1 = intent.getStringExtra(Symbol.SECRET_KEY_01.GetValue());
        secretKeyString2 = intent.getStringExtra(Symbol.SECRET_KEY_02.GetValue());
        firebaseDatabaseHandler.GetModelByKey(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE, userid, UserModel.class, this);
        websocketClient = new WebsocketClient(this, userid);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void CreateLinkAccountWithBank(View view){
        String cardNo = codeEditText.GetCombineText();
        String fullname = etFullNameCard.getText().toString();

        if(cardNo.isEmpty() || fullname.isEmpty()){
            return;
        }
        SendRequestForLinkingBank(cardNo, fullname, cmnd);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SendRequestForLinkingBank(String cardID, String fullNameCard, String cmndCard){
        progressBarManager.ShowProgressBar("Loading");
        LinkBankRequest request = new LinkBankRequest();
        request.userid = userid;
        request.cardno = cardID;
        request.bankcode = chosenBankConnect.getBankCode();
        request.fullname = fullNameCard;
        request.cmnd = cmndCard;
        request.phone = model.getPhone();
        LinkAccountWithBankAPI linkAccountWithBankAPI = new LinkAccountWithBankAPI(request, this);
        linkAccountWithBankAPI.StartLinkCard(getString(R.string.public_key), secretKeyString1, secretKeyString2);
    }

    @Override
    public void GetModel(UserModel data) {
        this.model = data;
    }

    @Override
    public void GetBankInfo(BankInfo bankInfo) {
        progressBarManager.HideProgressBar();

        if (bankInfo != null){
            bankInfo.setCardName(chosenBankConnect.getBankName());
            Intent intent = new Intent();
            intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), websocketClient.IsUpdateBalance());
            intent.putExtra(Symbol.AMOUNT.GetValue(), websocketClient.getNewBalance());
            intent.putExtra(Symbol.BANK_INFO_CONNECTION.GetValue(), Symbol.CONNECT_BANK.GetValue());
            intent.putExtra(Symbol.BANK_INFO.GetValue(), GsonUtils.toJsonString(bankInfo));
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(ChooseBankConnectActivity.this, "Máy chủ không phản hồi", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void BackToPreviousActivity() {
        Intent intent = new Intent();
        intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), websocketClient.IsUpdateBalance());
        intent.putExtra(Symbol.AMOUNT.GetValue(), websocketClient.getNewBalance());
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
