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

    private StompClient mStompClient;
    private WebsocketResponse response;
    private Context context;
    private String userid;
    private boolean hasUser;
    private Gson gson;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public WebsocketClient(Context context, String userid, WebsocketResponse response){
        hasUser = true;
        this.context = context;
        this.response = response;
        gson = new Gson();
        this.userid = userid;
        WEBSOCKET_NOTIFICATION_TOPIC += userid;
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
                    if (response != null){
                        JSONObject json = new JSONObject(stompMessage.getPayload());
                        Log.d("TAG", "createConnection: " + stompMessage.getPayload());
                        String userid = json.getString("userid");
                        long balance = json.getLong("balance");
                        response.UpdateWallet(userid, balance);
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

    public void disconnect(){
        mStompClient.disconnect();
    }
}
