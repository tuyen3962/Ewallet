package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ewalletexample.Server.api.bank.BankMappingCallback;
import com.example.ewalletexample.Server.api.transaction.BankInfo;
import com.example.ewalletexample.Server.api.transaction.ExchangeInfo;
import com.example.ewalletexample.Server.api.transaction.MobileCardInfo;
import com.example.ewalletexample.Server.api.transaction.TransactionHistoryAPI;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.TransactionDetail;
import com.example.ewalletexample.data.TransactionHistory;
import com.example.ewalletexample.model.StatisMonthTransaction;
import com.example.ewalletexample.model.Statistic;
import com.example.ewalletexample.service.mobilecard.MobileCardOperator;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.ResponseModelByKey;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.transaction.MonthTransaction;
import com.example.ewalletexample.utilies.Date;
import com.example.ewalletexample.utilies.HandleDateTime;
import com.example.ewalletexample.utilies.Utilies;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryTransactionActivity extends AppCompatActivity implements BankMappingCallback<List<TransactionHistory>>, DatePickerDialog.OnDateSetListener {

    RecyclerView rvTransactionDetail;
    String userid, currentDate, fullName;
    FirebaseStorageHandler storageHandler;

    int pageSize = 10;
    TransactionHistoryAPI history;
    TextView tvFullTransaction, tvLoadContinueTransaction, tvStartDateSearchTransaction, tvTitle;
    ProgressBar progressBar;
    List<TransactionHistory> transactionDetailList;
    Button btnSearchTransaction;
    Gson gson;
    ImageButton imgBack;
    DatePickerDialog datePickerDialog;
    TransactionDetailAdapter transactionDetailAdapter;

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
        storageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        transactionDetailList = new ArrayList<>();
        gson = new Gson();
        tvTitle = findViewById(R.id.tvTitle);
        imgBack = findViewById(R.id.btnBackToPreviousActivity);
        progressBar = findViewById(R.id.progressBar);
        tvFullTransaction = findViewById(R.id.tvFullTransaction);
        tvLoadContinueTransaction = findViewById(R.id.tvLoadContinueTransaction);
        rvTransactionDetail = findViewById(R.id.transactionDetail);
        btnSearchTransaction = findViewById(R.id.btnSearchTransaction);
        tvStartDateSearchTransaction = findViewById(R.id.tvStartDateSearchTransaction);

        datePickerDialog = new DatePickerDialog(this,this,1998,1,1);

        rvTransactionDetail.setHasFixedSize(true);
        rvTransactionDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        btnSearchTransaction.setEnabled(false);
        tvLoadContinueTransaction.setVisibility(View.GONE);
        tvFullTransaction.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        tvTitle.setText("Lịch sử giao dịch");
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        String currentSearchDay = tvStartDateSearchTransaction.getText().toString();
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
        long timeMilisecond = HandleDateTime.GetLongByStringTimeDate(currentDate);
        if (timeMilisecond == 0){
            tvStartDateSearchTransaction.setError("Lỗi format ngày");
            return;
        }
        history.SetStartTime(timeMilisecond);
        history.SetPageSize(pageSize);
        history.StartGetHistoryTransaction();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void MappingResponse(boolean response, List<TransactionHistory> callback)  {
        progressBar.setVisibility(View.GONE);
        if (response){
            if (callback.size() == 0){
                tvFullTransaction.setVisibility(View.VISIBLE);
                return;
            }
            tvLoadContinueTransaction.setVisibility(View.VISIBLE);
            transactionDetailList.addAll(callback);
            currentDate = callback.get(callback.size() - 1).getTimemilliseconds();
            transactionDetailAdapter = new TransactionDetailAdapter(this, transactionDetailList);
            rvTransactionDetail.setAdapter(transactionDetailAdapter);
        }
    }

    private void SwitchToTransactionDetail(TransactionHistory history){
        Intent intent = new Intent(HistoryTransactionActivity.this, TransactionDetailActivity.class);
        intent.putExtra(Symbol.STYLE_TRANSACTION_DETAIL.GetValue(), Symbol.REVIEW.GetValue());
        intent.putExtra(Symbol.TRANSACTION_DETAIL.GetValue(), gson.toJson(history));
    }

    public class TransactionDetailAdapter extends RecyclerView.Adapter<TransactionDetailAdapter.TransactionDetailViewHolder>{
        private Context context;
        private LayoutInflater mInflater;
        private List<TransactionHistory> transactionDetailList;

        public TransactionDetailAdapter(Context context, List<TransactionHistory> transactionDetails){
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.transactionDetailList = transactionDetails;
        }

        @NonNull
        @Override
        public TransactionDetailAdapter.TransactionDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.transaction_detail_layout, parent, false);

            TransactionDetailViewHolder holder = new TransactionDetailViewHolder(view, context);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull TransactionDetailViewHolder holder, int position) {
            TransactionHistory detail = transactionDetailList.get(position);
            holder.SetTransactionInfo(detail);
        }

        @Override
        public long getItemId(int position) {
            return transactionDetailList.get(position).getOrderid();
        }

        @Override
        public int getItemCount() {
            return transactionDetailList.size();
        }

        public class TransactionDetailViewHolder extends RecyclerView.ViewHolder {
            private View view;
            private TextView tvTitle, tvTime, tvMoney;
            private CircleImageView imgService;

            public TransactionDetailViewHolder(View view, Context context) {
                super(view);
                this.view = view;
                tvMoney = view.findViewById(R.id.tvMoney);
                tvTime = view.findViewById(R.id.tvTime);
                tvTitle = view.findViewById(R.id.tvTitle);
                imgService = view.findViewById(R.id.imgService);
            }

            public void SetTransactionInfo(TransactionHistory history){
                tvMoney.setText(Utilies.HandleBalanceTextView(String.valueOf(history.getAmount())));
                tvTime.setText(history.getTimemilliseconds());
                SetTitle(history.getServicetype(), history.getTxndetail());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SwitchToTransactionDetail(history);
                    }
                });
            }

            private void SetTitle(int serviceType, String txtDetail){
                Service service = Service.Find(serviceType);
                imgService.setImageResource(service.GetImageId());
                if (service == Service.TOPUP_SERVICE_TYPE || service == Service.WITHDRAW_SERVICE_TYPE){
                    BankInfo bankInfo = gson.fromJson(txtDetail, BankInfo.class);
                    if (service == Service.TOPUP_SERVICE_TYPE){
                        tvTitle.setText("Nạp tiền vào ví từ ngân hàng " + bankInfo.getBankName());
                    } else {
                        tvTitle.setText("Rút tiền về ngân hàng " + bankInfo.getBankName());
                    }
                } else if (service == Service.EXCHANGE_SERVICE_TYPE){
                    ExchangeInfo exchangeInfo = gson.fromJson(txtDetail, ExchangeInfo.class);
                    if (exchangeInfo.getSenderName().equalsIgnoreCase(fullName)){
                        tvTitle.setText("Chuyển tiền đến " + exchangeInfo.getReceiverName());
                    } else {
                        tvTitle.setText("Nhận tiền từ " + exchangeInfo.getReceiverName());
                    }
                } else {
                    MobileCardInfo info = gson.fromJson(txtDetail, MobileCardInfo.class);
                    MobileCardOperator operator = MobileCardOperator.FindOperator(info.getCardType());
                    tvTitle.setText("Mua thẻ cào " + operator.GetMobileCardName());
                }
            }
        }
    }
}