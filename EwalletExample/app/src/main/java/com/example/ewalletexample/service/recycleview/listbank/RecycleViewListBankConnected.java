package com.example.ewalletexample.service.recycleview.listbank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ewalletexample.data.BankInfo;

import java.util.List;

import com.example.ewalletexample.R;
import com.example.ewalletexample.Symbol.BankSupport;
import com.example.ewalletexample.service.UserSelectFunction;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleViewListBankConnected extends RecyclerView.Adapter<RecycleViewListBankConnected.ListBankConnectViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<BankInfo> bankInfoList;
    private FirebaseStorageHandler storageHandler;
    private UserSelectFunction function;
    private UserSelectBankConnect selectBankConnect;

    public RecycleViewListBankConnected(Context context, FirebaseStorageHandler storageHandler, UserSelectFunction function, List<BankInfo> bankInfos){
        this.context = context;
        this.bankInfoList = bankInfos;
        this.storageHandler = storageHandler;
        this.function = function;
        inflater = LayoutInflater.from(context);
    }

    public RecycleViewListBankConnected(Context context, FirebaseStorageHandler storageHandler, UserSelectBankConnect selectBankConnect, List<BankInfo> bankInfos){
        this.context = context;
        this.bankInfoList = bankInfos;
        this.storageHandler = storageHandler;
        this.selectBankConnect = selectBankConnect;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ListBankConnectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListBankConnectViewHolder holder = new ListBankConnectViewHolder(inflater.inflate(R.layout.activity_bank_card, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListBankConnectViewHolder holder, int position) {
        BankInfo info = bankInfoList.get(position);
        BankSupport support = BankSupport.FindBankSupport(info.getBankCode());
        holder.SetBankInfo(info);
        holder.SetBankImage(support.getBankLinkImage());
        holder.SetClickEvent(info);
    }

    @Override
    public int getItemCount() {
        return bankInfoList.size();
    }

    public class ListBankConnectViewHolder extends RecyclerView.ViewHolder{
        TextView tvCardName, tvF6Card, tvL4Card;
        ImageView imgBank;
        View itemView;

        public ListBankConnectViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvCardName = itemView.findViewById(R.id.tvcardName);
            tvF6Card = itemView.findViewById(R.id.tvF6CardNo);
            tvL4Card = itemView.findViewById(R.id.tvL4CardNo);
            imgBank = itemView.findViewById(R.id.imgBank);
        }

        public void SetBankInfo(BankInfo bankInfo){
            tvCardName.setText(bankInfo.getCardName());
            tvF6Card.setText(bankInfo.getF6CardNo());
            tvL4Card.setText(bankInfo.getL4CardNo());
        }

        public void SetBankImage(String link){
            storageHandler.LoadAccountImageFromLink(link, imgBank);
        }

        public void SetClickEvent(BankInfo info){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(function != null){
                        function.SelectModel(info);
                    } else if(selectBankConnect != null){
                        selectBankConnect.SelectBankInfo(info, ListBankConnectViewHolder.this);
                    }
                }
            });
        }

        public void SetBackgroundColor(int colorId){
            itemView.setBackgroundColor(colorId);
        }
    }
}
