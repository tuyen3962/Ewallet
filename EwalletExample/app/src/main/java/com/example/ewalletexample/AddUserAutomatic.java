package com.example.ewalletexample;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.ewalletexample.Server.api.register.RegisterRequest;
import com.example.ewalletexample.Server.api.register.RegisterUserAPI;
import com.example.ewalletexample.Server.api.register.RegisterCallback;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.temp.CarrierNumber;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AddUserAutomatic extends AppCompatActivity {
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_automatic);
        btnAdd = findViewById(R.id.btnAdd);
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference());
        btnAdd = findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add(v);
            }
        });
    }

    public void Add(View view) {
        new AddUser().start();
    }

    class AddUser extends Thread implements RegisterCallback {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://api.namefake.com").get().build();

        private String randomName(){
            try {
                Response response = client.newCall(request).execute();
                ResponseBody body = response.body();
                String bodyString = body.string();
                return bodyString;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run(){
            try {
                for (int i = 0; i < 100; i++) {
                    String body = randomName();
                    if (body.equalsIgnoreCase("")) {
                        continue;
                    }
                    JSONObject jsonObject = new JSONObject(body);
                    RegisterRequest request = new RegisterRequest();
                    request.fullname = jsonObject.getString("name");
                    request.phone = CarrierNumber.GetInstance().GetPhoneNumber();
                    request.pin = "123456";
                    Log.e("TAG", "RegisterTemp: start add");
                    RegisterUserAPI registerAPI = new RegisterUserAPI(request, this);
                    registerAPI.StartRegister(getString(R.string.public_key), getString(R.string.share_key));
                    Thread.sleep(1000);
                }
            } catch (InterruptedException | JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void RegisterSuccessful(String userid, String customToken, String secretKeyString1, String secretKeyString2) {
//            UserModel model = new UserModel();
//            model.setFullname(fullName);
//            model.setPhone(phone);
//            firebaseDatabaseHandler.PushDataIntoDatabase(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE.GetValue(), userid, model);
        }

        @Override
        public void RegisterTemp(String userid, String fullName, String phone){
            Log.e("TAG", "RegisterTemp: add successfully");
            UserModel model = new UserModel();
            model.setFullname(fullName);
            model.setPhone(phone);
            firebaseDatabaseHandler.PushDataIntoDatabase(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE.GetValue(), userid, model);
        }
    }
}
