package com.example.ewalletexample.service.mobilecard.recycleViewHolder;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ewalletexample.R;
import com.example.ewalletexample.service.mobilecard.MobileCardAmount;
import com.example.ewalletexample.service.mobilecard.SelectMobileCardFunction;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class MobileCardAmountAdapter extends RecyclerView.Adapter<MobileCardAmountAdapter.MobileCardAmountViewHolder> {
    private final int width = 4;
    private final int colorDefault = R.color.Grey;

    private Context context;
    private LayoutInflater mInflater;
    private List<MobileCardAmount> listAmounts;
    private SelectMobileCardFunction function;

    public MobileCardAmountAdapter(Context context, List<MobileCardAmount> listAmounts, SelectMobileCardFunction function){
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listAmounts = listAmounts;
        this.function = function;
    }

    @NonNull
    @Override
    public MobileCardAmountAdapter.MobileCardAmountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.mobile_card_amount_form, parent, false);

        MobileCardAmountViewHolder holder = new MobileCardAmountViewHolder(view, function, context);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull MobileCardAmountViewHolder viewHolder, int position) {
        viewHolder.SetAmount(listAmounts.get(position).GetAmount());
        viewHolder.SetTitle(function.GetCurrentMobileCardOperator().GetMobileCardName());
        viewHolder.MobileCardAmountSelected(viewHolder, listAmounts.get(position));
        viewHolder.SetBackgroundLayout(colorDefault);
        if(position == 0){
            function.SelectMobileCardAmount(viewHolder, listAmounts.get(position));
        }
    }

    @Override
    public long getItemId(int position) {
        return listAmounts.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return listAmounts.size();
    }

    public class MobileCardAmountViewHolder extends RecyclerView.ViewHolder {
        private View layoutMobileOperator;
        private TextView tvTitle, tvAmount;
        private SelectMobileCardFunction selectMobileCardFunction;
        private GradientDrawable backgroundDrawable;
        private Context context;

        public MobileCardAmountViewHolder(View view, SelectMobileCardFunction selectMobileCardFunction, Context context) {
            super(view);
            this.context = context;
            this.selectMobileCardFunction = selectMobileCardFunction;
            layoutMobileOperator = view.findViewById(R.id.formLayout);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvAmount = view.findViewById(R.id.tvAmount);
            backgroundDrawable = (GradientDrawable)layoutMobileOperator.getBackground();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void SetBackgroundLayout(int colorId){
            backgroundDrawable.setStroke(width, context.getColor(colorId));
        }

        public void SetTitle(String title){
            tvTitle.setText(title);
        }

        public void SetAmount(String amount){
            tvAmount.setText(amount);
        }

        public void MobileCardAmountSelected(final MobileCardAmountViewHolder holder, final MobileCardAmount mobileCardAmount){
            layoutMobileOperator.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    selectMobileCardFunction.SelectMobileCardAmount(holder, mobileCardAmount);
                }
            });
        }
    }
}