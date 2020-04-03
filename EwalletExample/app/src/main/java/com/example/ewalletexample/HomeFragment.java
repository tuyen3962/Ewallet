package com.example.ewalletexample;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ewalletexample.Symbol.Symbol;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private String userid, imgAccountLink;
    private long amount;

    CircleImageView imgAccount;
    TextView tvBalance;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String userid, long amount, String imgAccount) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(Symbol.USER_ID.GetValue(), userid);
        args.putLong(Symbol.AMOUNT.GetValue(), amount);
        args.putString(Symbol.IMAGE_ACCOUNT_LINK.GetValue(), imgAccount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userid = getArguments().getString(Symbol.USER_ID.GetValue());
            amount = getArguments().getLong(Symbol.AMOUNT.GetValue());
            imgAccountLink = getArguments().getString(Symbol.IMAGE_ACCOUNT_LINK.GetValue());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    void Initialize(View view){
        imgAccount = view.findViewById(R.id.imgAccount);
        tvBalance = view.findViewById(R.id.tvBalance);
    }
}
