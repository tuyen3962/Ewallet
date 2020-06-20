package com.example.ewalletexample.ui.mywallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ewalletexample.HistoryTransactionActivity;
import com.example.ewalletexample.LoginActivity;
import com.example.ewalletexample.MainLayoutActivity;
import com.example.ewalletexample.PersonalDetailActivity;
import com.example.ewalletexample.R;
import com.example.ewalletexample.SettingActivity;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.UserBankCardActivity;
import com.example.ewalletexample.VerifyAccountActivity;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.service.MemoryPreference.SharedPreferenceLocal;
import com.example.ewalletexample.ui.shareData.ShareDataViewModel;
import com.example.ewalletexample.utilies.GsonUtils;
import com.example.ewalletexample.utilies.Utilies;

public class MyWalletFragement extends Fragment implements View.OnClickListener{
    ShareDataViewModel shareDataViewModel;
    CircleImageView imgAccount;
    View layoutUserAccount, layoutSetting, layoutBankAccount, layoutHistoryTransaction, layoutSecurityAccount;
    TextView tvFullName, tvPhone;
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
        Initialize(view);
        SetupUI();
        layoutSetting.setOnClickListener(this);
        layoutUserAccount.setOnClickListener(this);
        layoutBankAccount.setOnClickListener(this);
        layoutHistoryTransaction.setOnClickListener(this);
        layoutSecurityAccount.setOnClickListener(this);
        SetupViewModel();

