package com.example.ewalletexample;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.websocket.WebsocketClient;
import com.example.ewalletexample.service.websocket.WebsocketResponse;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

public class PersonalDetailActivity extends AppCompatActivity {
    FirebaseStorageHandler firebaseStorageHandler;
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    View layoutAddress, layoutDob, layoutImageAccount, layoutVerifyAccount, layoutEmail;
    TextView tvDateOfBirth, tvAddress, tvFullname, tvPhone, tvVerifyText, tvEmail;
    CircleImageView imgAccount;
    ProgressBarManager progressBarManager;

    User user;
    String firstKey, secondKey;
    Gson gson;
    long balance;
    WebsocketClient client;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_detail);
        Initialize();
        LoadDataFromIntent();
    }

    void Initialize(){
        gson = new Gson();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    void LoadDataFromIntent(){
        Intent intent = getIntent();
        user = gson.fromJson(intent.getStringExtra(Symbol.USER.GetValue()), User.class);
        user.setDate();
        firstKey = intent.getStringExtra(Symbol.SECRET_KEY_01.GetValue());
        secondKey = intent.getStringExtra(Symbol.SECRET_KEY_02.GetValue());
        client = new WebsocketClient(this, user.getUserId());
        FillUserProfile();
        LoadImageAccount();
    }

    private void FillUserProfile(){
        tvFullname.setText(user.getFullName());
        tvDateOfBirth.setText(user.getDateOfbirth());
        tvAddress.setText(user.getAddress());
        tvPhone.setText(user.getPhoneNumber());
        tvEmail.setText(user.getEmail());
        if(user.getStatus() == 0){
            tvVerifyText.setText("Chưa xác thực");
        } else if(user.getStatus() == 2){
            tvVerifyText.setText("Xác thực thất bại");
        } else{
            tvVerifyText.setText("Đã xác thực");
        }
    }

    void LoadImageAccount(){
        if(user.getAvatar().isEmpty()){
            imgAccount.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_person, null));
            return;
        }

        firebaseStorageHandler.LoadAccountImageFromLink(user.getAvatar(), imgAccount);
    }

    public void UploadUserInfo(View view){
        Intent intent = new Intent(PersonalDetailActivity.this, UpdateUserInformationActivity.class);
        intent.putExtra(Symbol.UPDATE_SYMBOL.GetValue(), Symbol.UPDATE_FOR_INFORMATION.GetValue());
        intent.putExtra(Symbol.USER.GetValue(), gson.toJson(user));
        intent.putExtra(Symbol.SECRET_KEY_01.GetValue(), firstKey);
        intent.putExtra(Symbol.SECRET_KEY_02.GetValue(), secondKey);
        startActivityForResult(intent, RequestCode.UPDATE_USER);
    }

    public void VerifyAccount(View view){
        Intent intent = new Intent(PersonalDetailActivity.this, VerifyAccountActivity.class);
        intent.putExtra(Symbol.UPDATE_SYMBOL.GetValue(), Symbol.UPDATE_FOR_INFORMATION.GetValue());
        intent.putExtra(Symbol.USER_ID.GetValue(), user.getUserId());
        intent.putExtra(Symbol.FULLNAME.GetValue(), user.getFullName());
        intent.putExtra(Symbol.CMND.GetValue(), user.getCmnd());
        intent.putExtra(Symbol.SECRET_KEY_01.GetValue(), firstKey);
        intent.putExtra(Symbol.SECRET_KEY_02.GetValue(), secondKey);
        intent.putExtra(Symbol.IMAGE_CMND_FRONT.GetValue(), user.getCmndFrontImage());
        intent.putExtra(Symbol.IMAGE_CMND_BACK.GetValue(), user.getCmndBackImage());
        startActivityForResult(intent, RequestCode.VERIFY_ACCOUNT_CODE);
    }

    public void BackToMainEvent(View view){
        Intent intent = new Intent();
        intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), client.IsUpdateBalance());
        intent.putExtra(Symbol.AMOUNT.GetValue(), client.getNewBalance());
        intent.putExtra(Symbol.USER.GetValue(), gson.toJson(user));
        setResult(RESULT_OK, intent);
        finish();
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
                String address = data.getStringExtra(Symbol.ADDRESS.GetValue());
                String dob = data.getStringExtra(Symbol.DOB.GetValue());
                String email = data.getStringExtra(Symbol.EMAIL.GetValue());
                if (address.length() > 0){
                    user.setAddress(address);
                }
                if (dob.length() > 0){
                    user.setDateOfbirth(dob);
                }
                if (email.length() > 0){
                    user.setEmail(email);
                }
                FillUserProfile();
            }
        } else if(requestCode == RequestCode.VERIFY_ACCOUNT_CODE && resultCode == RESULT_OK){
            user.setCmndFrontImage(data.getStringExtra(Symbol.IMAGE_CMND_FRONT.GetValue()));
            user.setCmndBackImage(data.getStringExtra(Symbol.IMAGE_CMND_BACK.GetValue()));
            user.setCmnd(data.getStringExtra(Symbol.CMND.GetValue()));
            FillUserProfile();
        }
    }

    void CheckImageLink(String imgLink){
        if(!imgLink.equalsIgnoreCase(user.getAvatar())){
            user.setAvatar(imgLink);
            LoadImageAccount();
        }
    }
}