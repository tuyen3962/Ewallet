package com.example.ewalletexample.service.websocket;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.UserNotifyEntity;
import com.example.ewalletexample.service.notification.NotificationCreator;
import com.google.gson.Gson;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import androidx.annotation.RequiresApi;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class WebsocketClient {
    private static final String WEBSOCKET_TOPIC = "/ws/topic/updated_wallet";
    private static String WEBSOCKET_NOTIFICATION_TOPIC = "/ws/topic/notify_";

    private WebsocketResponse response;
    private StompClient mStompClient;
    private Context context;
    private String userid;
    private boolean hasUser, changeBalance;
    private Gson gson;
    private long newBalance;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public WebsocketClient(Context context, String userid){
        hasUser = true;
        this.context = context;
        gson = new Gson();
        this.userid = userid;
        WEBSOCKET_NOTIFICATION_TOPIC += userid;
        changeBalance = false;
        newBalance = 0;
        createConnection();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public WebsocketClient(Context context, String userid, WebsocketResponse response){
        hasUser = true;
        this.context = context;
        this.response = response;
        gson = new Gson();
        this.userid = userid;
        WEBSOCKET_NOTIFICATION_TOPIC += userid;
        changeBalance = false;
        newBalance = 0;
        createConnection();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createConnection(){
        // replace your websocket url
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://" + Symbol.BASE_ADDRESS.GetValue() + ":8080/ws-stomp/websocket");
        mStompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d("TAG", "Stomp connection opened");
                    break;

                case ERROR:
                    Log.e("TAG", "Error", lifecycleEvent.getException());
                    break;

                case CLOSED:
                    Log.d("TAG", "Stomp connection closed");
                    break;
            }
        });

        mStompClient.connect();

        // replace with your topics
        mStompClient.topic(WEBSOCKET_TOPIC)
                .subscribe(stompMessage -> {
                    JSONObject json = new JSONObject(stompMessage.getPayload());
                    String userId = json.getString("userid");
                    long balance = json.getLong("balance");
                    if (this.userid.isEmpty() == false && this.userid.equalsIgnoreCase(userId)){
                        changeBalance = true;
                        newBalance = balance;
                        if (response != null){
                            response.UpdateWallet(newBalance);
                        }
                    }
                });

        if (hasUser){
            mStompClient.topic(WEBSOCKET_NOTIFICATION_TOPIC)
                    .subscribe(stompMessage -> {
                        UserNotifyEntity notifyEntity = gson.fromJson(stompMessage.getPayload(), UserNotifyEntity.class);
                        if (notifyEntity.getUserId().equalsIgnoreCase(userid)){
                            NotificationCreator.ShowNotification(context, notifyEntity);
                        }
                    });
        }
    }

    public boolean IsUpdateBalance(){
        return changeBalance;
    }

    public long getNewBalance(){
        return newBalance;
    }

    public void disconnect(){
        mStompClient.disconnect();
    }
}
