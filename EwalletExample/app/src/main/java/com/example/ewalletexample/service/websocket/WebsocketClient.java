package com.example.ewalletexample.service.websocket;

import android.app.Activity;
import android.util.Log;

import com.example.ewalletexample.Symbol.Symbol;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class WebsocketClient {
    private static final String WEBSOCKET_TOPIC = "/ws/topic/updated_wallet";

    private StompClient mStompClient;
    private WebsocketResponse response;

    public WebsocketClient(WebsocketResponse response){
        this.response = response;
        createConnection();
    }


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
                    Log.d("TAG", "createConnection: " + stompMessage.getPayload());
                    String userid = json.getString("userid");
                    long balance = json.getLong("balance");
                    response.UpdateWallet(userid, balance);
                });
    }

    public void disconnect(){
        mStompClient.disconnect();
    }
}
