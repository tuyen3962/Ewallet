package com.example.ewalletexample.service.recycleview.listitem;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ewalletexample.R;
import com.example.ewalletexample.service.UserSelectFunction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleViewListSingleItem extends RecyclerView.Adapter<RecycleViewListSingleItem.SingleItemViewHolder> {
    private Context context;
    private String[] values;
    private UserSelectFunction function;
    private LayoutInflater inflater;

    public RecycleViewListSingleItem(@NonNull Context context, String[] values, UserSelectFunction function) {
        this.context = context;
        this.values = values;
        this.function = function;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecycleViewListSingleItem.SingleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SingleItemViewHolder singleItemViewHolder = new SingleItemViewHolder(inflater.inflate(R.layout.single_text_layout, parent, false));
        return singleItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewListSingleItem.SingleItemViewHolder holder, int position) {
        holder.SetText(values[position]);
    }

    @Override
    public int getItemCount() {
        return values.length;
    }

    class SingleItemViewHolder extends RecyclerView.ViewHolder{

        View itemView;
        TextView textView;

        public SingleItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.textView = itemView.findViewById(R.id.tvValue);
        }

        public void SetText(String text){
            textView.setText(text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    function.SelectModel(text);
                }
            });
        }
    }
}
