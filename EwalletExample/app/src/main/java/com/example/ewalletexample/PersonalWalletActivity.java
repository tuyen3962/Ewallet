package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ewalletexample.service.BalanceVisible;

public class PersonalWalletActivity extends AppCompatActivity {

    Button btnVisibilityBalance;
    TextView tvFullName, tvPhone, tvEmail, tvBalance;
    ImageView imgAccount;

    LinearLayout layoutAccount, layoutBankAccount, layoutSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_wallet);

        Initialize();

        SetBalanceText("123");

        btnVisibilityBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BalanceVisible.SwitchBalanceVisible();
                SetBalanceText("123");
            }
        });
    }

    void Initialize(){
        btnVisibilityBalance = findViewById(R.id.btnVisibilyBalance);
        tvEmail = findViewById(R.id.tvEmail);
        tvFullName = findViewById(R.id.tvFullName);
        tvPhone = findViewById(R.id.tvPhone);
        tvBalance = findViewById(R.id.tvBalance);

        imgAccount = findViewById(R.id.imgAccount);

        layoutAccount = findViewById(R.id.layoutUserAccount);
        layoutBankAccount = findViewById(R.id.layoutBankAccount);
        layoutSetting = findViewById(R.id.layoutSetting);
    }

    void SetBalanceText(String amount){
        if(BalanceVisible.IsBalanceVisible()){
            tvBalance.setText(amount);
        }
        else
        {
            tvBalance.setText("*******");
        }
    }
}