        return view;
    }

    void Initialize(View view){
        local = new SharedPreferenceLocal(mainActivity, Symbol.NAME_PREFERENCES.GetValue());
        imgAccount = view.findViewById(R.id.imgAccount);
        layoutUserAccount = view.findViewById(R.id.layoutUserAccount);
        layoutSetting = view.findViewById(R.id.layoutSetting);
        layoutBankAccount = view.findViewById(R.id.layoutBankAccount);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvPhone = view.findViewById(R.id.tvPhone);
        layoutHistoryTransaction = view.findViewById(R.id.layoutHistoryTransaction);
        layoutSecurityAccount = view.findViewById(R.id.layoutSecurityAccount);
    }

    void SetupUI(){
        if(mainActivity.getImageUri() != null){
            Glide.with(this).load(mainActivity.getImageUri()).into(imgAccount);
        }else {
            Utilies.SetImageDrawable(mainActivity, imgAccount);
        }
        SetTextviewDetail();
    }

    void SetTextviewDetail(){
        User user = mainActivity.GetUserInformation();
        if(user != null){
            tvFullName.setText(user.getFullName());
            tvPhone.setText(user.getPhoneNumber());
        }
    }

    void SetupViewModel(){
        shareDataViewModel = new ViewModelProvider(requireActivity()).get(ShareDataViewModel.class);
        shareDataViewModel.getImageUri().observe(getViewLifecycleOwner(), image -> {
            if(image != null){
                Glide.with(this).load(image).into(imgAccount);
            }else {
                Utilies.SetImageDrawable(mainActivity, imgAccount);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == layoutUserAccount.getId()){
            SwitchToPersonalDetailActivity();
        }else if(v.getId() == layoutBankAccount.getId()){
            SwitchToBankConnectedActivity();
        }else if(v.getId() == layoutHistoryTransaction.getId()){
            ShowTransactionHistory();
        } else if(v.getId() == layoutSetting.getId()){
            ShowLayoutSetting();
        } else if(v.getId() == layoutSecurityAccount.getId()){
            SecurityAccount();
        }
    }

    void ShowLayoutSetting(){
        Intent intent = new Intent(mainActivity, SettingActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), mainActivity.GetUserInformation().getUserId());
        intent.putExtra(Symbol.SECRET_KEY_01.GetValue(), mainActivity.GetFirstKeyString());
        intent.putExtra(Symbol.SECRET_KEY_02.GetValue(), mainActivity.GetSecondKeyString());
        startActivityForResult(intent, RequestCode.SETTING_ACCOUNT);
    }

    void ShowTransactionHistory() {
        Intent intent = new Intent(mainActivity, HistoryTransactionActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), mainActivity.GetUserInformation().getUserId());
        intent.putExtra(Symbol.AMOUNT.GetValue(), mainActivity.GetUserBalance());
        intent.putExtra(Symbol.PHONE.GetValue(), mainActivity.GetUserInformation().getPhoneNumber());
        intent.putExtra(Symbol.SECRET_KEY_01.GetValue(), mainActivity.GetFirstKeyString());
        intent.putExtra(Symbol.SECRET_KEY_02.GetValue(), mainActivity.GetSecondKeyString());
        startActivity(intent);
    }

    public void SwitchToPersonalDetailActivity(){
        Intent intent = new Intent(mainActivity, PersonalDetailActivity.class);
        intent.putExtra(Symbol.USER.GetValue(), GsonUtils.toJsonString(mainActivity.GetUserInformation()));
        intent.putExtra(Symbol.SECRET_KEY_01.GetValue(), mainActivity.GetFirstKeyString());
        intent.putExtra(Symbol.SECRET_KEY_02.GetValue(), mainActivity.GetSecondKeyString());
        startActivityForResult(intent, RequestCode.MODIFY_INFORMATION);
    }

    public void SecurityAccount(){
        User user = mainActivity.GetUserInformation();
        Intent intent = new Intent(mainActivity, VerifyAccountActivity.class);
        intent.putExtra(Symbol.UPDATE_SYMBOL.GetValue(), Symbol.UPDATE_FOR_INFORMATION.GetValue());
        intent.putExtra(Symbol.USER_ID.GetValue(), user.getUserId());
        intent.putExtra(Symbol.FULLNAME.GetValue(), user.getFullName());
        intent.putExtra(Symbol.CMND.GetValue(), user.getCmnd());
        intent.putExtra(Symbol.IMAGE_CMND_FRONT.GetValue(), user.getCmndFrontImage());
        intent.putExtra(Symbol.IMAGE_CMND_BACK.GetValue(), user.getCmndBackImage());
        intent.putExtra(Symbol.SECRET_KEY_01.GetValue(), mainActivity.GetFirstKeyString());
        intent.putExtra(Symbol.SECRET_KEY_02.GetValue(), mainActivity.GetSecondKeyString());
        startActivityForResult(intent, RequestCode.VERIFY_ACCOUNT_CODE);
    }

    public void SwitchToBankConnectedActivity(){
        User user = mainActivity.GetUserInformation();
        Intent intent = new Intent(mainActivity, UserBankCardActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), user.getUserId());
        intent.putExtra(Symbol.AMOUNT.GetValue(), mainActivity.GetUserBalance());
        intent.putExtra(Symbol.CMND.GetValue(), user.getCmnd());
        intent.putExtra(Symbol.SECRET_KEY_01.GetValue(), mainActivity.GetFirstKeyString());
        intent.putExtra(Symbol.SECRET_KEY_02.GetValue(), mainActivity.GetSecondKeyString());
        startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestCode.MODIFY_INFORMATION && resultCode == ((Activity)mainActivity).RESULT_OK){
            boolean changeBalance = data.getBooleanExtra(Symbol.CHANGE_BALANCE.GetValue(), false);
            if(changeBalance){
                mainActivity.UpdateWallet(data.getLongExtra(Symbol.AMOUNT.GetValue(), 0));
            }
            String imageLink = mainActivity.GetUserInformation().getAvatar();
            mainActivity.SetUserInformationByJson(data.getStringExtra(Symbol.USER.GetValue()));
            if(!imageLink.equalsIgnoreCase(mainActivity.GetUserInformation().getAvatar())){
                mainActivity.FindImageUriFromInternet();
            }
        } else if (requestCode == RequestCode.SETTING_ACCOUNT){
            boolean isChange = data.getBooleanExtra(Symbol.CHANGE_BALANCE.GetValue(), false);
            if (isChange) {
                mainActivity.UpdateWallet(data.getLongExtra(Symbol.AMOUNT.GetValue(), 0));
            }

            if (resultCode == ((Activity)mainActivity).RESULT_CANCELED){
                mainActivity.BackToPreviousActivity();
            }
        } else if (requestCode == RequestCode.VERIFY_ACCOUNT_CODE){
            boolean isChange = data.getBooleanExtra(Symbol.CHANGE_BALANCE.GetValue(), false);
            if (isChange) {
                mainActivity.UpdateWallet(data.getLongExtra(Symbol.AMOUNT.GetValue(), 0));
            }

            if (resultCode == ((Activity)mainActivity).RESULT_OK){
                String cmndFrontImage = data.getStringExtra(Symbol.IMAGE_CMND_FRONT.GetValue());
                String cmndBackImage = data.getStringExtra(Symbol.IMAGE_CMND_BACK.GetValue());
                String cmnd = data.getStringExtra(Symbol.CMND.GetValue());
                mainActivity.SetUserSecurityInfo(cmnd, cmndFrontImage, cmndBackImage);
            }
        }
    }
}
