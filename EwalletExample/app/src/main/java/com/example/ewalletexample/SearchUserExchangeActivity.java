package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aldoapps.autoformatedittext.AutoFormatEditText;
import com.example.ewalletexample.Server.api.checklist.CheckListAPI;
import com.example.ewalletexample.Server.api.checklist.CheckListResponse;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.model.UserSearchModel;
import com.example.ewalletexample.service.CheckInputField;
import com.example.ewalletexample.service.UserSelectFunction;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.HandleDataFromFirebaseDatabase;
import com.example.ewalletexample.service.realtimeDatabase.ResponseModelByKey;
import com.example.ewalletexample.service.recycleview.search.RecycleViewFriendProfileAdapter;
import com.example.ewalletexample.service.recycleview.search.RecycleViewUserProfileAdapter;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.utilies.Utilies;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchUserExchangeActivity extends AppCompatActivity implements HandleDataFromFirebaseDatabase<UserModel>, UserSelectFunction<UserSearchModel>, ResponseModelByKey<UserModel>, CheckListResponse {
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    FirebaseStorageHandler firebaseStorageHandler;
    ProgressBarManager progressBarManager;
    List<UserSearchModel> listUserRecommend, listPhoneContacts, listFriendProfile;
    MaterialTextView tvSearchUser, tvContact, tvOtherContacts, tvTitleListReceiver;
    CheckBox cbSaveReceiverInfo;
    TextInputEditText etUserSearch, etNote;
    AutoFormatEditText etCash;
    ImageView imgUserProfile;
    RecyclerView lvUserRecommend, lvUserContact, lvFriendProfile;
    RecycleViewUserProfileAdapter userProfileAdapter, userInContactProfileAdapter;
    View layoutInfoTransaction, layoutInfoReceiver;
    String userid, phone;
    long userAmount;
    boolean hasGetContact;
    UserSearchModel currentReceiverModel;
    UserModel currentUserModel;
    CheckListAPI checkListAPI;
    GetListFriend getListFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_exchange);

        GetValueFromIntent();
        Initialize();
    }

    void Initialize(){
        listUserRecommend = new ArrayList<>();
        listFriendProfile = new ArrayList<>();
        cbSaveReceiverInfo = findViewById(R.id.cbSaveReceiverInfo);
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference(), this);
        firebaseStorageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        etCash = findViewById(R.id.etCash);
        etUserSearch = findViewById(R.id.etUserSearch);
        tvSearchUser = findViewById(R.id.tvSearchUser);
        lvUserRecommend = findViewById(R.id.lvUserRecommend);
        lvUserContact = findViewById(R.id.lvUserContact);
        lvFriendProfile = findViewById(R.id.lvFriend);
        tvTitleListReceiver = findViewById(R.id.tvTitleListReceiver);
        tvContact = findViewById(R.id.tvContact);
        tvOtherContacts = findViewById(R.id.tvOtherContacts);
        layoutInfoTransaction = findViewById(R.id.layoutInfoTransaction);
        layoutInfoReceiver = findViewById(R.id.layoutInfoReceiver);
        imgUserProfile = findViewById(R.id.imgUserProfile);
        checkListAPI = new CheckListAPI(this);
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar),
                findViewById(R.id.btnVerify), tvSearchUser, findViewById(R.id.btnFind));
        etNote = findViewById(R.id.etNote);
        lvUserRecommend.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        lvUserContact.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        lvFriendProfile.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        firebaseDatabaseHandler.GetModelByKey(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE, userid, UserModel.class, this);
        listPhoneContacts = new ArrayList<>();
        ShowLayoutTransactionDetail();
        hasGetContact = false;
        LoadContact();
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        userAmount = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
        phone = intent.getStringExtra(Symbol.PHONE.GetValue());
    }

    void LoadContact() {
        if(!hasPhoneContactsPermission(Manifest.permission.READ_CONTACTS)) {
            requestPermission(Manifest.permission.READ_CONTACTS);
        }
        else {
            List<String> phones = Utilies.LoadListContact(this);
            checkListAPI.SetList(phones);
            checkListAPI.SetPhone(phone);
            checkListAPI.StartRequest();
            hasGetContact = true;
        }
    }

    public void SearchReceiverPhoneRecommend(View view){
        firebaseDatabaseHandler.RegisterDataListener();
    }

    @Override
    public void HandleDataModel(UserModel data) {
        if(listUserRecommend.size() > 10){
            List<UserSearchModel> listTemp = new ArrayList<>(listUserRecommend);
            listUserRecommend.clear();
            Random random = new Random();
            for (int i = 0; i < 10; i++){
                int position = random.nextInt(listTemp.size());
                listUserRecommend.add(listTemp.get(position));
                listTemp.remove(position);
            }
        }

        userProfileAdapter = new RecycleViewUserProfileAdapter(this, this, listUserRecommend, firebaseStorageHandler);
        lvUserRecommend.setAdapter(userProfileAdapter);
    }

    @Override
    public void HandleDataSnapShot(DataSnapshot dataSnapshot) {
        String textSearch = etUserSearch.getText().toString();
        boolean isPhone = CheckInputField.IsPhone(textSearch);
        listUserRecommend.clear();
        for (DataSnapshot data : dataSnapshot.child(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE.GetValue()).getChildren()){
            UserModel model = data.getValue(UserModel.class);
            String modelCheck;
            if(isPhone){
                modelCheck = model.getPhone();
            }
            else{
                modelCheck = model.getFullname();
            }
            if(modelCheck.contains(textSearch)){
                UserSearchModel searchModel = new UserSearchModel();
                searchModel.setFullName(model.getFullname());
                searchModel.setPhone(model.getPhone());
                searchModel.setUserid(data.getKey());
                searchModel.setImgLink(model.getImgLink());
                listUserRecommend.add(searchModel);
            }
        }

        firebaseDatabaseHandler.UnregisterValueListener(null);
        if (listPhoneContacts.size() > 0){
            List<UserSearchModel> contacts = new ArrayList<>();
            for (UserSearchModel model : listPhoneContacts){
                if (isPhone){
                    if(model.getPhone().contains(textSearch)){
                        contacts.add(model);
                    }
                } else {
                    if(model.getFullName().contains(textSearch)){
                        contacts.add(model);
                    }
                }
            }
            if (contacts.isEmpty()){
                tvContact.setVisibility(View.GONE);
            } else {
                tvContact.setVisibility(View.VISIBLE);
                userInContactProfileAdapter = new RecycleViewUserProfileAdapter(this, this, contacts, firebaseStorageHandler);
                lvUserContact.setAdapter(userInContactProfileAdapter);
            }
        }
    }

    @Override
    public void HandlerDatabaseError(DatabaseError databaseError) {

    }

    void ShowLayoutTransactionDetail(){
        layoutInfoTransaction.setVisibility(View.VISIBLE);
        layoutInfoReceiver.setVisibility(View.GONE);
    }

    public void ShowLayoutInfoReceiver(View view){
        if (!hasGetContact){
            LoadContact();
            if (hasPhoneContactsPermission(Manifest.permission.READ_CONTACTS)){
                progressBarManager.ShowProgressBar("Dang lay so tu danh ba");
            } else {
                tvContact.setVisibility(View.GONE);
                lvUserContact.setVisibility(View.GONE);
            }
        }
        layoutInfoReceiver.setVisibility(View.VISIBLE);
        layoutInfoTransaction.setVisibility(View.GONE);
        etUserSearch.setText("");
        listUserRecommend.clear();
    }

    public void StartCreateExchangeOrder(View view){
        if (currentReceiverModel == null){
            tvSearchUser.setError("Tìm người nhận");
            return;
        }

        String cash = etCash.getText().toString();
        cash = cash.replaceAll(",","");
        long money = Long.parseLong(cash);
        if(cash.isEmpty()){
            etCash.setError("Hãy nhập số tiền");
            return;
        } else if(money < 999){
            etCash.setError("Giao dịch phải thực hiện trên 1,000đ");
            return;
        } else if(money > userAmount){
            etCash.setError("Bạn không đủ số tiền để chuyển. Xin nhập lại!");
            return;
        }
        Intent intent = new Intent(SearchUserExchangeActivity.this, SubmitOrderActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.AMOUNT_TRANSACTION.GetValue(), cash);
        intent.putExtra(Symbol.FEE_TRANSACTION.GetValue(), 0);
        intent.putExtra(Symbol.SERVICE_TYPE.GetValue(), Service.EXCHANGE_SERVICE_TYPE.GetCode());
        intent.putExtra(Symbol.NOTE.GetValue(), etNote.getText().toString());
        intent.putExtra(Symbol.RECEIVER_PHONE.GetValue(), currentReceiverModel.getPhone());
        intent.putExtra(Symbol.RECEIVER_FULL_NAME.GetValue(), currentReceiverModel.getFullName());
        intent.putExtra(Symbol.SOURCE_OF_FUND.GetValue(), SourceFund.WALLET_SOURCE_FUND.GetCode());
        startActivityForResult(intent, RequestCode.SUBMIT_ORDER);
    }

    @Override
    public void SelectModel(UserSearchModel model) {
        ShowLayoutTransactionDetail();
        SetCurrentReceiverModel(model);
    }

    @Override
    public void GetModel(UserModel data) {
        currentUserModel = data;
        List<String> friends = currentUserModel.getFriends();
        if (friends == null || (friends != null && friends.size() == 0)){
            tvTitleListReceiver.setVisibility(View.GONE);
            lvFriendProfile.setVisibility(View.GONE);
        } else {
            getListFriend = new GetListFriend(this, lvFriendProfile, firebaseStorageHandler, friends);
        }
    }

    private void SetCurrentReceiverModel(UserSearchModel model){
        this.currentReceiverModel = model;
        tvSearchUser.setText(model.getPhone());
        Utilies.LoadImageFromFirebase(this, firebaseStorageHandler, model.getImgLink(), imgUserProfile);
    }

    // Get the list of phone contacts
    @Override
    public void Response(List<UserSearchModel> models) {
        listPhoneContacts = models;
        if (listPhoneContacts.size() == 0){
            tvContact.setVisibility(View.GONE);
            lvUserContact.setVisibility(View.GONE);
        } else {
            tvContact.setVisibility(View.VISIBLE);
            lvUserContact.setVisibility(View.VISIBLE);
            userInContactProfileAdapter = new RecycleViewUserProfileAdapter(this, this, listPhoneContacts, firebaseStorageHandler);
            lvUserContact.setAdapter(userInContactProfileAdapter);
        }

        if (progressBarManager.IsVivisible()){
            progressBarManager.HideProgressBar();
        }
    }

    class GetListFriend implements CheckListResponse, UserSelectFunction<UserSearchModel>{
        private List<UserSearchModel> models;
        private CheckListAPI api;
        private RecyclerView lvFriendProfile;
        private RecycleViewFriendProfileAdapter friendProfileAdapter;
        private Context context;
        private FirebaseStorageHandler storageHandler;

        public GetListFriend(Context context, RecyclerView lvFriendProfile, FirebaseStorageHandler storageHandler, List<String> friends){
            this.lvFriendProfile = lvFriendProfile;
            this.context = context;
            this.storageHandler = storageHandler;
            api = new CheckListAPI(this);
            api.SetList(friends);
            api.StartRequest();
        }

        @Override
        public void Response(List<UserSearchModel> models) {
            listFriendProfile = models;
            this.models = models;
            friendProfileAdapter = new RecycleViewFriendProfileAdapter(context, storageHandler, this, models);
            lvFriendProfile.setAdapter(friendProfileAdapter);
        }

        @Override
        public void SelectModel(UserSearchModel model) {
            SetCurrentReceiverModel(model);
        }
    }

    private boolean hasPhoneContactsPermission(String permission)
    {
        boolean ret = false;

        // If android sdk version is bigger than 23 the need to check run time permission.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // return phone read contacts permission grant status.
            int hasPermission = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            // If permission is granted then return true.
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                ret = true;
            }
        }else
        {
            ret = true;
        }
        return ret;
    }

    private void requestPermission(String permission)
    {
        String requestPermissionArray[] = {permission};
        ActivityCompat.requestPermissions(this, requestPermissionArray, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int length = grantResults.length;
        if(length > 0)
        {
            int grantResult = grantResults[0];

            if(grantResult == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getApplicationContext(), "You allowed permission, please click the button again.", Toast.LENGTH_LONG).show();
            }else
            {
                Toast.makeText(getApplicationContext(), "You denied permission.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.SUBMIT_ORDER){
            if (resultCode == RESULT_OK){
                setResult(resultCode);
                finish();
            }
        }
    }
}