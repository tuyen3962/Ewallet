package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.model.UserSearchModel;
import com.example.ewalletexample.service.UserSelectFunction;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.HandleDataFromFirebaseDatabase;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchUserExchangeActivity extends AppCompatActivity implements HandleDataFromFirebaseDatabase<UserModel>, UserSelectFunction {

    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    FirebaseStorageHandler firebaseStorageHandler;
    List<UserSearchModel> listUserRecommeend;
    ListUserProfileAdapter profileAdapter;
    EditText etUserSelected;
    ListView lvUserRecommend;
    String userid;
    long userAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_exchange);

        Initialize();
        GetValueFromIntent();
    }

    void Initialize(){
        listUserRecommeend = new ArrayList<>();
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference(), this);
        firebaseStorageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        etUserSelected = findViewById(R.id.etUserSearch);
        profileAdapter = new ListUserProfileAdapter(this, listUserRecommeend, this);
        lvUserRecommend = findViewById(R.id.lvUserRecommend);
        lvUserRecommend.setAdapter(profileAdapter);
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        userAmount = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
    }

    public void SearchReceiverPhoneRecommend(View view){
        firebaseDatabaseHandler.RegisterDataListener();
    }

    @Override
    public void SelectUserExchangeMoney(UserSearchModel model) {
        Intent intent = new Intent(SearchUserExchangeActivity.this, TopupWalletActivity.class);
        intent.putExtra(Symbol.SERVICE_TYPE.GetValue(), Service.EXCHANGE_SERVICE_TYPE.GetCode());
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.AMOUNT.GetValue(), userAmount);
        intent.putExtra(Symbol.USER_SEARCH_MODEL.GetValue(), model.ExchangeToJson());
        startActivity(intent);
    }

    @Override
    public void HandleDataModel(UserModel data) {
        if(listUserRecommeend.size() > 10){
            List<UserSearchModel> listTemp = new ArrayList<>(listUserRecommeend);
            listUserRecommeend.clear();
            Random random = new Random();
            for (int i = 0; i < 10; i++){
                int position = random.nextInt(listTemp.size());
                listUserRecommeend.add(listTemp.get(position));
                listTemp.remove(position);
            }
        }
        profileAdapter.notifyDataSetChanged();
    }

    @Override
    public void HandleDataSnapShot(DataSnapshot dataSnapshot) {
        String text = etUserSelected.getText().toString();
        boolean isPhone = false;
        if(text.matches("\\d{1,10}")){
            isPhone = true;
        }
        listUserRecommeend.clear();
        for (DataSnapshot data : dataSnapshot.child(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE.GetValue()).getChildren()){
            UserModel model = data.getValue(UserModel.class);
            String modelCheck;
            if(isPhone){
                modelCheck = model.getPhone();
            }
            else{
                modelCheck = model.getFullname();
            }
            if(modelCheck.contains(text)){
                UserSearchModel searchModel = new UserSearchModel();
                searchModel.setFullName(model.getFullname());
                searchModel.setPhone(model.getPhone());
                searchModel.setUserid(data.getKey());
                searchModel.setImgLink(model.getImgLink());
                listUserRecommeend.add(searchModel);
            }
        }

        firebaseDatabaseHandler.UnregisterValueListener(null);
    }

    @Override
    public void HandlerDatabaseError(DatabaseError databaseError) {

    }

    class ListUserProfileAdapter extends BaseAdapter {
        private UserSelectFunction userSelectFunction;
        private List<UserSearchModel> userModelList;
        private Context context;

        public ListUserProfileAdapter(Context context, List<UserSearchModel> userModels, UserSelectFunction function){
            this.context = context;
            userModelList = userModels;
            userSelectFunction = function;
        }

        @Override
        public int getCount() {
            return userModelList.size();
        }

        @Override
        public Object getItem(int position) {
            return userModelList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return userModelList.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.form_user_profile,null);
            }
            ImageView imgUser = convertView.findViewById(R.id.imgUserProfile);
            TextView tvName = convertView.findViewById(R.id.tvName);
            TextView tvPhone = convertView.findViewById(R.id.tvPhone);
            Button select = convertView.findViewById(R.id.btnAdd);

            final UserSearchModel model = userModelList.get(position);
            tvName.setText(model.getFullName());
            tvPhone.setText(model.getPhone());
            if (model.getImgLink().equalsIgnoreCase("")){
                imgUser.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_person, null));
            }
            else{
                firebaseStorageHandler.LoadAccountImageFromLink(model.getImgLink(), imgUser);
            }
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userSelectFunction.SelectUserExchangeMoney(model);
                }
            });

            return convertView;
        }
    }
}
