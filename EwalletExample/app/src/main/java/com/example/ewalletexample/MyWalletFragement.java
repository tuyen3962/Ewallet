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
import com.example.ewalletexample.service.BalanceVisible;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyWalletFragement extends Fragment implements View.OnClickListener{
    private String userid, imgAccountLink, fullname;
    private long userAmount;

    CircleImageView imgAccount;
    Button btnVisibilyBalance;
    LinearLayout layoutUserAccount, layoutSetting;
    TextView tvBalance;

    private MainActivity mainActivity;

    public MyWalletFragement() {

    }

    public static MyWalletFragement newInstance(String userid, long userAmount, String imgAccountLink, String fullname) {
        MyWalletFragement fragment = new MyWalletFragement();
        Bundle args = new Bundle();
        args.putString(Symbol.USER_ID.GetValue(), userid);
        args.putLong(Symbol.AMOUNT.GetValue(), userAmount);
        args.putString(Symbol.IMAGE_ACCOUNT_LINK.GetValue(), imgAccountLink);
        args.putString(Symbol.FULLNAME.GetValue(), fullname);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userid = getArguments().getString(Symbol.USER_ID.GetValue());
            userAmount = getArguments().getLong(Symbol.AMOUNT.GetValue());
            imgAccountLink = getArguments().getString(Symbol.IMAGE_ACCOUNT_LINK.GetValue());
            fullname = getArguments().getString(Symbol.FULLNAME.GetValue());
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
        mainActivity.InitializeProgressBar(btnVisibilyBalance, layoutSetting, layoutUserAccount);
        ShowAccountImage();

        btnVisibilyBalance.setOnClickListener(this);
        layoutSetting.setOnClickListener(this);
        layoutUserAccount.setOnClickListener(this);

        return view;
    }

    void Initialize(View view){
        imgAccount = view.findViewById(R.id.imgAccount);
        btnVisibilyBalance = view.findViewById(R.id.btnVisibilyBalance);
        layoutUserAccount = view.findViewById(R.id.layoutUserAccount);
        layoutSetting = view.findViewById(R.id.layoutSetting);
        tvBalance = view.findViewById(R.id.tvBalance);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnVisibilyBalance.getId()){
            SetVisibilityWalletAmount();
        }else if(v.getId() == layoutUserAccount.getId()){
            mainActivity.SwitchToPersonalDetailActivity();
        }else{
            //switch to setting layout
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

    void ShowAccountImage(){
        if(TextUtils.isEmpty(imgAccountLink)){
            imgAccount.setImageDrawable(mainActivity.getResources().getDrawable(R.drawable.ic_action_person, null));
            return;
        }

        mainActivity.SetImageViewByUri(imgAccount);
    }
}
