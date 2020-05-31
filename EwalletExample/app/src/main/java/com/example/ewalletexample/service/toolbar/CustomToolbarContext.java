package com.example.ewalletexample.service.toolbar;


import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ewalletexample.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CustomToolbarContext {
    private Toolbar toolbar;
    private TextView tvTitle;
    private ImageButton btnBack;
    private Activity context;
    private ToolbarEvent event;

    public CustomToolbarContext(Activity context, String title, ToolbarEvent event){
        this.context = context;
        this.toolbar = context.findViewById(R.id.toolbarLayout);
        this.tvTitle = context.findViewById(R.id.tvToolbarTitle);
        this.btnBack = context.findViewById(R.id.btnBackToPreviousActivity);
        this.tvTitle.setText(title);
        this.event = event;
        ((AppCompatActivity)this.context).setSupportActionBar(toolbar);

        this.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.BackToPreviousActivity();
            }
        });
    }


    public CustomToolbarContext(Activity context, ToolbarEvent event){
        this.context = context;
        this.toolbar = context.findViewById(R.id.toolbarLayout);
        this.tvTitle = context.findViewById(R.id.tvToolbarTitle);
        this.btnBack = context.findViewById(R.id.btnBackToPreviousActivity);
        this.event = event;
        ((AppCompatActivity)this.context).setSupportActionBar(toolbar);

        this.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.BackToPreviousActivity();
            }
        });
    }

    public void SetTitle(String title){
        tvTitle.setText(title);
    }

    public void SetVisibilityImageButtonBack(int isVisibility){
        btnBack.setVisibility(isVisibility);
    }
}
