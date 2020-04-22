package com.example.ewalletexample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.BalanceVisible;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        ShowAccountImage();

        btnVisibilyBalance.setOnClickListener(this);
        layoutSetting.setOnClickListener(this);
        layoutUserAccount.setOnClickListener(this);
        layoutBankAccount.setOnClickListener(this);
        layoutHistoryTransaction.setOnClickListener(this);

        SetTextviewDetail();

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

    void SetTextviewDetail(){
        mainActivity.SetBalanceText(tvBalance);
        UserModel model = mainActivity.GetUserModel();
        if(model != null){
            tvFullName.setText(model.getFullname());
            tvPhone.setText(model.getPhone());
//            tvNumCardConnected.setText(mainActivity.GetNumCardConnected() + " thẻ liên kết");
            tvEmail.setText(model.getEmail());
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnVisibilyBalance.getId()){
            SetVisibilityWalletAmount();
        }else if(v.getId() == layoutUserAccount.getId()){
            mainActivity.SwitchToPersonalDetailActivity();
        }else if(v.getId() == layoutBankAccount.getId()){
            mainActivity.SwitchToBankConnectedActivity();
        }else if(v.getId() == layoutHistoryTransaction.getId()){
            ShowTransactionHistory();
        }
    }

    private void SetVisibilityWalletAmount(){
        BalanceVisible.SwitchBalanceVisible();
        if (BalanceVisible.IsBalanceVisible()){
            tvBalance.setText("123456");
        }
        else{
            tvBalance.setText("******");
        }
    }

    public void setBalanceText(String balance){
        tvBalance = getView().findViewById(R.id.tvBalance);
        tvBalance.setText(balance);
    }

    void ShowAccountImage(){
        mainActivity.SetImageViewByUri(imgAccount);
    }

    void ShowTransactionHistory() {
        Intent intent = new Intent(mainActivity, HistoryTransactionActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.AMOUNT.GetValue(), mainActivity.userAmount);
        intent.putExtra(Symbol.PHONE.GetValue(), mainActivity.GetUserModel().getPhone());
        startActivity(intent);
    }
}
