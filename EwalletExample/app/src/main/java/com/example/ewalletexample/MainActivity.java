package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.example.ewalletexample.Server.balance.BalanceResponse;
import com.example.ewalletexample.Server.balance.GetBalanceAPI;
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
import com.example.ewalletexample.service.realtimeDatabase.ResponseModelByKey;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.storageFirebase.ResponseImageUri;
import com.example.ewalletexample.service.websocket.WebsocketClient;
import com.example.ewalletexample.service.websocket.WebsocketResponse;
import com.example.ewalletexample.utilies.Utilies;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements ResponseImageUri, BalanceResponse, ResponseModelByKey<UserModel> {
    private static String HOME = "HOME", MY_WALLET = "MY_WALLET";
    long userAmount;
    FirebaseAuth auth;
    BottomNavigationView bottomNavigation;
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    FirebaseStorageHandler storageHandler;
    ProgressBarManager progressBarManager;
    Uri imageUri;
    User user;
    UserModel model;

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_main:
                            openFragment(HomeFragment.newInstance(user.getUserId()), HOME);
                            return true;
                        case R.id.navigation_history:
//                            openFragment(SmsFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_wallet:
                            openFragment(MyWalletFragement.newInstance(user.getUserId()), MY_WALLET);
                            return true;
                    }
                    return false;
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Setup();
        Initialize();
        LoadDataFromIntent();
    }

    void Setup(){
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            auth.signOut();;
        }
    }

    void Initialize(){
        storageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference());
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), bottomNavigation);
    }

    private void LoadDataFromIntent(){
        progressBarManager.ShowProgressBar("Loading");
        user = new User();
        Intent intent = getIntent();
        user.ReadJson(intent.getStringExtra(Symbol.USER.GetValue()));
        GetBalanceAPI balanceAPI = new GetBalanceAPI(user.getUserId(), this);
        balanceAPI.GetBalance();
        firebaseDatabaseHandler.GetModelByKey(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE, user.getUserId(), UserModel.class, this);
    }

    public void FindImageUriFromInternet(){
        if(!user.getImgAccountLink().isEmpty()){
            storageHandler.GetUriImageFromServerFile(user.getImgAccountLink(), this);
        } else {
            this.imageUri = null;
            openFragment(HomeFragment.newInstance(user.getUserId()), HOME);
            progressBarManager.HideProgressBar();
        }
    }

    @Override
    public void GetImageUri(Uri imageUri) {
        this.imageUri = imageUri;
        openFragment(HomeFragment.newInstance(user.getUserId()), HOME);

        progressBarManager.HideProgressBar();
    }

    public void SetImageUriForImageView(CircleImageView circleImageView){
        if(imageUri != null){
            Glide.with(this).load(imageUri).into(circleImageView);
        }else {
            Utilies.SetImageDrawable(this, circleImageView);
        }
    }

    public void openFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainLayoutFragment, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public long GetUserBalance(){
        return userAmount;
    }

    public void SetUserBalance(long balance){
        this.userAmount = balance;
    }

    public User GetUserInformation(){
        return user;
    }

    public void SetUserInformationByJson(String json){
        user.ReadJson(json);
    }

    public void SwitchToBankConnectedActivity(){
        Intent intent = new Intent(MainActivity.this, UserBankCardActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), user.getUserId());
        intent.putExtra(Symbol.AMOUNT.GetValue(), userAmount);
        startActivity(intent);
    }

    @Override
    public void GetBalanceResponse(long balance) {
        userAmount = balance;
        FindImageUriFromInternet();
    }

    @Override
    public void GetModel(UserModel data) {
        this.model = data;
        auth.signInWithCustomToken(model.getPhoneToken());
    }
}
