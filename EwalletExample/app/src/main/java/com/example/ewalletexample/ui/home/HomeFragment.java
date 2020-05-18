package com.example.ewalletexample.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ewalletexample.MainLayoutActivity;
import com.example.ewalletexample.R;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.VerifyAccountActivity;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.service.UserSelectFunction;
import com.example.ewalletexample.service.recycleview.service.RecycleHorzontalServiceView;
import com.example.ewalletexample.service.recycleview.service.RecycleViewService;
import com.example.ewalletexample.utilies.Utilies;

public class HomeFragment extends Fragment implements UserSelectFunction<Service> {

    HomeViewModel homeViewModel;
    RecyclerView rvService, rvMainService;
    CircleImageView imgAccount;
    TextView tvBalance, tvIntroduce, tvVerifyAccount;
    MainLayoutActivity mainActivity;
    RecycleViewService recycleViewMainService;
    RecycleHorzontalServiceView recycleViewService;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof MainLayoutActivity){
            mainActivity = (MainLayoutActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        Initialize(view);
        SetupUI();
        return view;
    }

    void Initialize(View view){
        imgAccount = view.findViewById(R.id.imgAccount);
        tvBalance = view.findViewById(R.id.tvBalance);
        tvIntroduce = view.findViewById(R.id.tvIntroduce);
        rvService = view.findViewById(R.id.rvService);
        rvMainService = view.findViewById(R.id.rvMainService);
        tvVerifyAccount = view.findViewById(R.id.tvVerifyAccount);
        recycleViewService = new RecycleHorzontalServiceView(mainActivity, Service.GetList(), this);
        rvService.setLayoutManager(new GridLayoutManager(mainActivity, 2));
        rvService.setAdapter(recycleViewService);
        recycleViewMainService = new RecycleViewService(mainActivity, this, Service.GetMainService(), true);
        rvMainService.setLayoutManager(new GridLayoutManager(mainActivity, 4));
        rvMainService.setAdapter(recycleViewMainService);

        tvVerifyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = mainActivity.GetUserInformation();
                Intent intent = new Intent(mainActivity, VerifyAccountActivity.class);
                intent.putExtra(Symbol.UPDATE_SYMBOL.GetValue(), Symbol.UPDATE_FOR_INFORMATION.GetValue());
                intent.putExtra(Symbol.USER_ID.GetValue(), user.getUserId());
                intent.putExtra(Symbol.FULLNAME.GetValue(), user.getFullName());
                intent.putExtra(Symbol.CMND.GetValue(), user.getCmnd());
                intent.putExtra(Symbol.IMAGE_CMND_FRONT.GetValue(), user.getCmndFrontImage());
                intent.putExtra(Symbol.IMAGE_CMND_BACK.GetValue(), user.getCmndBackImage());
                startActivityForResult(intent, RequestCode.VERIFY_ACCOUNT_CODE);
            }
        });
    }

    public void SetupUI(){
        mainActivity.SetImageUriForImageView(imgAccount);
        tvBalance.setText(Utilies.HandleBalanceTextView(String.valueOf(mainActivity.GetUserBalance())));
        tvIntroduce.setText("Xin chào, " + mainActivity.GetUserInformation().getFullName() +"!");
        if (mainActivity.GetUserInformation().getStatus() == 0){
            tvVerifyAccount.setVisibility(View.VISIBLE);
        } else if (mainActivity.GetUserInformation().getStatus() == 2){
            tvVerifyAccount.setVisibility(View.VISIBLE);
            tvVerifyAccount.setText("Tài khoản xác thực thất bại");
        } else {
            tvVerifyAccount.setVisibility(View.GONE);
        }
    }

    @Override
    public void SelectModel(Service model) {
        if (model == Service.SCAN_CODE_QR || model == Service.WATCH_CODE_QR){
            Intent intent = new Intent(mainActivity, model.GetClassTransition());
            intent.putExtra(Symbol.USER_ID.GetValue(), mainActivity.GetUserInformation().getUserId());
            if (model == Service.SCAN_CODE_QR){
                intent.putExtra(Symbol.QRCODE.GetValue(), Symbol.SCAN.GetValue());
            } else {
                intent.putExtra(Symbol.QRCODE.GetValue(), Symbol.GENERATE.GetValue());
            }
            startActivity(intent);
        }
        if (mainActivity.GetUserInformation().getStatus() == 1){
            User user = mainActivity.GetUserInformation();
            Intent intent = new Intent(mainActivity, model.GetClassTransition());
            intent.putExtra(Symbol.USER_ID.GetValue(), user.getUserId());
            intent.putExtra(Symbol.AMOUNT.GetValue(), mainActivity.GetUserBalance());
            intent.putExtra(Symbol.PHONE.GetValue(), user.getPhoneNumber());
            intent.putExtra(Symbol.CMND.GetValue(), user.getCmnd());
            intent.putExtra(Symbol.SERVICE_TYPE.GetValue(), model.GetCode());
            startActivity(intent);
        }
        else {
            mainActivity.ShowSecurityLayout();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.VERIFY_ACCOUNT_CODE ){
            if(resultCode == ((Activity) mainActivity).RESULT_OK){
                String imgFront = data.getStringExtra(Symbol.IMAGE_CMND_FRONT.GetValue());
                String imgBack = data.getStringExtra(Symbol.IMAGE_CMND_BACK.GetValue());
                String cmnd = data.getStringExtra(Symbol.CMND.GetValue());
                mainActivity.SetUserSecurityInfo(cmnd, imgFront, imgBack);
            }
        }
    }
}