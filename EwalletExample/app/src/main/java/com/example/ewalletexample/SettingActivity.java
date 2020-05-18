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
import com.google.android.material.textview.MaterialTextView;

public class SettingActivity extends AppCompatActivity implements UserSelectFunction<String> {

    ImageButton btnBackToPreviousActivity;
    View layoutListItem, listItem;
    RecyclerView rvListItem;
    MaterialTextView tvTitle, tvLanguage, tvToolbarTitle;
    SharedPreferenceLocal local;
    RecycleViewListSingleItem recycleViewListSingleItem;
    String userid;
    Toolbar toolbar;
    AnimationManager animationManager;

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
        toolbar = findViewById(R.id.toolbarLayout);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        btnBackToPreviousActivity = findViewById(R.id.btnBackToPreviousActivity);
        tvToolbarTitle.setText("Cài đặt");
        setSupportActionBar(toolbar);
        layoutListItem.setVisibility(View.GONE);
        animationManager = new AnimationManager(this);
        local = new SharedPreferenceLocal(this, Symbol.NAME_PREFERENCES.GetValue());
        rvListItem.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        btnBackToPreviousActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackToPreviousActivity();
            }
        });
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
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
        setResult(RESULT_CANCELED);
        finish();
    }

    public void ChangeNewPassword(View view){
        Intent intent = new Intent(SettingActivity.this, ChangeNewPasswordActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        startActivity(intent);
    }

    @Override
    public void SelectModel(String model) {
        tvLanguage.setText(model);
    }

    void BackToPreviousActivity(){
        setResult(RESULT_OK);
        finish();
    }
}
