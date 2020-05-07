package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ewalletexample.Server.api.register.RegisterUserAPI;
import com.example.ewalletexample.Server.api.register.ResgisterCallback;
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
//                firebaseDatabaseHandler.RegisterDataListener();
                try {
                    CreateUser();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void CreateUser() throws IOException, JSONException {
        new Test().execute();
    }

    public class Test extends AsyncTask<Void, Void, String> implements ResgisterCallback {

        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://dawn2k-random-german-profiles-and-names-generator-v1.p.rapidapi.com/?count=80&gender=b&maxage=40&minage=30&cc=all&email=gmail.com%252Cyahoo.com&pwlen=12&ip=a&phone=l%252Ct%252Co&uuid=false&lic=false&color=false&seed=helloworld&images=false&format=json")
                    .get()
                    .addHeader("x-rapidapi-host", "dawn2k-random-german-profiles-and-names-generator-v1.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "30182fbfc2msha3b65e90ed4ed14p180b5djsnf480a5b9d0b6")
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                ResponseBody body = response.body();
                String bodyString = body.string();
                return bodyString;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String text){
            try {
                JSONArray json = new JSONArray(text);
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonObject = json.getJSONObject(i);
                    String fullname = jsonObject.getString("firstname") + " " + jsonObject.getString("lastname");
                    String phone = CarrierNumber.GetInstance().GetPhoneNumber();
                    String pin = "123456";
                    RegisterUserAPI registerAPI = new RegisterUserAPI(fullname, phone, pin, this);
                    registerAPI.StartRegister();
                    Thread.sleep(1000);
                }
            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void RegisterSuccessful(String userid, String fullName, String phone) {
//            UserModel model = new UserModel();
//            model.setFullname(fullName);
//            model.setPhone(phone);
//            firebaseDatabaseHandler.PushDataIntoDatabase(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE.GetValue(), userid, model);
        }
    }
}
