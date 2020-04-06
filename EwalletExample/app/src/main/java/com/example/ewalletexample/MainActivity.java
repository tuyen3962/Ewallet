package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.ewalletexample.Server.RequestServerAPI;
import com.example.ewalletexample.Server.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.HandleDataFromFirebaseDatabase;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.storageFirebase.ResponseImageUri;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements HandleDataFromFirebaseDatabase<UserModel>, ResponseImageUri {

    String userid, imgAccountLink, fullName;
    long userAmount;
    BottomNavigationView bottomNavigation;
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    FirebaseStorageHandler storageHandler;
    ProgressBarManager progressBarManager;
    List<BankInfo> bankInfoList;
    Uri imageUri;

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_main:
                            openFragment(HomeFragment.newInstance(userid, userAmount, imgAccountLink));
                            return true;
                        case R.id.navigation_history:
//                            openFragment(SmsFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_wallet:
                            openFragment(MyWalletFragement.newInstance(userid,userAmount, imgAccountLink, fullName));
                            return true;
                    }
                    return false;
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initialize();
        LoadDataFromIntent();
        GetImageUri();
        openFragment(HomeFragment.newInstance(userid, userAmount, imgAccountLink));
        LoadListBankInfo();
    }

    void LoadListBankInfo(){
//        progressBarManager.ShowProgressBar("Loading");

        JSONObject jsonObject = new JSONObject();

        try{
            jsonObject.put("userid", userid);

            new LoadListBankConnected().execute(ServerAPI.GET_BANK_LINKING_API.GetUrl(), jsonObject.toString());
        }catch (JSONException e){
            Log.d("TAG", "LoadListBankInfo: " + e.getMessage());
        }
    }

    void GetImageUri(){
        storageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        if(!imgAccountLink.isEmpty()){
            storageHandler.GetUriImageFromServerFile(imgAccountLink, this);
        }
    }

    @Override
    public void GetImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public void InitializeProgressBar(View... listButtons){
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), listButtons);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainLayoutFragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    void Initialize(){
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference(), this);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

    }

    private void LoadDataFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        userAmount = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
        imgAccountLink = intent.getStringExtra(Symbol.IMAGE_ACCOUNT_LINK.GetValue());
        fullName = intent.getStringExtra(Symbol.FULLNAME.GetValue());
    }

    public void SetImageViewByUri(CircleImageView imageView){
        Glide.with(this).load(imageUri).into(imageView);
    }

    public void SwitchToPersonalDetailActivity(){
        progressBarManager.ShowProgressBar("Loading");
        firebaseDatabaseHandler.RegisterDataListener();
    }

    @Override
    public void HandleDataModel(UserModel data) {
        if(data == null) return;

        Intent intent = new Intent(MainActivity.this, PersonalDetailActivity.class);
//        try {
            intent.putExtra(Symbol.FULLNAME.GetValue(), data.getFullname());
            intent.putExtra(Symbol.USER_ID.GetValue(), userid);
            intent.putExtra(Symbol.IMAGE_ACCOUNT_LINK.GetValue(), imgAccountLink);
            intent.putExtra(Symbol.AMOUNT.GetValue(), userAmount);
            startActivity(intent);
//        } catch (JSONException e) {
//            return;
//        }
    }

    @Override
    public void HandleDataSnapShot(DataSnapshot dataSnapshot) {
        UserModel model = firebaseDatabaseHandler.GetUserModelByKey(dataSnapshot, userid, UserModel.class);
        firebaseDatabaseHandler.UnregisterValueListener(model);
    }

    @Override
    public void HandlerDatabaseError(DatabaseError databaseError) {

    }

    class LoadListBankConnected extends RequestServerAPI implements RequestServerFunction{
        public LoadListBankConnected(){
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
//            if(jsonData.has("cards")){
//
//            }
//            else{
//                bankInfoList = new ArrayList<>();
//            }

            bankInfoList = new ArrayList<>();

//            progressBarManager.HideProgressBar();
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }
}
