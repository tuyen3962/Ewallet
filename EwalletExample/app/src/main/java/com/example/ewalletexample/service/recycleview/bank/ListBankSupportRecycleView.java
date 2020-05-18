package com.example.ewalletexample.service.recycleview.bank;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ewalletexample.R;
import com.example.ewalletexample.Symbol.BankSupport;
import com.example.ewalletexample.service.UserSelectFunction;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListBankSupportRecycleView extends RecyclerView.Adapter<ListBankSupportRecycleView.BankSupportViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private BankSupport[] bankSupportList;
    private FirebaseStorageHandler storageHandler;
    private UserSelectFunction function;

    public ListBankSupportRecycleView(Context context, FirebaseStorageHandler storageHandler, UserSelectFunction function, BankSupport[] supportList){
        this.context = context;
        this.storageHandler = storageHandler;
        this.function = function;
        this.inflater = LayoutInflater.from(context);
        this.bankSupportList = supportList;
    }

    @NonNull
    @Override
    public BankSupportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.bank_support_item, parent, false);
        BankSupportViewHolder holder = new BankSupportViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BankSupportViewHolder holder, int position) {
        BankSupport support = bankSupportList[position];
        holder.SetBankText(support.getBankName());
        holder.LoadImageFromFirebase(storageHandler, support.getBankLinkImage());
        holder.SetClickEvent(support);
    }

    @Override
    public int getItemCount() {
        return bankSupportList.length;
    }

    class BankSupportViewHolder extends RecyclerView.ViewHolder{
        View view;
        ImageView bankImg;
        TextView tvBankName;

        public BankSupportViewHolder(View view){
            super(view);
            this.view = view;
            bankImg = view.findViewById(R.id.imgBank);
            tvBankName = view.findViewById(R.id.tvBankName);
        }

        public void SetBankText(String text){
            tvBankName.setText(text);
        }

        public void LoadImageFromFirebase(FirebaseStorageHandler firebaseStorageHandler, String imageLink){
            firebaseStorageHandler.LoadAccountImageFromLink(imageLink, bankImg);
        }

        public void SetClickEvent(final BankSupport bankSupport){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    function.SelectModel(bankSupport);
                }
            });
        }
    }
}
