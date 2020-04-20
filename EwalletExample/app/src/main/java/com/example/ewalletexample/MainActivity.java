package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.ResponseModelByKey;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.storageFirebase.ResponseImageUri;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements ResponseImageUri, ResponseModelByKey<UserModel> {

    String userid;
    long userAmount;
    BottomNavigationView bottomNavigation;
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    FirebaseStorageHandler storageHandler;
    ProgressBarManager progressBarManager;
    int numBankConnected;
    Uri imageUri;
    UserModel model;

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_main:
                            openFragment(HomeFragment.newInstance(userid));
                            return true;
                        case R.id.navigation_history:
//                            openFragment(SmsFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_wallet:
                            openFragment(MyWalletFragement.newInstance(userid));
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
    }

    void Initialize(){
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference());
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), bottomNavigation);
    }

    private void LoadDataFromIntent(){
        progressBarManager.ShowProgressBar("Loading");

        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        userAmount = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);

        firebaseDatabaseHandler.GetModelByKey(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE, userid, UserModel.class,this);
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
        if(!model.getImgLink().isEmpty()){
            storageHandler.GetUriImageFromServerFile(model.getImgLink(), this);
        }
    }

    @Override
    public void GetImageUri(Uri imageUri) {
        this.imageUri = imageUri;
        openFragment(HomeFragment.newInstance(userid));
        LoadListBankInfo();
    }

    @Override
    public void GetModel(UserModel data) {
        model = data;
        GetImageUri();
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainLayoutFragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public long GetUserAmount(){
        return userAmount;
    }

    public void SetImageViewByUri(CircleImageView imageView){
        if(imageUri != null){
            Glide.with(this).load(imageUri).into(imageView);
        }
        else{
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_person, null));
        }
    }

    public UserModel GetUserModel(){
        return model;
    }

    public int GetNumCardConnected(){
        return numBankConnected;
    }

    public void SetBalanceText(TextView tvBalance){
        tvBalance.setText("$" + userAmount);
    }

    public void SwitchToPersonalDetailActivity(){
        Intent intent = new Intent(MainActivity.this, PersonalDetailActivity.class);
        intent.putExtra(Symbol.FULLNAME.GetValue(), model.getFullname());
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.IMAGE_ACCOUNT_LINK.GetValue(), model.getImgLink());
        intent.putExtra(Symbol.AMOUNT.GetValue(), userAmount);
        startActivity(intent);
    }

    public void SwitchToBankConnectedActivity(){
        if(numBankConnected == 0){
            Intent intent = new Intent(MainActivity.this, ChooseBankConnectActivity.class);
            intent.putExtra(Symbol.USER_ID.GetValue(), userid);
            intent.putExtra(Symbol.AMOUNT.GetValue(), userAmount);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(MainActivity.this, UserBankCardActivity.class);
            intent.putExtra(Symbol.USER_ID.GetValue(), userid);
            intent.putExtra(Symbol.AMOUNT.GetValue(), userAmount);
            startActivity(intent);
        }
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
            JSONArray bankArray = jsonData.getJSONArray("cards");
            numBankConnected = bankArray.length();
            progressBarManager.HideProgressBar();
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }
}
