package com.example.ewalletexample.service.recycleview.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ewalletexample.R;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.service.UserSelectFunction;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleViewService extends RecyclerView.Adapter<RecycleViewService.ServiceViewHolder>{
    private Context context;
    private LayoutInflater inflater;
    private List<Service> services;
    private UserSelectFunction function;
    private boolean isUpperCase;

    public RecycleViewService(Context context, UserSelectFunction function, List<Service> services, boolean isUpperCase){
        this.context = context;
        this.services = services;
        this.function = function;
        this.isUpperCase = isUpperCase;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ServiceViewHolder holder = new ServiceViewHolder(inflater.inflate(R.layout.serivce_vertical_layout, parent, false), function);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = services.get(position);
        holder.SetService(service);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public class ServiceViewHolder extends RecyclerView.ViewHolder{
        TextView tvServiceName;
        ImageView imgService;
        View itemView;
        UserSelectFunction function;

        public ServiceViewHolder(@NonNull View itemView, UserSelectFunction function) {
            super(itemView);
            this.itemView = itemView;
            this.function = function;
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            imgService = itemView.findViewById(R.id.imgService);
        }

        public void SetService(Service service){
            if (isUpperCase){
                tvServiceName.setText(service.GetName().toUpperCase());
            } else {
                tvServiceName.setText(service.GetName());
            }
            imgService.setImageResource(service.GetImageId());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    function.SelectModel(service);
                }
            });
        }
    }
}
