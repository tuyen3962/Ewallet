package com.example.ewalletexample;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class AddNewBankCardFragment extends Fragment implements View.OnClickListener{
    ListView listBankConnected;
    Button btnBack;
    LinearLayout layoutAddNewBankAccount;

    public AddNewBankCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_bank_card, container, false);

        Initialize(view);
        layoutAddNewBankAccount.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view){
        if(view.getId() == btnBack.getId()){

        }else{

        }
    }

    void Initialize(View view){
        btnBack = view.findViewById(R.id.btnBackMain);
        layoutAddNewBankAccount = view.findViewById(R.id.layoutAddNewBankAccount);
    }

}
