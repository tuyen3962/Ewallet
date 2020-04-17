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
import android.widget.TextView;

import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.Symbol;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private String userid;

    View topupLayout, withdrawLayout, exchangeMoneyLayout, buyMobileCardLayout;
    CircleImageView imgAccount;
    TextView tvBalance;
    MainActivity mainActivity;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String userid) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(Symbol.USER_ID.GetValue(), userid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof MainActivity){
            mainActivity = (MainActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userid = getArguments().getString(Symbol.USER_ID.GetValue());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Initialize(view);
        mainActivity.SetBalanceText(tvBalance);

        topupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopupEvent();
            }
        });
        withdrawLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WithdrawEvent();
            }
        });
        exchangeMoneyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExchangeMoneyEvent();
            }
        });

        buyMobileCardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuyMobileCard();
            }
        });
        ShowAccountImage();
        return view;
    }

    void Initialize(View view){
        imgAccount = view.findViewById(R.id.imgAccount);
        tvBalance = view.findViewById(R.id.tvBalance);
        topupLayout = view.findViewById(R.id.topupLayout);
        withdrawLayout = view.findViewById(R.id.withdrawLayout);
        exchangeMoneyLayout = view.findViewById(R.id.exchangeMoneyLayout);
        buyMobileCardLayout = view.findViewById(R.id.buyMobileCardLayout);
    }

    void ShowAccountImage(){
        mainActivity.SetImageViewByUri(imgAccount);
    }

    public void TopupEvent(){
        Intent intent = new Intent(mainActivity, TopupWalletActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.AMOUNT.GetValue(), mainActivity.GetUserAmount());
        intent.putExtra(Symbol.SERVICE_TYPE.GetValue(), Service.TOPUP_SERVICE_TYPE.GetCode());
        startActivity(intent);
    }

    public void WithdrawEvent(){
        Intent intent = new Intent(mainActivity, TopupWalletActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.AMOUNT.GetValue(), mainActivity.GetUserAmount());
        intent.putExtra(Symbol.SERVICE_TYPE.GetValue(), Service.WITHDRAW_SERVICE_TYPE.GetCode());
        startActivity(intent);
    }

    public void ExchangeMoneyEvent(){
        Intent intent = new Intent(mainActivity, SearchUserExchangeActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.AMOUNT.GetValue(), mainActivity.GetUserAmount());
        startActivity(intent);
    }

    public void BuyMobileCard(){
        Intent intent = new Intent(mainActivity, SelectMobileCardFunctionActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.AMOUNT.GetValue(), mainActivity.GetUserAmount());
        intent.putExtra(Symbol.PHONE.GetValue(), mainActivity.GetUserModel().getPhone());
        startActivity(intent);
    }
}
