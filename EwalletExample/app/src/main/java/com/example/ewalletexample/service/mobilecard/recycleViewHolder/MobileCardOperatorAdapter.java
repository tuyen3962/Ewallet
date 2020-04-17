package com.example.ewalletexample.service.mobilecard.recycleViewHolder;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ewalletexample.R;
import com.example.ewalletexample.service.mobilecard.MobileCardOperator;
import com.example.ewalletexample.service.mobilecard.SelectMobileCardFunction;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.google.firebase.storage.FirebaseStorage;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class MobileCardOperatorAdapter extends RecyclerView.Adapter<MobileCardOperatorAdapter.MobileCardOperatorViewHolder> {
    private final int width = 4;
    private final int colorDefault = R.color.Grey;

    FirebaseStorageHandler firebaseStorageHandler;
    private MobileCardOperator[] arrMobileCardNetwork;
    private Context context;
    private SelectMobileCardFunction function;
    LayoutInflater mInflater;

    public MobileCardOperatorAdapter(MobileCardOperator[] arrMobileCardNetwork, Context context, SelectMobileCardFunction function) {
        this.arrMobileCardNetwork = arrMobileCardNetwork;
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.function = function;
        firebaseStorageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), context);
    }

    @Override
    public MobileCardOperatorAdapter.MobileCardOperatorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.mobile_card_operator, parent, false);

        MobileCardOperatorViewHolder mobileCardOperatorViewHolder = new MobileCardOperatorViewHolder(view, function, context, firebaseStorageHandler);
        return mobileCardOperatorViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(MobileCardOperatorViewHolder holder, int position) {
        holder.SetImageMobileCardOperator(arrMobileCardNetwork[position].GetMobileLinkImage());
        holder.SetBackgroundLayout(colorDefault);
        holder.MobileCardOperatorSelected(holder, arrMobileCardNetwork[position]);
        if(position == 0){
            function.SelectMobileCardOperator(holder, arrMobileCardNetwork[position]);
        }
    }

    @Override
    public int getItemCount() {
        return arrMobileCardNetwork.length;
    }

    public class MobileCardOperatorViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgMobileCardOperator;
        private View layoutMobileOperator;
        private TextView tvName;
        private SelectMobileCardFunction selectMobileCardFunction;
        private GradientDrawable backgroundDrawable;
        private Context context;
        private FirebaseStorageHandler storageHandler;

        public MobileCardOperatorViewHolder(View view, SelectMobileCardFunction selectMobileCardFunction, Context context, FirebaseStorageHandler storageHandler) {
            super(view);
            this.context = context;
            this.storageHandler = storageHandler;
            this.selectMobileCardFunction = selectMobileCardFunction;
            imgMobileCardOperator = view.findViewById(R.id.imgMobileOperator);
            layoutMobileOperator = view.findViewById(R.id.layoutMobileOperator);
            tvName = view.findViewById(R.id.tvName);
            backgroundDrawable = (GradientDrawable)layoutMobileOperator.getBackground();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void SetBackgroundLayout(int colorId){
            backgroundDrawable.setStroke(width, context.getColor(colorId));
        }

        public void SetImageMobileCardOperator(String link){
            storageHandler.LoadAccountImageFromLink(link, imgMobileCardOperator);
        }

        public void MobileCardOperatorSelected(final MobileCardOperatorAdapter.MobileCardOperatorViewHolder holder, final MobileCardOperator mobileCardOperator){
            layoutMobileOperator.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    selectMobileCardFunction.SelectMobileCardOperator(holder, mobileCardOperator);
                }
            });
        }
    }
}