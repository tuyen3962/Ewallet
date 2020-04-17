package com.example.ewalletexample;

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
import android.widget.Toast;

import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.SourceFund;
import com.example.ewalletexample.Symbol.Symbol;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class SelectMobileCardFunctionActivity extends AppCompatActivity implements SelectMobileCardFunction, HandleDataFromFirebaseDatabase<MobileCardModel> {
    private final int colorDefault = R.color.Grey;
    private final int colorChosen = R.color.Black;

    String userid, phone;
    long userAmount;

    FirebaseDatabaseHandler<MobileCardModel> firebaseDatabaseHandler;
    FirebaseStorageHandler firebaseStorageHandler;

    RecyclerView  rvMobileCardOperator, layoutCardAmount;

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
        arrMobileCardNetwork = MobileCard.GetInstance().GetMobileCardOperator();

        firebaseStorageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference(), this);

        rvMobileCardOperator = findViewById(R.id.rvMobileCardOperator);
        mobileCardOperatorAdapter = new MobileCardOperatorAdapter(arrMobileCardNetwork, this, this);
        InitializeRecycleView(rvMobileCardOperator,
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false),
                mobileCardOperatorAdapter);

        layoutCardAmount = findViewById(R.id.layoutCardAmount);
        listAmounts = MobileCard.GetInstance().GetListAmountsByMobileCardOperator(arrMobileCardNetwork[0]);
        mobileCardOperatorChosen = arrMobileCardNetwork[0];
        mobileCardAmountAdapter = new MobileCardAmountAdapter(this, listAmounts, this);
        InitializeRecycleView(layoutCardAmount,
                new GridLayoutManager(this, 3),
                mobileCardAmountAdapter);
    }

    void GetValueFromIntent(){
        Intent intent = getIntent();
        userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        userAmount = intent.getLongExtra(Symbol.AMOUNT.GetValue(), 0);
        phone = intent.getStringExtra(Symbol.PHONE.GetValue());
    }

    void InitializeRecycleView(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager, RecyclerView.Adapter adapter){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void SetCurrentMobileCardOperatorViewHolder(MobileCardOperatorAdapter.MobileCardOperatorViewHolder mobileCardOperatorViewHolder) {
        currentMobileCardOperatorViewHolder = mobileCardOperatorViewHolder;
        currentMobileCardOperatorViewHolder.SetBackgroundLayout(colorChosen);
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

    private void SetMobileCardOperatorChosen(MobileCardOperator mobileCardOperator){
        this.mobileCardOperatorChosen = mobileCardOperator;
        listAmounts = MobileCard.GetInstance().GetListAmountsByMobileCardOperator(mobileCardOperator);
        mobileCardAmountAdapter.notifyDataSetChanged();
    }

    @Override
    public MobileCardOperator GetCurrentMobileCardOperator() {
        return mobileCardOperatorChosen;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void SelectMobileCardAmount(MobileCardAmountAdapter.MobileCardAmountViewHolder mobileCardAmountViewHolder, MobileCardAmount mobileCardAmount) {
        if(currentAmount != null && currentAmount == mobileCardAmount){
            return;
        }

        if (currentMobileCardAmountViewHolder != null){
            currentMobileCardAmountViewHolder.SetBackgroundLayout(colorDefault);
        }
        SetCurrentMobileCardAmountViewHolder(mobileCardAmountViewHolder);
        currentAmount = mobileCardAmount;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void SetCurrentMobileCardAmountViewHolder(MobileCardAmountAdapter.MobileCardAmountViewHolder mobileCardAmountViewHolder) {
        currentMobileCardAmountViewHolder = mobileCardAmountViewHolder;
        currentMobileCardAmountViewHolder.SetBackgroundLayout(colorChosen);
    }

    public void VerifySelection(View view){
        firebaseDatabaseHandler.RegisterDataListener();
    }

    private void StartCreateMobileOrder(){
        Intent intent = new Intent(SelectMobileCardFunctionActivity.this, SubmitOrderActivity.class);
        intent.putExtra(Symbol.USER_ID.GetValue(), userid);
        intent.putExtra(Symbol.AMOUNT.GetValue(), userAmount);
        intent.putExtra(Symbol.PHONE.GetValue(), phone);
        intent.putExtra(Symbol.MOBILE_CODE.GetValue(), mobileCardOperatorChosen.GetMobileCode());
        intent.putExtra(Symbol.MOBILE_AMOUNT.GetValue() , currentAmount.GetAmount());
        intent.putExtra(Symbol.FEE_TRANSACTION.GetValue(), 0);
        intent.putExtra(Symbol.SERVICE_TYPE.GetValue(), Service.MOBILE_CARD_SERVICE_TYPE.GetCode());
        intent.putExtra(Symbol.SOURCE_OF_FUND.GetValue(), SourceFund.WALLET_SOURCE_FUND.GetCode());
        startActivity(intent);
    }

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
}
