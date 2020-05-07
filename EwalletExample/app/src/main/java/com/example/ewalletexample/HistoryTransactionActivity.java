package com.example.ewalletexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ewalletexample.Server.api.bank.BankMappingCallback;
import com.example.ewalletexample.Server.api.transaction.TransactionHistory;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.TransactionDetail;
import com.example.ewalletexample.model.StatisMonthTransaction;
import com.example.ewalletexample.model.Statistic;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.ResponseModelByKey;
import com.example.ewalletexample.service.transaction.MonthTransaction;
import com.example.ewalletexample.utilies.Date;
import com.example.ewalletexample.utilies.HandleDateTime;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HistoryTransactionActivity extends AppCompatActivity implements BankMappingCallback<List<TransactionDetail>>, ResponseModelByKey<StatisMonthTransaction> {
    FirebaseDatabaseHandler<StatisMonthTransaction> firebaseDatabaseHandler;

    RecyclerView rvMonthTransaction, rvTransactionDetail;

    String userid, userPhone;
    long userAmount;
    int pageSize = 10;
    TransactionHistory history;
    StatisMonthTransaction statisMonthTransaction;

    List<TransactionDetail> transactionDetailList;
    List<MonthTransaction> monthTransactionsList;

    StatisticMonthTransactionAdapter.StatisticMonthTransactionViewHolder currentMonthTransactionHolder;
    StatisticMonthTransactionAdapter statisticMonthTransactionAdapter;
    Date currentDateChoose;

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
        userPhone = intent.getStringExtra(Symbol.PHONE.GetValue());
        userAmount = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
        history = new TransactionHistory(this, userid, 0, 10);
    }

    void Initialize(){
        transactionDetailList = new ArrayList<>();

        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference());
        firebaseDatabaseHandler.GetModelByKey(Symbol.CHILD_NAME_TRANSACTION, userid, StatisMonthTransaction.class, this);

        rvMonthTransaction = findViewById(R.id.monthTransaction);
        rvTransactionDetail = findViewById(R.id.transactionDetail);
    }

    @Override
    public void MappingResponse(boolean response, List<TransactionDetail> callback)  {
        if (response){
            transactionDetailList.clear();
            transactionDetailList.addAll(callback);
            transactionDetailAdapter = new TransactionDetailAdapter(this, transactionDetailList);
            rvTransactionDetail.setHasFixedSize(true);
            rvTransactionDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rvTransactionDetail.setAdapter(transactionDetailAdapter);
        }
    }

    void SetChangeCurrentDate(Date changeDate, StatisticMonthTransactionAdapter.StatisticMonthTransactionViewHolder holder){
        if(currentDateChoose != null && currentDateChoose.isSameByMonth(changeDate)){
            return;
        }

        if (currentMonthTransactionHolder != null){
            currentMonthTransactionHolder.HideLineChoose();
        }

        currentMonthTransactionHolder = holder;
        currentMonthTransactionHolder.ShowLineChoose();
        currentDateChoose = changeDate;

        GetTransactionDetailByCurrentDate();
    }

    void GetTransactionDetailByCurrentDate(){
        String currentDateString = currentDateChoose.getDayByFormat("-");
        long currentTime = HandleDateTime.GetLongByString(currentDateString);
        history.SetStartTime(currentTime);
        SetPageSizeAndStart();
    }

    void SetPageSizeAndStart(){
        history.SetPageSize(pageSize);
        history.StartGetHistoryTransaction();
    }

    @Override
    public void GetModel(StatisMonthTransaction data) {
        if (data != null){
            this.statisMonthTransaction = data;
            SetLayoutMonthTransaction();
        }
    }

    void SetLayoutMonthTransaction(){
        rvMonthTransaction.setHasFixedSize(true);
        rvMonthTransaction.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        statisticMonthTransactionAdapter = new StatisticMonthTransactionAdapter(this, statisMonthTransaction);
        rvMonthTransaction.setAdapter(statisticMonthTransactionAdapter);
    }

    public class StatisticMonthTransactionAdapter extends RecyclerView.Adapter<StatisticMonthTransactionAdapter.StatisticMonthTransactionViewHolder> {
        private Context context;
        private LayoutInflater mInflater;
        private StatisMonthTransaction statisMonthTransaction;
        private List<String> listDates;

        public StatisticMonthTransactionAdapter(Context context, StatisMonthTransaction statisMonthTransaction){
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.statisMonthTransaction = statisMonthTransaction;
            this.listDates = this.statisMonthTransaction.listDay();
        }

        @NonNull
        @Override
        public StatisticMonthTransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.transaction_history_title_layout, parent, false);

            StatisticMonthTransactionViewHolder holder = new StatisticMonthTransactionViewHolder(view, context);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull StatisticMonthTransactionViewHolder viewHolder, int position) {
            String currentDate = listDates.get(position);
            String[] split = currentDate.split("-");
            Date currentDateTime = new Date(split[2], split[1], split[0]);
            Statistic statistic = statisMonthTransaction.getStatisByDay(currentDate);
            long totalCost = statistic.getTotalCost();
            if(totalCost > 0){
                viewHolder.SetTextMonthTransactionTitle("Tổng thu " + currentDate);
            }else {
                viewHolder.SetTextMonthTransactionTitle("Tổng chi " + currentDate);
            }
            viewHolder.SetTextMoney(String.valueOf(statistic.getTotalCost()));
            viewHolder.SetOnClickEvent(currentDateTime, viewHolder);
            if(position == 0){
                SetChangeCurrentDate(currentDateTime, viewHolder);
                viewHolder.ShowLineChoose();
            }
            else {
                viewHolder.HideLineChoose();
            }
        }

        @Override
        public long getItemId(int position) {
            return HandleDateTime.GetLongByString(listDates.get(position));
        }

        @Override
        public int getItemCount() {
            return listDates.size();
        }

        public class StatisticMonthTransactionViewHolder extends RecyclerView.ViewHolder {
            private View lineChoose, layoutMonthTransaction;
            private TextView tvMonthTransaction, tvMoney;
            private Context context;

            public StatisticMonthTransactionViewHolder(View view, Context context) {
                super(view);
                this.context = context;
                tvMonthTransaction = view.findViewById(R.id.tvMonthTransaction);
                tvMoney = view.findViewById(R.id.tvMoney);
                lineChoose = view.findViewById(R.id.lineChoose);
                layoutMonthTransaction = view.findViewById(R.id.layoutMonthTransaction);
            }

            public void SetTextMoney(String money){
                tvMoney.setText(money);
            }

            public void SetTextMonthTransactionTitle(String title){
                tvMonthTransaction.setText(title);
            }

            public void ShowLineChoose(){
                lineChoose.setVisibility(View.VISIBLE);
            }

            public void HideLineChoose(){
                lineChoose.setVisibility(View.GONE);
            }

            public void SetOnClickEvent(final Date date, final StatisticMonthTransactionViewHolder holder){
                layoutMonthTransaction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SetChangeCurrentDate(date, holder);
                    }
                });
            }
        }
    }

    public class TransactionDetailAdapter extends RecyclerView.Adapter<TransactionDetailAdapter.TransactionDetailViewHolder>{
        private Context context;
        private LayoutInflater mInflater;
        private List<TransactionDetail> transactionDetailList;

        public TransactionDetailAdapter(Context context, List<TransactionDetail> transactionDetails){
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
        public void onBindViewHolder(@NonNull TransactionDetailViewHolder viewHolder, int position) {
            TransactionDetail detail = transactionDetailList.get(position);
            Log.d("TAG", "onBindViewHolder: " + detail.getAmount() + " " + detail.getTitle() + " " + detail.getChargetime().getDayMonthTime());
            viewHolder.SetMoney(detail.getAmount()+"");
            viewHolder.SetTime(detail.getChargetime().getDayMonthTime());
            viewHolder.SetTitle(detail.getTitle());
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
            private View layoutMonthTransaction;
            private TextView tvTitle, tvTime, tvMoney;
            private Context context;

            public TransactionDetailViewHolder(View view, Context context) {
                super(view);
                this.context = context;
                tvMoney = view.findViewById(R.id.tvMoney);
                tvTime = view.findViewById(R.id.tvTime);
                tvTitle = view.findViewById(R.id.tvTitle);
            }

            public void SetMoney(String text){
                tvMoney.setText(text);
            }

            public void SetTitle(String title){
                tvTitle.setText(title);
            }

            public void SetTime(String time){
                tvTime.setText(time);
            }
        }
    }
}