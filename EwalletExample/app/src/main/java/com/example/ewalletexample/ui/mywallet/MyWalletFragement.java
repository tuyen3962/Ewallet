package com.example.ewalletexample.ui.mywallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ewalletexample.HistoryTransactionActivity;
import com.example.ewalletexample.LoginActivity;
import com.example.ewalletexample.MainLayoutActivity;
import com.example.ewalletexample.PersonalDetailActivity;
import com.example.ewalletexample.R;
import com.example.ewalletexample.SettingActivity;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.service.BalanceVisible;
import com.example.ewalletexample.service.MemoryPreference.SharedPreferenceLocal;

public class MyWalletFragement extends Fragment implements View.OnClickListener{
    MyWalletViewModel myWalletViewModel;
    CircleImageView imgAccount;
    LinearLayout layoutUserAccount, layoutSetting, layoutBankAccount, layoutHistoryTransaction;
    TextView tvFullName, tvPhone, tvNumCardConnected;
    SharedPreferenceLocal local;

    MainLayoutActivity mainActivity;

    public MyWalletFragement() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof MainLayoutActivity){
            mainActivity = (MainLayoutActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_wallet, container, false);
        myWalletViewModel = ViewModelProviders.of(this).get(MyWalletViewModel.class);
        Initialize(view);
        SetupUI();
        layoutSetting.setOnClickListener(this);
        layoutUserAccount.setOnClickListener(this);
        layoutBankAccount.setOnClickListener(this);
        layoutHistoryTransaction.setOnClickListener(this);


        return view;
    }

    void Initialize(View view){
        local = new SharedPreferenceLocal(mainActivity, Symbol.NAME_PREFERENCES.GetValue());
        imgAccount = view.findViewById(R.id.imgAccount);
        layoutUserAccount = view.findViewById(R.id.layoutUserAccount);
        layoutSetting = view.findViewById(R.id.layoutSetting);
        layoutBankAccount = view.findViewById(R.id.layoutBankAccount);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvNumCardConnected = view.findViewById(R.id.tvNumCard);
        tvPhone = view.findViewById(R.id.tvPhone);
        layoutHistoryTransaction = view.findViewById(R.id.layoutHistoryTransaction);
    }

    void SetupUI(){
        mainActivity.SetImageUriForImageView(imgAccount);
        SetTextviewDetail();
    }

    void SetTextviewDetail(){
        User user = mainActivity.GetUserInformation();
        if(user != null){
            tvFullName.setText(user.getFullName());
            tvPhone.setText(user.getPhoneNumber());
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == layoutUserAccount.getId()){
            SwitchToPersonalDetailActivity();
        }else if(v.getId() == layoutBankAccount.getId()){
            mainActivity.SwitchToBankConnectedActivity();
        }else if(v.getId() == layoutHistoryTransaction.getId()){
            ShowTransactionHistory();
        } else if(v.getId() == layoutSetting.getId()){
            ShowLayoutSetting();
        }
    }

    void ShowLayoutSetting(){
        Intent intent = new Intent(mainActivity, SettingActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), mainActivity.GetUserInformation().getUserId());
        startActivityForResult(intent, RequestCode.SETTING_ACCOUNT);
    }

    void ShowTransactionHistory() {
        Intent intent = new Intent(mainActivity, HistoryTransactionActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), mainActivity.GetUserInformation().getUserId());
        intent.putExtra(Symbol.AMOUNT.GetValue(), mainActivity.GetUserBalance());
        intent.putExtra(Symbol.PHONE.GetValue(), mainActivity.GetUserInformation().getPhoneNumber());
        startActivity(intent);
    }

    public void SwitchToPersonalDetailActivity(){
        Intent intent = new Intent(mainActivity, PersonalDetailActivity.class);
        intent.putExtra(Symbol.USER.GetValue(), mainActivity.GetGson().toJson(mainActivity.GetUserInformation()));
        startActivityForResult(intent, RequestCode.MODIFY_INFORMATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestCode.MODIFY_INFORMATION && resultCode == ((Activity)mainActivity).RESULT_OK){
            boolean changeBalance = data.getBooleanExtra(Symbol.CHANGE_BALANCE.GetValue(), false);
            if(changeBalance){
                mainActivity.SetUserBalance(data.getLongExtra(Symbol.AMOUNT.GetValue(), 0));
            }
            String imageLink = mainActivity.GetUserInformation().getAvatar();
            mainActivity.SetUserInformationByJson(data.getStringExtra(Symbol.USER.GetValue()));
            if(!imageLink.equalsIgnoreCase(mainActivity.GetUserInformation().getAvatar())){
                mainActivity.FindImageUriFromInternet();
            }
        } else if (requestCode == RequestCode.SETTING_ACCOUNT){
            if (resultCode == ((Activity)mainActivity).RESULT_CANCELED){
                Intent intent = new Intent(mainActivity, LoginActivity.class);
                intent.putExtra(Symbol.FULLNAME.GetValue(), local.GetValueStringByKey(Symbol.KEY_FULL_NAME.GetValue()));
                intent.putExtra(Symbol.PHONE.GetValue(), local.GetValueStringByKey(Symbol.KEY_PHONE.GetValue()));
                startActivity(intent);
            }
        }
    }
}
