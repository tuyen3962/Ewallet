package com.example.ewalletexample;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ewalletexample.Server.api.notification.GetHistoryNotifycationAPI;
import com.example.ewalletexample.Server.api.notification.HistoryNotifyRequest;
import com.example.ewalletexample.Server.api.notification.HistoryNotifycationResponse;
import com.example.ewalletexample.Server.api.notification.UserNotifyEntity;
import com.example.ewalletexample.service.LoadItem.LoadItemLayout;
import com.example.ewalletexample.service.LoadItem.LoadItemLayoutFunction;
import com.example.ewalletexample.service.recycleview.notifycation.UserNotifycationEntityAdapter;
import com.example.ewalletexample.utilies.HandleDateTime;

import java.util.ArrayList;
import java.util.List;

public class HistoryNotificatoinFragment extends Fragment implements HistoryNotifycationResponse, LoadItemLayoutFunction {

    GetHistoryNotifycationAPI getHistoryNotifycationAPI;
    MainLayoutActivity mainLayoutActivity;
    RecyclerView rvListNortifycation;
    LoadItemLayout loadItemLayout;
    long timemillisecondLoadContinue;
    List<UserNotifyEntity> userNotifyEntityList;
    UserNotifycationEntityAdapter notifycationEntityAdapter;
    int pageSize;
    boolean isLoadMore;

    public HistoryNotificatoinFragment() {
        // Required empty public constructor
    }

    public static HistoryNotificatoinFragment newInstance() {
        HistoryNotificatoinFragment fragment = new HistoryNotificatoinFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainLayoutActivity) {
            this.mainLayoutActivity = (MainLayoutActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_notificatoin, container, false);
        Initialize(view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void Initialize(View view){
        isLoadMore = false;
        pageSize = 10;
        userNotifyEntityList = new ArrayList<>();
        rvListNortifycation = view.findViewById(R.id.rvListNotifycation);
        loadItemLayout = new LoadItemLayout(mainLayoutActivity, this::LoadContinue);
        HistoryNotifyRequest request = new HistoryNotifyRequest();
        request.userid = mainLayoutActivity.GetUserInformation().getUserId();
        request.pagesize = pageSize;
        request.starttime = HandleDateTime.GetCurrentTimeMillis();
        request.key = mainLayoutActivity.firstKeyString;
        request.secondKey = mainLayoutActivity.secondKeyString;
        rvListNortifycation.setLayoutManager(new LinearLayoutManager(mainLayoutActivity, LinearLayoutManager.VERTICAL, false));
        notifycationEntityAdapter = new UserNotifycationEntityAdapter(mainLayoutActivity, userNotifyEntityList);
        rvListNortifycation.setAdapter(notifycationEntityAdapter);
        getHistoryNotifycationAPI = new GetHistoryNotifycationAPI(request, mainLayoutActivity.getString(R.string.public_key), this);
        getHistoryNotifycationAPI.StartRequest();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void LoadContinue() {
        isLoadMore = true;
        getHistoryNotifycationAPI.SetStartTime(timemillisecondLoadContinue);
        getHistoryNotifycationAPI.SetPage(pageSize + 1);
        getHistoryNotifycationAPI.StartRequest();
        loadItemLayout.ShowProgressBar();
    }

    @Override
    public void ListNotifyResponse(List<UserNotifyEntity> notifyEntities) {
        if (notifyEntities.size() == 0){
            loadItemLayout.ShowFull();
        } else {
            if (isLoadMore){
                notifyEntities.remove(0);
            }
            if (notifyEntities.size() == 0){
                loadItemLayout.HideAll();
                return;
            }
            loadItemLayout.ShowLoad();
            userNotifyEntityList.addAll(notifyEntities);
            timemillisecondLoadContinue = notifyEntities.get(notifyEntities.size() - 1).timemilliseconds;
            rvListNortifycation.notifyAll();
        }
    }
}
