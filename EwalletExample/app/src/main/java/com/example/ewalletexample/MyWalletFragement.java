package com.example.ewalletexample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.service.BalanceVisible;

public class MyWalletFragement extends Fragment implements View.OnClickListener{
    private String userid, imgAccountLink;
    private long userAmount;

    CircleImageView imgAccount;
    Button btnVisibilyBalance;
    LinearLayout layoutBankAccount, layoutSetting;
    TextView tvBalance;

    private MainActivity mainActivity;

    public MyWalletFragement() {

    }

    public static MyWalletFragement newInstance(String userid, long userAmount,String imgAccountLink) {
        MyWalletFragement fragment = new MyWalletFragement();
        Bundle args = new Bundle();
        args.putString(Symbol.USER_ID.GetValue(), userid);
        args.putLong(Symbol.AMOUNT.GetValue(), userAmount);
        args.putString(Symbol.IMAGE_ACCOUNT_LINK.GetValue(), imgAccountLink);
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

        imgAccount = view.findViewById(R.id.imgAccount);
        btnVisibilyBalance = view.findViewById(R.id.btnVisibilyBalance);
        layoutBankAccount = view.findViewById(R.id.layoutBankAccount);
        layoutSetting = view.findViewById(R.id.layoutSetting);
        tvBalance = view.findViewById(R.id.tvBalance);

        btnVisibilyBalance.setOnClickListener(this);
        layoutSetting.setOnClickListener(this);
        layoutBankAccount.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnVisibilyBalance.getId()){
            SetVisibilityWalletAmount();
        }else if(v.getId() == layoutBankAccount.getId()){
            startActivity(new Intent(mainActivity, ChooseBankConnectActivity.class));
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
}
