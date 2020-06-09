package com.example.ewalletexample.service.LoadItem;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ewalletexample.R;

public class LoadItemLayout {
    private TextView tvFullTransaction, tvLoadContinueTransaction;
    private ProgressBar progressBar;
    private LoadItemLayoutFunction function;

    public LoadItemLayout(Activity activity, LoadItemLayoutFunction function){
        this.tvFullTransaction = activity.findViewById(R.id.tvFullTransaction);
        this.tvLoadContinueTransaction = activity.findViewById(R.id.tvLoadContinueTransaction);
        this.progressBar = activity.findViewById(R.id.progressBar);
        this.function = function;

        this.tvLoadContinueTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                function.LoadContinue();
            }
        });

        HideAll();
    }

    public void HideAll(){
        this.progressBar.setVisibility(View.GONE);
        this.tvFullTransaction.setVisibility(View.GONE);
        this.tvLoadContinueTransaction.setVisibility(View.GONE);
    }

    public void ShowProgressBar(){
        this.progressBar.setVisibility(View.VISIBLE);
        this.tvFullTransaction.setVisibility(View.GONE);
        this.tvLoadContinueTransaction.setVisibility(View.GONE);
    }

    public void ShowFull(){
        this.progressBar.setVisibility(View.GONE);
        this.tvFullTransaction.setVisibility(View.VISIBLE);
        this.tvLoadContinueTransaction.setVisibility(View.GONE);
    }

    public void ShowLoad(){
        this.progressBar.setVisibility(View.GONE);
        this.tvFullTransaction.setVisibility(View.GONE);
        this.tvLoadContinueTransaction.setVisibility(View.VISIBLE);
    }
}
