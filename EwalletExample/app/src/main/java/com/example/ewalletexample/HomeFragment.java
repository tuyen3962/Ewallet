package com.example.ewalletexample;

import android.content.Context;
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

    private String userid, imgAccountLink;
    private long amount;

    CircleImageView imgAccount;
    TextView tvBalance;
    MainActivity mainActivity;

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
            amount = getArguments().getLong(Symbol.AMOUNT.GetValue());
            imgAccountLink = getArguments().getString(Symbol.IMAGE_ACCOUNT_LINK.GetValue());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Initialize(view);
        mainActivity.InitializeProgressBar(null);
        ShowAccountImage();
        return view;
    }

    void Initialize(View view){
        imgAccount = view.findViewById(R.id.imgAccount);
        tvBalance = view.findViewById(R.id.tvBalance);
    }

    void ShowAccountImage(){
        if(TextUtils.isEmpty(imgAccountLink)){
            imgAccount.setImageDrawable(mainActivity.getResources().getDrawable(R.drawable.ic_action_person, null));
            return;
        }

        mainActivity.SetImageViewByUri(imgAccount);
    }
}
