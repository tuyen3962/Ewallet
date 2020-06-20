package com.example.ewalletexample.service.recycleview.listitem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ewalletexample.R;
import com.example.ewalletexample.Server.api.transaction.model.BankInfo;
import com.example.ewalletexample.Server.api.transaction.model.ExchangeInfo;
import com.example.ewalletexample.Server.api.transaction.model.MobileCardInfo;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.data.TransactionHistory;
import com.example.ewalletexample.service.UserSelectFunction;
import com.example.ewalletexample.service.mobilecard.MobileCardOperator;
import com.example.ewalletexample.utilies.HandleDateTime;
import com.example.ewalletexample.utilies.Utilies;
import com.google.gson.Gson;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionDetailAdapter extends RecyclerView.Adapter<TransactionDetailAdapter.TransactionDetailViewHolder>{
    private Context context;
    private LayoutInflater mInflater;
    private List<TransactionHistory> transactionDetailList;
    private Gson gson;
    private String fullName;
    private UserSelectFunction function;

    public TransactionDetailAdapter(Context context, List<TransactionHistory> transactionDetails, String fullName, Gson gson, UserSelectFunction function){
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.transactionDetailList = transactionDetails;
        this.gson = gson;
        this.fullName = fullName;
        this.function = function;
    }

    @NonNull
    @Override
    public TransactionDetailAdapter.TransactionDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.transaction_detail_layout, parent, false);

        TransactionDetailViewHolder holder = new TransactionDetailViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionDetailViewHolder holder, int position) {
        TransactionHistory detail = transactionDetailList.get(position);
        holder.SetTransactionInfo(detail);
    }

    @Override
    public long getItemId(int position) {
        return transactionDetailList.get(position).getOrderid();
    }

    @Override
    public int getItemCount() {
        return transactionDetailList.size();
    }

    public class TransactionDetailViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView tvTitle, tvTime, tvMoney, tvStatus;
        private ImageView imgService;

        public TransactionDetailViewHolder(View view) {
            super(view);
            this.view = view;
            tvMoney = view.findViewById(R.id.tvMoney);
            tvTime = view.findViewById(R.id.tvTime);
            tvTitle = view.findViewById(R.id.tvTitle);
            imgService = view.findViewById(R.id.imgService);
            tvStatus = view.findViewById(R.id.tvStatus);
        }

        public void SetTransactionInfo(TransactionHistory history){
            tvMoney.setText(Utilies.HandleBalanceTextView(String.valueOf(history.getAmount())));
            tvTime.setText(HandleDateTime.FormatStringMillisecondIntoDate(history.getTimemilliseconds()));
            SetStatusText(history.getStatus());
            SetTitle(history.getServicetype(), history.getTxndetail());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    function.SelectModel(history);
                }
            });
        }

        private void SetTitle(int serviceType, String txtDetail){
            Service service = Service.Find(serviceType);
            imgService.setImageResource(service.GetImageId());
            if (service == Service.TOPUP_SERVICE_TYPE || service == Service.WITHDRAW_SERVICE_TYPE){
                BankInfo bankInfo = gson.fromJson(txtDetail, BankInfo.class);
                if (service == Service.TOPUP_SERVICE_TYPE){
                    tvTitle.setText("Nạp tiền vào ví từ ngân hàng " + bankInfo.getBankName());
                } else {
                    tvTitle.setText("Rút tiền về ngân hàng " + bankInfo.getBankName());
                }
            } else if (service == Service.EXCHANGE_SERVICE_TYPE){
                ExchangeInfo exchangeInfo = gson.fromJson(txtDetail, ExchangeInfo.class);
                if (exchangeInfo.getSenderName().equalsIgnoreCase(fullName)){
                    tvTitle.setText("Chuyển tiền đến " + exchangeInfo.getReceiverName());
                } else {
                    tvTitle.setText("Nhận tiền từ " + exchangeInfo.getReceiverName());
                }
            } else {
                MobileCardInfo info = gson.fromJson(txtDetail, MobileCardInfo.class);
                MobileCardOperator operator = MobileCardOperator.FindOperator(info.getCardType());
                tvTitle.setText("Mua thẻ cào " + operator.GetMobileCardName());
            }
        }

        private void SetStatusText(int status){
            if (status == 1){
                tvStatus.setText("Thành công");
            } else if (status < 1){
                tvStatus.setText("Thất bại");
            } else {
                tvStatus.setText("Đang xử lý");
            }
        }
    }
}