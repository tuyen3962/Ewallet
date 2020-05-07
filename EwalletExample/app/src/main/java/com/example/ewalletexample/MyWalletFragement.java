package com.example.ewalletexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.service.BalanceVisible;

public class MyWalletFragement extends Fragment implements View.OnClickListener{
    private String userid;

    CircleImageView imgAccount;
    Button btnVisibilyBalance;
    LinearLayout layoutUserAccount, layoutSetting, layoutBankAccount, layoutHistoryTransaction;
    TextView tvBalance, tvFullName, tvPhone, tvNumCardConnected, tvEmail;

    private MainActivity mainActivity;

    public MyWalletFragement() {

    }

    public static MyWalletFragement newInstance(String userid) {
        MyWalletFragement fragment = new MyWalletFragement();
        Bundle args = new Bundle();
        args.putString(Symbol.USER_ID.GetValue(), userid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userid = getArguments().getString(Symbol.USER_ID.GetValue());
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof MainActivity){
            mainActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_wallet, container, false);

        Initialize(view);
        SetupUI();
        btnVisibilyBalance.setOnClickListener(this);
        layoutSetting.setOnClickListener(this);
        layoutUserAccount.setOnClickListener(this);
        layoutBankAccount.setOnClickListener(this);
        layoutHistoryTransaction.setOnClickListener(this);


        return view;
    }

    void Initialize(View view){
        imgAccount = view.findViewById(R.id.imgAccount);
        btnVisibilyBalance = view.findViewById(R.id.btnVisibilyBalance);
        layoutUserAccount = view.findViewById(R.id.layoutUserAccount);
        layoutSetting = view.findViewById(R.id.layoutSetting);
        layoutBankAccount = view.findViewById(R.id.layoutBankAccount);
        tvBalance = view.findViewById(R.id.tvBalance);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvNumCardConnected = view.findViewById(R.id.tvNumCard);
        tvPhone = view.findViewById(R.id.tvPhone);
        layoutHistoryTransaction = view.findViewById(R.id.layoutHistoryTransaction);
    }

    void SetupUI(){
        mainActivity.SetImageUriForImageView(imgAccount);
        tvBalance.setText(mainActivity.GetUserBalance() + "");
        SetTextviewDetail();
    }

    void SetTextviewDetail(){
        User user = mainActivity.GetUserInformation();
        if(user != null){
            tvFullName.setText(user.getFullName());
            tvPhone.setText(user.getPhoneNumber());
//            tvNumCardConnected.setText(mainActivity.GetNumCardConnected() + " thẻ liên kết");
            if(user.getEmail().isEmpty()){
                tvEmail.setText("");
                return;
            }
            tvEmail.setText(user.getEmail() + "(Đã xác thực)");
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnVisibilyBalance.getId()){
            SetVisibilityWalletAmount();
        }else if(v.getId() == layoutUserAccount.getId()){
            SwitchToPersonalDetailActivity();
        }else if(v.getId() == layoutBankAccount.getId()){
            mainActivity.SwitchToBankConnectedActivity();
        }else if(v.getId() == layoutHistoryTransaction.getId()){
            ShowTransactionHistory();
        }
    }

    private void SetVisibilityWalletAmount(){
        BalanceVisible.SwitchBalanceVisible();
        if (BalanceVisible.IsBalanceVisible()){
            tvBalance.setText(mainActivity.GetUserBalance() + "");
        }
        else{
            tvBalance.setText("******");
        }
    }

    void ShowTransactionHistory() {
        Intent intent = new Intent(mainActivity, HistoryTransactionActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.AMOUNT.GetValue(), mainActivity.userAmount);
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
        }
    }
}
