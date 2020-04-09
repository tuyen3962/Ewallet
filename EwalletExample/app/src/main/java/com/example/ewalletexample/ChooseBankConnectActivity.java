package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ewalletexample.Server.RequestServerAPI;
import com.example.ewalletexample.Server.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankSupport;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.ResponseModelByKey;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChooseBankConnectActivity extends AppCompatActivity implements ResponseModelByKey<UserModel> {

    FirebaseDatabaseHandler firebaseDatabaseHandler;
    FirebaseStorageHandler firebaseStorageHandler;
    GridView listBankSupportLayout;
    LinearLayout listBankLayout;
    CardView cardView;
    EditText etFullNameCard, etCardNo, etCMNDCard;
    private List<BankSupport> bankSupportList;
    String bankCode, userid;
    private UserModel model;
    long amount;

    boolean isShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bank_connect);

        Initialize();
        GetValueFromIntent();

        new GetSupportBank().execute(ServerAPI.GET_LIST_BANK_SUPPORT.GetUrl(), "{}");

        listBankSupportLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!isShowing){
                    isShowing = true;
                    ShowView(cardView);
                    HideGridView();
                    bankCode = bankSupportList.get(position).getBankCode();
                }
            }
        });
    }

    void Initialize(){
        firebaseDatabaseHandler = new FirebaseDatabaseHandler(FirebaseDatabase.getInstance().getReference());
        firebaseStorageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        cardView = findViewById(R.id.cardBankDetail);
        listBankSupportLayout = findViewById(R.id.listBankSupport);
        listBankLayout = findViewById(R.id.listBankLayout);
        etCardNo = findViewById(R.id.etCardNo);
        etCMNDCard = findViewById(R.id.etCMNDCard);
        etFullNameCard = findViewById(R.id.etFullNameBank);
        isShowing = false;
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        amount = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);

        firebaseDatabaseHandler.GetUserModelByKey(userid, UserModel.class, this);
    }

    private void HideGridView(){
        listBankLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.SoftWhite, null));
        listBankSupportLayout.setEnabled(false);
    }

    private void ShowGridView(){
        listBankSupportLayout.setEnabled(true);
        listBankLayout.setBackground(null);
        HideView(cardView);
        isShowing = false;
        bankCode = "";
    }

    private void AddIntoBankSupportGridView(){
        ListBankAdaper bankAdaper = new ListBankAdaper(this, bankSupportList);
        listBankSupportLayout.setAdapter(bankAdaper);
    }

    private void ShowView(final View view){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        //use this to make it longer:  animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });

        view.startAnimation(animation);
    }

    private void HideView(final View view){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_down);
        //use this to make it longer:  animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }
        });

        view.startAnimation(animation);
    }

    public void CreateLinkAccountWithBank(View view){
        String cardNo = etCardNo.getText().toString();
        String fullname = etFullNameCard.getText().toString();
        String cmnd = etCMNDCard.getText().toString();

        if(cardNo.isEmpty() || fullname.isEmpty() || cmnd.isEmpty()){
            return;
        }

        SendRequestForLinkingBank(cardNo, fullname, cmnd);
    }

    private void SendRequestForLinkingBank(String cardID, String fullNameCard, String cmndCard){
        String[] arr = new String[]{"userid:"+userid,"bankcode:"+bankCode,"cardno:"+cardID,
                "fullname:"+fullNameCard,"cmnd:"+cmndCard, "phone:"+model.getPhone()};

        try {
            String jsonData = HandlerJsonData.ExchangeToJsonString(arr);
            new LinkAccountWithBank().execute(ServerAPI.LINK_BANK_CARD_API.GetUrl(), jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void GetModel(UserModel data) {
        this.model = data;
    }

    private class ListBankAdaper extends BaseAdapter {

        Context mContext;
        List<BankSupport> mBankSupports;

        public ListBankAdaper(Context context, List<BankSupport> bankSupports){
            mContext = context;
            mBankSupports = bankSupports;
        }

        @Override
        public int getCount() {
            return mBankSupports.size();
        }

        @Override
        public Object getItem(int position) {
            return mBankSupports.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mBankSupports.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BankSupport bankSupport = mBankSupports.get(position);

            if(convertView == null){
                final LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.bank_support_item, null);
            }

            ImageView bankImg = convertView.findViewById(R.id.imgBank);
            firebaseStorageHandler.LoadAccountImageFromLink(bankSupport.getBankImage(), bankImg);
            TextView tvBankName = convertView.findViewById(R.id.tvBankName);

            tvBankName.setText(bankSupport.getBankName());

            return convertView;
        }
    }

    class GetSupportBank extends RequestServerAPI implements RequestServerFunction{
        public GetSupportBank(){
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
            bankSupportList = new ArrayList<>();

            JSONArray mBankSupportArray = jsonData.getJSONArray("banks");
            for(int i = 0; i < mBankSupportArray.length(); i++){
                JSONObject mBankSupport = mBankSupportArray.getJSONObject(i);
                String bankCode = mBankSupport.getString("bankcode");
                String bankName = mBankSupport.getString("bankname");
                String bankImage = mBankSupport.getString("bankimage");
                bankSupportList.add(new BankSupport(bankCode, bankName, bankImage));
            }

            AddIntoBankSupportGridView();
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
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
            Intent intent = getIntent();
            intent.putExtra(Symbol.USER_ID.GetValue(), userid);
            intent.putExtra(Symbol.AMOUNT.GetValue(), amount);
            startActivity(intent);
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }
}
