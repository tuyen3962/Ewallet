package com.example.ewalletexample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.websocket.WebsocketClient;
import com.example.ewalletexample.service.websocket.WebsocketResponse;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;
import org.json.JSONObject;

public class PersonalDetailActivity extends AppCompatActivity implements WebsocketResponse {
    FirebaseStorageHandler firebaseStorageHandler;
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    View layoutAddress, layoutDob, layoutImageAccount, layoutVerifyAccount, layoutEmail;
    TextView tvDateOfBirth, tvAddress, tvFullname, tvPhone, tvVerifyText, tvEmail;
    CircleImageView imgAccount;
    ProgressBarManager progressBarManager;

    private User user;

    WebsocketClient client;
    long balance;
    boolean changeBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_detail);
        Initialize();
        LoadDataFromIntent();
    }

    void Initialize(){
        changeBalance = false;
        balance = 0;
        client = new WebsocketClient(this);

        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        tvAddress = findViewById(R.id.tvAddress);
        tvFullname = findViewById(R.id.tvFullName);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvVerifyText = findViewById(R.id.tvVerifyText);

        imgAccount = findViewById(R.id.imgAccount);

        layoutAddress = findViewById(R.id.layoutAddress);
        layoutDob = findViewById(R.id.layoutDateOfBirth);
        layoutImageAccount = findViewById(R.id.layoutImageAccount);
        layoutVerifyAccount = findViewById(R.id.layoutVerifyAccount);
        layoutEmail = findViewById(R.id.layoutEmail);

        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar),
                layoutAddress, layoutDob, layoutImageAccount, layoutVerifyAccount, layoutEmail);

        firebaseStorageHandler = new FirebaseStorageHandler(imgAccount, FirebaseStorage.getInstance(), this);
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference());
    }

    void LoadDataFromIntent(){
        user = new User();
        Intent intent = getIntent();
        String json = intent.getStringExtra(Symbol.USER.GetValue());
        if(!json.isEmpty()){
            user.ReadJson(json);
        }
        FillUserProfile();
        LoadImageAccount();
    }

    private void FillUserProfile(){
        tvFullname.setText(user.getFullName());
        tvDateOfBirth.setText(user.getDateOfbirth());
        tvAddress.setText(user.getAddress());
        tvPhone.setText(user.getPhoneNumber());
        tvEmail.setText(user.getEmail());
        if(user.getStatus() == -1){
            tvVerifyText.setText("Chưa xác thực");
        } else if(user.getStatus() == 0){
            tvVerifyText.setText("Đang xác thực");
        } else{
            tvVerifyText.setText("Đã xác thực");
        }
    }

    void LoadImageAccount(){
        if(user.getImgAccountLink().isEmpty()){
            imgAccount.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_person, null));
            return;
        }

        firebaseStorageHandler.LoadAccountImageFromLink(user.getImgAccountLink(), imgAccount);
    }

    public void UploadUserInfo(View view){
        Intent intent = new Intent(PersonalDetailActivity.this, UpdateUserInformationActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), user.getUserId());
        intent.putExtra(Symbol.ADDRESS.GetValue(), user.getAddress());
        intent.putExtra(Symbol.FULLNAME.GetValue(), user.getFullName());
        intent.putExtra(Symbol.DOB.GetValue(), user.getDateOfbirth());
        intent.putExtra(Symbol.IMAGE_ACCOUNT_LINK.GetValue(), user.getImgAccountLink());
        startActivityForResult(intent, RequestCode.UPDATE_USER);
    }

    public void VerifyAccount(View view){
        Intent intent = new Intent(PersonalDetailActivity.this, VerifyAccountActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), user.getUserId());
        intent.putExtra(Symbol.FULLNAME.GetValue(), user.getFullName());
        intent.putExtra(Symbol.CMND.GetValue(), user.getCmnd());
        intent.putExtra(Symbol.IMAGE_ACCOUNT_LINK.GetValue(), user.getImgID());
        intent.putExtra(Symbol.STATUS.GetValue(), user.getStatus());
        startActivityForResult(intent, RequestCode.VERIFY_ACCOUNT_CODE);
    }

    public void UpdateUserEmail(View view){
        Intent intent = new Intent(PersonalDetailActivity.this, UpdateEmailActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), user.getUserId());
        intent.putExtra(Symbol.EMAIL.GetValue(), user.getEmail());
        startActivityForResult(intent, RequestCode.UPDATE_EMAIL);
    }

    public void BackToMainEvent(View view){
        Intent intent = new Intent();
        intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), changeBalance);
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        intent.putExtra(Symbol.USER.GetValue(), user.ExchangeToJson());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void UpdateWallet(String userid, long balance) {
        if(userid.equalsIgnoreCase(user.getUserId())){
            this.balance = balance;
            changeBalance = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestCode.UPDATE_USER){
            boolean changeBalance = data.getBooleanExtra(Symbol.CHANGE_BALANCE.GetValue(), false);
            if(changeBalance){
                this.balance = data.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
            }
            String imgLink = data.getStringExtra(Symbol.IMAGE_ACCOUNT_LINK.GetValue());
            CheckImageLink(imgLink);
            if(resultCode == RESULT_OK){
                user.setAddress(data.getStringExtra(Symbol.ADDRESS.GetValue()));
                user.setDateOfbirth(data.getStringExtra(Symbol.DOB.GetValue()));
                FillUserProfile();
            }
        } else if(requestCode == RequestCode.VERIFY_ACCOUNT_CODE && resultCode == RESULT_OK){
            user.setImgID(data.getStringExtra(Symbol.IMAGE_ID.GetValue()));
            user.setCmnd(data.getStringExtra(Symbol.CMND.GetValue()));
            user.setStatus(data.getIntExtra(Symbol.STATUS.GetValue(), -1));
            FillUserProfile();
        } else if(requestCode == RequestCode.UPDATE_EMAIL){
            boolean change = data.getBooleanExtra(Symbol.CHANGE_BALANCE.GetValue(), false);
            if(change){
                this.balance = data.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
            }

            if(resultCode == RESULT_OK){
                user.setEmail(data.getStringExtra(Symbol.EMAIL.GetValue()));
                FillUserProfile();
            }
        }
    }

    void CheckImageLink(String imgLink){
        if(!imgLink.equalsIgnoreCase(user.getImgAccountLink())){
            user.setImgAccountLink(imgLink);
            LoadImageAccount();
        }
    }
}