package com.example.ewalletexample;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ewalletexample.Server.api.transaction.fee.GetServiceFeeAPI;
import com.example.ewalletexample.Server.api.transaction.fee.GetServiceFeeRequest;
import com.example.ewalletexample.Server.api.transaction.fee.GetServiceFeeResponse;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.MobileCardModel;
import com.example.ewalletexample.service.mobilecard.MobileCard;
import com.example.ewalletexample.service.mobilecard.MobileCardAmount;
import com.example.ewalletexample.service.mobilecard.MobileCardOperator;
import com.example.ewalletexample.service.mobilecard.SelectMobileCardFunction;
import com.example.ewalletexample.service.mobilecard.recycleViewHolder.MobileCardAmountAdapter;
import com.example.ewalletexample.service.mobilecard.recycleViewHolder.MobileCardOperatorAdapter;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.HandleDataFromFirebaseDatabase;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.utilies.Utilies;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class SelectMobileCardFunctionActivity extends AppCompatActivity implements SelectMobileCardFunction,
        HandleDataFromFirebaseDatabase<MobileCardModel>, GetServiceFeeResponse {
    private final int colorDefault = R.color.Grey;
    private final int colorChosen = R.color.Black;

    String userid, phone, secretKeyString1, secretKeyString2;
    long userAmount;
    boolean hasChangeOperator;

    FirebaseDatabaseHandler<MobileCardModel> firebaseDatabaseHandler;
    FirebaseStorageHandler firebaseStorageHandler;

    RecyclerView  rvMobileCardOperator, layoutCardAmount;
    TextView tvQuantity;
    GetServiceFeeAPI getServiceFeeAPI;
    GetServiceFeeRequest request;
    ProgressBarManager progressBarManager;
    MobileCardOperator[] arrMobileCardNetwork;

    MobileCardOperatorAdapter mobileCardOperatorAdapter;
    MobileCardOperatorAdapter.MobileCardOperatorViewHolder currentMobileCardOperatorViewHolder;
    MobileCardOperator mobileCardOperatorChosen;

    List<MobileCardAmount> listAmounts;
    MobileCardAmountAdapter mobileCardAmountAdapter;
    MobileCardAmountAdapter.MobileCardAmountViewHolder currentMobileCardAmountViewHolder;
    MobileCardAmount currentAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mobile_card);

        Initialize();

        GetValueFromIntent();
    }

    void Initialize(){
        hasChangeOperator = false;
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), findViewById(R.id.btnVerify));
        arrMobileCardNetwork = MobileCard.GetInstance().GetMobileCardOperator();

        firebaseStorageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference(), this);
        tvQuantity = findViewById(R.id.tvQuantity);
        rvMobileCardOperator = findViewById(R.id.rvMobileCardOperator);
        mobileCardOperatorAdapter = new MobileCardOperatorAdapter(arrMobileCardNetwork, this, this);
        Utilies.InitializeRecycleView(rvMobileCardOperator,
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false),
                mobileCardOperatorAdapter);

        layoutCardAmount = findViewById(R.id.layoutCardAmount);
        listAmounts = MobileCard.GetInstance().GetListAmountsByMobileCardOperator(arrMobileCardNetwork[0]);
        mobileCardAmountAdapter = new MobileCardAmountAdapter(this, listAmounts, this);
        Utilies.InitializeRecycleView(layoutCardAmount,
                new GridLayoutManager(this, 3),
                mobileCardAmountAdapter);
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        userAmount = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
        phone = intent.getStringExtra(Symbol.PHONE.GetValue());
        secretKeyString1 = intent.getStringExtra(Symbol.SECRET_KEY_01.GetValue());
        secretKeyString2 = intent.getStringExtra(Symbol.SECRET_KEY_02.GetValue());

        request = new GetServiceFeeRequest();
        request.service_code = Service.MOBILE_CARD_SERVICE_TYPE.GetCode();
        request.key = secretKeyString1;
        request.secondKey = secretKeyString2;
        getServiceFeeAPI = new GetServiceFeeAPI(request, this::GetTransactionFee);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void SelectMobileCardOperator(MobileCardOperatorAdapter.MobileCardOperatorViewHolder mobileCardOperatorViewHolder, MobileCardOperator mobileCardOperator) {
        if(mobileCardOperatorChosen != null && mobileCardOperatorChosen == mobileCardOperator){
            return;
        }

        if (currentMobileCardOperatorViewHolder != null){
            currentMobileCardOperatorViewHolder.SetBackgroundLayout(colorDefault);
        }
        SetCurrentMobileCardOperatorViewHolder(mobileCardOperatorViewHolder);
        SetMobileCardOperatorChosen(mobileCardOperator);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void SetCurrentMobileCardOperatorViewHolder(MobileCardOperatorAdapter.MobileCardOperatorViewHolder mobileCardOperatorViewHolder) {
        currentMobileCardOperatorViewHolder = mobileCardOperatorViewHolder;
        currentMobileCardOperatorViewHolder.SetBackgroundLayout(colorChosen);
    }

    private void SetMobileCardOperatorChosen(MobileCardOperator mobileCardOperator){
        this.mobileCardOperatorChosen = mobileCardOperator;
        listAmounts = MobileCard.GetInstance().GetListAmountsByMobileCardOperator(mobileCardOperator);
        mobileCardAmountAdapter.notifyDataSetChanged();
        hasChangeOperator = true;
    }

    @Override
    public MobileCardOperator GetCurrentMobileCardOperator() {
        return mobileCardOperatorChosen;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void SelectMobileCardAmount(MobileCardAmountAdapter.MobileCardAmountViewHolder mobileCardAmountViewHolder, MobileCardAmount mobileCardAmount) {
        if(currentAmount != null && currentAmount == mobileCardAmount && !hasChangeOperator){
            return;
        }

        if (currentMobileCardAmountViewHolder != null){
            currentMobileCardAmountViewHolder.SetBackgroundLayout(colorDefault);
        }
        SetCurrentMobileCardAmountViewHolder(mobileCardAmountViewHolder);
        currentAmount = mobileCardAmount;
        if (hasChangeOperator){
            hasChangeOperator = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void SetCurrentMobileCardAmountViewHolder(MobileCardAmountAdapter.MobileCardAmountViewHolder mobileCardAmountViewHolder) {
        currentMobileCardAmountViewHolder = mobileCardAmountViewHolder;
        currentMobileCardAmountViewHolder.SetBackgroundLayout(colorChosen);
    }

    public void VerifySelection(View view){
        firebaseDatabaseHandler.RegisterDataListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void StartCreateMobileOrder(){
        getServiceFeeAPI.StartRequest(getString(R.string.public_key));
        progressBarManager.ShowProgressBar("Lấy phí giao dịch");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void HandleDataModel(MobileCardModel model) {
        if(model != null){
            if(model.IsCardAmountAvailable(currentAmount) && userAmount > Long.valueOf(currentAmount.GetAmount())){
                StartCreateMobileOrder();
                return;
            }
        }

        Toast.makeText(this, "Thẻ cào với mệnh giá hiện không còn.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void HandleDataSnapShot(DataSnapshot dataSnapshot) {
        if(dataSnapshot.child(Symbol.CHILD_NAME_CARDS_FIREBASE_DATABASE.GetValue()).hasChild(mobileCardOperatorChosen.GetMobileCode())) {
            MobileCardModel model = dataSnapshot.child(Symbol.CHILD_NAME_CARDS_FIREBASE_DATABASE.GetValue()).child(mobileCardOperatorChosen.GetMobileCode()).getValue(MobileCardModel.class);
            firebaseDatabaseHandler.UnregisterValueListener(model);
            return;
        }

        firebaseDatabaseHandler.UnregisterValueListener(null);
    }

    @Override
    public void HandlerDatabaseError(DatabaseError databaseError) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.SUBMIT_ORDER){
            if (resultCode == RESULT_OK){
                setResult(resultCode);
                finish();
            }
        }
    }

    @Override
    public void GetTransactionFee(long fee) {
        if (fee == -1) {
            progressBarManager.HideProgressBar();
            Toast.makeText(this, "Không lấy được phí giao dịch", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(SelectMobileCardFunctionActivity.this, SubmitOrderActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.AMOUNT.GetValue(), userAmount);
        intent.putExtra(Symbol.PHONE.GetValue(), phone);
        intent.putExtra(Symbol.MOBILE_CODE.GetValue(), mobileCardOperatorChosen.GetMobileCode());
        intent.putExtra(Symbol.MOBILE_AMOUNT.GetValue() , currentAmount.GetAmount());
        intent.putExtra(Symbol.FEE_TRANSACTION.GetValue(), 0);
        intent.putExtra(Symbol.SERVICE_TYPE.GetValue(), Service.MOBILE_CARD_SERVICE_TYPE.GetCode());
        intent.putExtra(Symbol.SOURCE_OF_FUND.GetValue(), SourceFund.WALLET_SOURCE_FUND.GetCode());
        intent.putExtra(Symbol.SECRET_KEY_01.GetValue(), secretKeyString1);
        intent.putExtra(Symbol.SECRET_KEY_02.GetValue(), secretKeyString2);
        startActivityForResult(intent, RequestCode.SUBMIT_ORDER);
    }
}
