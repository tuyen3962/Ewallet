package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.service.AnimationManager;
import com.example.ewalletexample.service.MemoryPreference.SharedPreferenceLocal;
import com.example.ewalletexample.service.UserSelectFunction;
import com.example.ewalletexample.service.recycleview.listitem.RecycleViewListSingleItem;
import com.example.ewalletexample.service.toolbar.CustomToolbarContext;
import com.example.ewalletexample.service.websocket.WebsocketClient;
import com.google.android.material.textview.MaterialTextView;

public class SettingActivity extends AppCompatActivity implements UserSelectFunction<String> {

    View layoutListItem, listItem;
    RecyclerView rvListItem;
    MaterialTextView tvTitle, tvLanguage;
    SharedPreferenceLocal local;
    RecycleViewListSingleItem recycleViewListSingleItem;
    String userid, secretKeyString1, secretKeyString2;
    AnimationManager animationManager;
    WebsocketClient websocketClient;
    CustomToolbarContext customToolbarContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Initialize();
        GetValueFromIntent();
    }

    void Initialize(){
        rvListItem = findViewById(R.id.rvListItems);
        listItem = findViewById(R.id.listItem);
        layoutListItem = findViewById(R.id.layoutListItem);
        tvTitle = findViewById(R.id.tvTitle);
        tvLanguage = findViewById(R.id.tvLanguage);
        customToolbarContext = new CustomToolbarContext(this, "Cài đặt", this::BackToPreviousActivity);
        layoutListItem.setVisibility(View.GONE);
        animationManager = new AnimationManager(this);
        local = new SharedPreferenceLocal(this, Symbol.NAME_PREFERENCES.GetValue());
        rvListItem.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        secretKeyString1 = intent.getStringExtra(Symbol.SECRET_KEY_01.GetValue());
        secretKeyString2 = intent.getStringExtra(Symbol.SECRET_KEY_02.GetValue());
        websocketClient = new WebsocketClient(this, userid);
    }

    public void ShowListLanguage(View view){
        String[] languages = getResources().getStringArray(R.array.language);
        recycleViewListSingleItem = new RecycleViewListSingleItem(this, languages, this);
        rvListItem.setAdapter(recycleViewListSingleItem);
        tvTitle.setText("Ngôn ngữ");
        layoutListItem.setVisibility(View.VISIBLE);
        animationManager.ShowAnimationView(listItem);
    }

    public void HideListItemLayout(View view){
        animationManager.HideAnimationView(layoutListItem);
    }

    public void UserLogoutPhoneEvent(View view){
        Intent intent = new Intent();
        intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), websocketClient.IsUpdateBalance());
        intent.putExtra(Symbol.AMOUNT.GetValue(), websocketClient.getNewBalance());
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void ChangeNewPassword(View view){
        Intent intent = new Intent(SettingActivity.this, ChangeNewPasswordActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.SECRET_KEY_01.GetValue(), secretKeyString1);
        intent.putExtra(Symbol.SECRET_KEY_02.GetValue(), secretKeyString2);
        startActivity(intent);
    }

    @Override
    public void SelectModel(String model) {
        tvLanguage.setText(model);
    }

    void BackToPreviousActivity(){
        Intent intent = new Intent();
        intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), websocketClient.IsUpdateBalance());
        intent.putExtra(Symbol.AMOUNT.GetValue(), websocketClient.getNewBalance());
        setResult(RESULT_OK, intent);
        finish();
    }
}
