package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ewalletexample.R;
import com.example.ewalletexample.Server.GetBalanceServer;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

public class MainActivity extends AppCompatActivity {

    String userid;
    ImageView imgAccount;
    TextView tvBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Initialize();

        LoadDataFromIntent();
    }

    void Initialize(){
        tvBalance = findViewById(R.id.tvBalance);
        imgAccount = findViewById(R.id.imgAccount);
    }

    private void LoadDataFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        long amount = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
        UpdateUserBalance(amount);
    }

    private void UpdateUserBalance(long amount){
        tvBalance.setText("$" + (int) amount);
    }
}
