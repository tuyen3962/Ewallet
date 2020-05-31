package com.example.ewalletexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.ResponseModelByKey;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.storageFirebase.ResponseImageUri;
import com.example.ewalletexample.service.toolbar.CustomToolbarContext;
import com.example.ewalletexample.service.toolbar.ToolbarEvent;
import com.example.ewalletexample.utilies.Utilies;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainLayoutActivity extends AppCompatActivity implements ResponseImageUri, ResponseModelByKey<UserModel>, ToolbarEvent {

    long userAmount;
    View securityLayout;
    FirebaseAuth auth;
    BottomNavigationView bottomNavigation;
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    FirebaseStorageHandler storageHandler;
    ProgressBarManager progressBarManager;
    Uri imageUri;
    User user;
    UserModel model;
    CustomToolbarContext customToolbarContext;
    Gson gson;
    String firstKeyString, secondKeyString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
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
        gson = new Gson();
        securityLayout = findViewById(R.id.securityLayout);
        HideSecurityLayout(securityLayout);
        storageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference());
        bottomNavigation = findViewById(R.id.nav_view);
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), bottomNavigation);
        customToolbarContext = new CustomToolbarContext(this, this::BackToPreviousActivity);
        customToolbarContext.SetTitle("Màn hình chính");
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_notifications, R.id.navigation_personal)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigation, navController);
    }

    private void LoadDataFromIntent(){
        progressBarManager.ShowProgressBar("Loading");
        user = new User();
        Intent intent = getIntent();
        user = gson.fromJson(intent.getStringExtra(Symbol.USER.GetValue()), User.class);
        firstKeyString = intent.getStringExtra(Symbol.SECRET_KEY_01.GetValue());
        secondKeyString = intent.getStringExtra(Symbol.SECRET_KEY_02.GetValue());
        userAmount = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
        firebaseDatabaseHandler.GetModelByKey(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE, user.getUserId(), UserModel.class, this);
        FindImageUriFromInternet();
    }

    public void FindImageUriFromInternet(){
        if(!user.getAvatar().isEmpty()){
            storageHandler.GetUriImageFromServerFile(user.getAvatar(), this);
        } else {
            this.imageUri = null;
            progressBarManager.HideProgressBar();
        }
    }

    public String GetFirstKeyString(){
        return firstKeyString;
    }

    public String GetSecondKeyString(){
        return secondKeyString;
    }

    @Override
    public void GetImageUri(Uri imageUri) {
        this.imageUri = imageUri;
        progressBarManager.HideProgressBar();
    }

    public void SetImageUriForImageView(CircleImageView circleImageView){
        if(imageUri != null){
            Glide.with(this).load(imageUri).into(circleImageView);
        }else {
            Utilies.SetImageDrawable(this, circleImageView);
        }
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
        user = gson.fromJson(json, User.class);
    }

    @Override
    public void GetModel(UserModel data) {
        this.model = data;
    }

    public Gson GetGson(){
        return gson;
    }

    public void SetUserSecurityInfo(String cmnd, String imgCMNDFront, String imgCMNDBack){
        user.setCmnd(cmnd);
        user.setCmndFrontImage(imgCMNDFront);
        user.setCmndBackImage(imgCMNDBack);
    }

    public void ShowSecurityLayout(){
        securityLayout.setVisibility(View.VISIBLE);
    }

    public void HideSecurityLayout(View view){
        securityLayout.setVisibility(View.GONE);
    }

    public void SecurityAccount(View view){
        Intent intent = new Intent(MainLayoutActivity.this, VerifyAccountActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), user.getUserId());
        intent.putExtra(Symbol.UPDATE_SYMBOL.GetValue(), Symbol.UPDATE_FOR_INFORMATION.GetValue());
        intent.putExtra(Symbol.FULLNAME.GetValue(), user.getFullName());
        intent.putExtra(Symbol.CMND.GetValue(), user.getCmnd());
        intent.putExtra(Symbol.IMAGE_CMND_FRONT.GetValue(), user.getCmndFrontImage());
        intent.putExtra(Symbol.IMAGE_CMND_BACK.GetValue(), user.getCmndBackImage());
        startActivityForResult(intent, RequestCode.VERIFY_ACCOUNT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.VERIFY_ACCOUNT_CODE && resultCode == RESULT_OK){
            user.setCmndBackImage(data.getStringExtra(Symbol.IMAGE_CMND_BACK.GetValue()));
            user.setCmndFrontImage(data.getStringExtra(Symbol.IMAGE_CMND_FRONT.GetValue()));
            user.setCmnd(data.getStringExtra(Symbol.CMND.GetValue()));
        }
    }

    @Override
    public void BackToPreviousActivity() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
