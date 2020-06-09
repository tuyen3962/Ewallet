package com.example.ewalletexample.service.recycleview.notifycation;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ewalletexample.R;
import com.example.ewalletexample.Server.api.notification.UserNotifyEntity;
import com.example.ewalletexample.Symbol.Service;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserNotifycationEntityAdapter extends RecyclerView.Adapter<UserNotifycationEntityAdapter.UserNotifycationViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<UserNotifyEntity> entities;

    public UserNotifycationEntityAdapter(Context context, List<UserNotifyEntity> entities){
        this.entities = entities;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public UserNotifycationEntityAdapter.UserNotifycationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserNotifycationViewHolder holder = new UserNotifycationViewHolder(inflater.inflate(R.layout.transaction_detail_layout, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserNotifycationEntityAdapter.UserNotifycationViewHolder holder, int position) {
        holder.SetNotifyDetail(entities.get(position));
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    class UserNotifycationViewHolder extends RecyclerView.ViewHolder{
        ImageView imgService;
        TextView tvTitleNotify, tvChargeTimeNotify, tvStatusNotify, tvBalanceTransaction;

        public UserNotifycationViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imgService = itemView.findViewById(R.id.imgService);
            this.tvTitleNotify = itemView.findViewById(R.id.tvTitle);
            this.tvChargeTimeNotify = itemView.findViewById(R.id.tvTime);
            this.tvBalanceTransaction = itemView.findViewById(R.id.tvMoney);
            this.tvBalanceTransaction.setVisibility(View.GONE);
            this.tvStatusNotify = itemView.findViewById(R.id.tvStatus);
        }

        public void SetNotifyDetail(UserNotifyEntity entity){
            Service service = Service.Find(entity.serviceType);
            this.imgService.setImageResource(service.GetImageId());
            this.tvTitleNotify.setText(entity.title);
            this.tvChargeTimeNotify.setText(entity.time);
            this.tvStatusNotify.setText(entity.status == 1 ? "Hoàn thành" : "Thất bại");
        }
    }
}
