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
import de.hdodenhof.circleimageview.CircleImageView;

public class RecycleHorzontalServiceView extends RecyclerView.Adapter<RecycleHorzontalServiceView.HorizontalServiceViewHolder> {

    private Context context;
    private List<Service> services;
    private LayoutInflater inflater;
    private UserSelectFunction function;

    public RecycleHorzontalServiceView(Context context, List<Service> services, UserSelectFunction function){
        this.context = context;
        this.services = services;
        this.function = function;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecycleHorzontalServiceView.HorizontalServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HorizontalServiceViewHolder holder = new HorizontalServiceViewHolder(inflater.inflate(R.layout.service_horizontal_layout, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleHorzontalServiceView.HorizontalServiceViewHolder holder, int position) {
        holder.SetService(services.get(position));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public class HorizontalServiceViewHolder extends RecyclerView.ViewHolder{

        ImageView imgService;
        TextView tvService;
        View itemView;

        public HorizontalServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.imgService = itemView.findViewById(R.id.imgService);
            this.tvService = itemView.findViewById(R.id.tvServiceName);
        }

        public void SetService(Service service){
            imgService.setImageResource(service.GetImageId());
            tvService.setText(service.GetName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    function.SelectModel(service);
                }
            });
        }
    }
}
