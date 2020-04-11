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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ewalletexample.Symbol.Symbol;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private String userid;

    View topupLayout;
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
        ShowAccountImage();
        return view;
    }

    void Initialize(View view){
        imgAccount = view.findViewById(R.id.imgAccount);
        tvBalance = view.findViewById(R.id.tvBalance);
        topupLayout = view.findViewById(R.id.topupLayout);
    }

    void ShowAccountImage(){
        mainActivity.SetImageViewByUri(imgAccount);
    }

    public void TopupEvent(){
        Intent intent = new Intent(mainActivity, TopupWalletActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.AMOUNT.GetValue(), mainActivity.GetUserAmount());
        startActivity(intent);
    }

}
