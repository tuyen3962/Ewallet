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
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.AnimationManager;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.service.code.CodeEditText;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.ResponseModelByKey;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChooseBankConnectActivity extends AppCompatActivity implements ResponseModelByKey<UserModel> {
    ProgressBarManager progressBarManager;
    FirebaseDatabaseHandler firebaseDatabaseHandler;
    FirebaseStorageHandler firebaseStorageHandler;

    RecyclerView rvBankSupportLayout;
    ListBankSupportRecycleView listBankSupportRecycleView;

    LinearLayout listBankLayout, layoutBankAccountDetail;

    EditText etFullNameCard, etCMNDCard, etCardNo0, etCardNo1, etCardNo2, etCardNo3;
    CodeEditText codeEditText;
    TextView tvBack;

    AnimationManager animationManager;

    BankSupport[] bankSupportList;
    BankSupport chosenBankConnect;
    String userid;
    UserModel model;
    long amount;

    boolean isShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bank_connect);

        Initialize();
        GetValueFromIntent();

//        new GetSupportBank().execute(ServerAPI.GET_LIST_BANK_SUPPORT.GetUrl(), "{}");

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReturnToUserBankActivity();
            }
        });
    }

    void Initialize(){
        firebaseDatabaseHandler = new FirebaseDatabaseHandler(FirebaseDatabase.getInstance().getReference());
        firebaseStorageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        animationManager = new AnimationManager(this);

        layoutBankAccountDetail = findViewById(R.id.layoutBankAccountDetail);
        listBankLayout = findViewById(R.id.listBankLayout);

        rvBankSupportLayout = findViewById(R.id.rvListBankSupportLayout);
        bankSupportList = BankSupport.values();
        listBankSupportRecycleView = new ListBankSupportRecycleView(this, bankSupportList);
        rvBankSupportLayout.setHasFixedSize(true);
        rvBankSupportLayout.setLayoutManager(new GridLayoutManager(this, 3));
        rvBankSupportLayout.setAdapter(listBankSupportRecycleView);

        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), findViewById(R.id.btnLinkAccount));

        etCMNDCard = findViewById(R.id.etCMNDCard);
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

        firebaseDatabaseHandler.GetUserModelByKey(userid, UserModel.class, this);
    }

    private void HideGridView(){
        rvBankSupportLayout.setEnabled(false);
    }

    public void ShowGridView(View view){
        rvBankSupportLayout.setEnabled(true);
        listBankLayout.setBackground(null);
        animationManager.HideAnimationView(layoutBankAccountDetail);
        isShowing = false;
        chosenBankConnect = null;
    }

    public void CreateLinkAccountWithBank(View view){
        String cardNo = codeEditText.GetCombineText();
        String fullname = etFullNameCard.getText().toString();
        String cmnd = etCMNDCard.getText().toString();

        if(cardNo.isEmpty() || fullname.isEmpty() || cmnd.isEmpty()){
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

    private class ListBankSupportRecycleView extends RecyclerView.Adapter<ListBankSupportRecycleView.BankSupportViewHolder>{

        private Context context;
        private LayoutInflater inflater;
        private BankSupport[] bankSupportList;

        public ListBankSupportRecycleView(Context context, BankSupport[] supportList){
            this.context = context;
            inflater = LayoutInflater.from(context);
            bankSupportList = supportList;
        }

        class BankSupportViewHolder extends RecyclerView.ViewHolder{
            View view;
            ImageView bankImg;
            TextView tvBankName;
            GradientDrawable gradientDrawable;

            public BankSupportViewHolder(View view){
                super(view);
                this.view = view.findViewById(R.id.bankSupportLayout);
                gradientDrawable = (GradientDrawable) view.getBackground();
                bankImg = view.findViewById(R.id.imgBank);
                tvBankName = view.findViewById(R.id.tvBankName);

                gradientDrawable.setShape(GradientDrawable.OVAL);
                gradientDrawable.setStroke(2, Color.GRAY);
            }

            public void SetBankText(String text){
                tvBankName.setText(text);
            }

            public void LoadImageFromFirebase(FirebaseStorageHandler firebaseStorageHandler, String imageLink){
                firebaseStorageHandler.LoadAccountImageFromLink(imageLink, bankImg);
            }

            public void SetClickEvent(final BankSupport bankSupport){
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isShowing){
                            isShowing = true;
                            animationManager.ShowAnimationView(layoutBankAccountDetail);
                            HideGridView();
                            chosenBankConnect = bankSupport;
                        }
                    }
                });
            }
        }

        @NonNull
        @Override
        public BankSupportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.bank_support_item, parent, false);
            BankSupportViewHolder holder = new BankSupportViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull BankSupportViewHolder holder, int position) {
            BankSupport support = bankSupportList[position];
            holder.SetBankText(support.getBankName());
            holder.LoadImageFromFirebase(firebaseStorageHandler, support.getBankLinkImage());
            holder.SetClickEvent(support);
        }

        @Override
        public int getItemCount() {
            return bankSupportList.length;
        }
    }

//    class GetSupportBank extends RequestServerAPI implements RequestServerFunction{
//        public GetSupportBank(){
//            SetRequestServerFunction(this);
//        }
//
//        @Override
//        public boolean CheckReturnCode(int code) {
//            if(code == ErrorCode.SUCCESS.GetValue()){
//                return true;
//            }
//
//            return false;
//        }
//
//        @Override
//        public void DataHandle(JSONObject jsonData) throws JSONException {
//            bankSupportList = new ArrayList<>();
//
//            JSONArray mBankSupportArray = jsonData.getJSONArray("banks");
//            for(int i = 0; i < mBankSupportArray.length(); i++){
//                JSONObject mBankSupport = mBankSupportArray.getJSONObject(i);
//                String bankCode = mBankSupport.getString("bankcode");
//                String bankName = mBankSupport.getString("bankname");
//                String bankImage = mBankSupport.getString("bankimage");
//                bankSupportList.add(new BankSupport(bankCode, bankName, bankImage));
//            }
//
//            AddIntoBankSupportGridView();
//        }
//
//        @Override
//        public void ShowError(int errorCode, String message) {
//
//        }
//    }

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
            JSONObject bankInfoObject = jsonData.getJSONObject("bankInfo");

            Intent intent = getIntent();
            intent.putExtra(Symbol.BANK_INFO.GetValue(), bankInfoObject.toString());
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        public void ShowError(int errorCode, String message) {
            progressBarManager.HideProgressBar();
            Toast.makeText(ChooseBankConnectActivity.this, "Khong the lien ket voi tai khoan ngan hang", Toast.LENGTH_SHORT).show();
        }
    }
}
