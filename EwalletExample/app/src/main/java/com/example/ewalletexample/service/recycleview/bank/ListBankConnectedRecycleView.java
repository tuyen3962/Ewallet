package com.example.ewalletexample.service.recycleview.bank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ewalletexample.R;
import com.example.ewalletexample.Symbol.BankSupport;
import com.example.ewalletexample.UserBankCardActivity;
import com.example.ewalletexample.data.BankInfo;
import com.example.ewalletexample.service.UserSelectFunction;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListBankConnectedRecycleView extends RecyclerView.Adapter<ListBankConnectedRecycleView.BankConnectedViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<BankInfo> bankInfoList;
    private FirebaseStorageHandler storageHandler;
    private UserSelectFunction function;

    public ListBankConnectedRecycleView(Context context, List<BankInfo> bankInfos, FirebaseStorageHandler storageHandler, UserSelectFunction function){
        this.context = context;
        inflater = LayoutInflater.from(context);
        bankInfoList = bankInfos;
        this.storageHandler = storageHandler;
        this.function = function;
    }

    class BankConnectedViewHolder extends RecyclerView.ViewHolder{
        TextView tvCardName;
        TextView tvF6Card;
        TextView tvL4Card;
        ImageView imgBank;
        View layout;

        public BankConnectedViewHolder(View view){
            super(view);
            tvCardName = view.findViewById(R.id.tvcardName);
            tvF6Card = view.findViewById(R.id.tvF6CardNo);
            tvL4Card = view.findViewById(R.id.tvL4CardNo);
            imgBank = view.findViewById(R.id.imgBank);
            layout = view.findViewById(R.id.bankConnectLayout);
        }

        public void SetCardName(String name){
            tvCardName.setText(name);
        }

        public void SetF6CardNo(String f6Cardno){
            tvF6Card.setText(f6Cardno);
        }

        public void SetL4CardNo(String l4Cardno){
            tvL4Card.setText(l4Cardno);
        }

        public void LoadBankImage(FirebaseStorageHandler storageHandler, String imgBankLink){
            storageHandler.LoadAccountImageFromLink(imgBankLink, imgBank);
        }

        public void SetClickEvent(BankInfo info){
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    function.SelectModel(info);
                }
            });
        }
    }

    @NonNull
    @Override
    public ListBankConnectedRecycleView.BankConnectedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_bank_card, parent, false);
        BankConnectedViewHolder holder = new BankConnectedViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BankConnectedViewHolder holder, int position) {
        BankInfo info = bankInfoList.get(position);
        BankSupport support = BankSupport.FindBankSupport(info.getBankCode());
        holder.SetCardName(info.getCardName());
        holder.SetF6CardNo(info.getF6CardNo());
        holder.SetL4CardNo(info.getL4CardNo());
        holder.LoadBankImage(storageHandler, support.getBankLinkImage());
        holder.SetClickEvent(info);
    }

    @Override
    public int getItemCount() {
        return bankInfoList.size();
    }
}

