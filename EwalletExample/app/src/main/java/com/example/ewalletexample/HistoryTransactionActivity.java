package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ewalletexample.Server.api.bank.BankMappingCallback;
import com.example.ewalletexample.Server.api.transaction.BankInfo;
import com.example.ewalletexample.Server.api.transaction.ExchangeInfo;
import com.example.ewalletexample.Server.api.transaction.MobileCardInfo;
import com.example.ewalletexample.Server.api.transaction.TransactionHistoryAPI;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.TransactionHistory;
import com.example.ewalletexample.service.UserSelectFunction;
import com.example.ewalletexample.service.mobilecard.MobileCardOperator;
import com.example.ewalletexample.service.recycleview.listitem.TransactionDetailAdapter;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.toolbar.CustomToolbarContext;
import com.example.ewalletexample.service.toolbar.ToolbarEvent;
import com.example.ewalletexample.utilies.HandleDateTime;
import com.example.ewalletexample.utilies.Utilies;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class HistoryTransactionActivity extends AppCompatActivity implements BankMappingCallback<List<TransactionHistory>>,
        DatePickerDialog.OnDateSetListener , ToolbarEvent, UserSelectFunction<TransactionHistory> {

    RecyclerView rvTransactionDetail;
    String userid, currentDate, fullName;
    FirebaseStorageHandler storageHandler;
    CustomToolbarContext customToolbarContext;
    TransactionHistoryAPI history;
    TextView tvFullTransaction, tvLoadContinueTransaction, tvStartDateSearchTransaction;
    ProgressBar progressBar;
    List<TransactionHistory> transactionDetailList;
    Button btnSearchTransaction;
    Gson gson;
    DatePickerDialog datePickerDialog;
    TransactionDetailAdapter transactionDetailAdapter;
    int pageSize = 2;
    boolean isLoadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_transaction);

        GetValueFromIntent();
        Initialize();
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        fullName = intent.getStringExtra(Symbol.FULLNAME.GetValue());
        history = new TransactionHistoryAPI(this, userid, 0, 10);
    }

    void Initialize(){
        isLoadMore = false;
        storageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        transactionDetailList = new ArrayList<>();
        gson = new Gson();
        progressBar = findViewById(R.id.progressBar);
        tvFullTransaction = findViewById(R.id.tvFullTransaction);
        tvLoadContinueTransaction = findViewById(R.id.tvLoadContinueTransaction);
        rvTransactionDetail = findViewById(R.id.transactionDetail);
        btnSearchTransaction = findViewById(R.id.btnSearchTransaction);
        tvStartDateSearchTransaction = findViewById(R.id.tvStartDateSearchTransaction);

        customToolbarContext = new CustomToolbarContext(this, this);
        customToolbarContext.SetTitle("Lịch sử giao dịch");
        String currentDate = HandleDateTime.GetStringCurrentDay();
        String[] date = currentDate.split("-");
        datePickerDialog = new DatePickerDialog(this,this, Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]));

        rvTransactionDetail.setHasFixedSize(true);
        rvTransactionDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        btnSearchTransaction.setEnabled(false);
        tvLoadContinueTransaction.setVisibility(View.GONE);
        tvFullTransaction.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    public void ShowDatePickerDialog(View view){
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String day = dayOfMonth + "";
        if(dayOfMonth < 10){
            day = "0" + day;
        }
        String monthInYear = (month+1) + "";
        if(month+1 < 10){
            monthInYear = "0" + monthInYear;
        }
        tvStartDateSearchTransaction.setText(day + "-" + monthInYear + "-" + year);
        btnSearchTransaction.setEnabled(true);
    }

    public void SearchTransactionByCurrentDate(View view) throws ParseException {
        isLoadMore = false;
        String currentSearchDay = tvStartDateSearchTransaction.getText().toString();
        tvFullTransaction.setVisibility(View.GONE);
        tvLoadContinueTransaction.setVisibility(View.GONE);
        if (currentSearchDay.isEmpty()){
            tvStartDateSearchTransaction.setError("Chưa nhập ngày");
            return;
        }

        long timeMilisecond = HandleDateTime.GetLongByString(HandleDateTime.GetNextDayByString(currentSearchDay));
        if (timeMilisecond == 0){
            tvStartDateSearchTransaction.setError("Lỗi format ngày");
            return;
        }
        transactionDetailList.clear();

        history.SetStartTime(timeMilisecond);
        history.SetPageSize(pageSize);
        history.StartGetHistoryTransaction();
        progressBar.setVisibility(View.VISIBLE);
    }

    public void LoadMoreTransaction(View view){
        isLoadMore = true;
        tvLoadContinueTransaction.setVisibility(View.GONE);
        history.SetStartTime(Long.valueOf(currentDate));
        history.SetPageSize(pageSize + 1);
        history.StartGetHistoryTransaction();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void MappingResponse(boolean response, List<TransactionHistory> callback)  {
        progressBar.setVisibility(View.GONE);
        if (response){
            if (isLoadMore){
                callback.remove(0);
            }
            if (callback.size() == 0){
                tvFullTransaction.setVisibility(View.VISIBLE);
                tvLoadContinueTransaction.setVisibility(View.GONE);
                return;
            }
            tvLoadContinueTransaction.setVisibility(View.VISIBLE);
            transactionDetailList.addAll(callback);
            currentDate = callback.get(callback.size() - 1).getTimemilliseconds();
            transactionDetailAdapter = new TransactionDetailAdapter(this, transactionDetailList, fullName, gson, this);
            rvTransactionDetail.setAdapter(transactionDetailAdapter);
        }
    }

    @Override
    public void BackToPreviousActivity() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void SelectModel(TransactionHistory model) {
        Intent intent = new Intent(HistoryTransactionActivity.this, TransactionDetailActivity.class);
        intent.putExtra(Symbol.STYLE_TRANSACTION_DETAIL.GetValue(), Symbol.REVIEW.GetValue());
        intent.putExtra(Symbol.TRANSACTION_DETAIL.GetValue(), gson.toJson(model));
        startActivityForResult(intent, RequestCode.VIEW_TRANSACTION);
    }
}