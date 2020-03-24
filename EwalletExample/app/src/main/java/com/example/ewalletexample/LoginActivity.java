package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.model.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class LoginActivity extends AppCompatActivity {

    private final String urlLoginInServer = "http://192.168.1.14:8080/um/login";

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvLogging, tvForgetPassword, tvRegister;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ui);

        InitLayoutProperties();
    }

    void InitLayoutProperties(){
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        tvLogging = findViewById(R.id.tvLogging);
        tvForgetPassword = findViewById(R.id.tvForgetPass);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);
    }

    public void UserLoginEvent(View view){
        ShowLoading();

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        Response response = CheckUsernameAndPassword(username,password);

        if(response.isStatus()){
            SendLoginRequest(username,password);
        }
        else{
            HideLoading();
            Toast.makeText(LoginActivity.this, response.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    Response CheckUsernameAndPassword(String username, String password){
        if(TextUtils.isEmpty(username)){
            return new Response("Nhap username", false);
        }
        else if(TextUtils.isEmpty(password)){
            return new Response("Nhap mat khau", false);
        }

        return new Response("", true);
    }

    private void SendLoginRequest(String username, String password){
        JSONObject json = new JSONObject();

        try{
            json.put("phone",username);
            json.put("pin",password);

            new LoginThread().execute(urlLoginInServer, json.toString());
        }catch (Exception e){
            HideLoading();
            Log.d("TAG", "CheckUsernameAndPassword: "  + e.getMessage());
        }
    }

    public void UserForgetPasswordEvent(View view){
        startActivity(new Intent(LoginActivity.this, VerifyUserForForget.class));
    }

    public void UserRegisterEvent(View view){
        startActivity(new Intent(LoginActivity.this, RegisterByPhone.class));
    }

    private void ShowLoading(){
        tvLogging.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        SetEnableForEditLogin(false);
    }

    private void HideLoading(){
        tvLogging.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        SetEnableForEditLogin(true);
    }

    private void SetEnableForEditLogin(boolean active){
        tvRegister.setEnabled(active);
        tvForgetPassword.setEnabled(active);
        etPassword.setEnabled(active);
        etUsername.setEnabled(active);
        btnLogin.setEnabled(active);
    }

    private class LoginThread extends AsyncTask<String, Void, ResponseEntity<String>>{
        @Override
        protected ResponseEntity<String> doInBackground(String... params) {
            final String url = params[0];

            RestTemplate restTemplate = new RestTemplate();

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

                HttpEntity<String> entity = new HttpEntity<>(params[1], headers);

                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

                return response;
            }
            catch (Exception e){
                HideLoading();
                Log.w("Warn",e.getMessage());
            }

            return null;
        }



        @Override
        protected void onPostExecute(ResponseEntity<String> response){
            if(response != null){
                String body = response.getBody();
                try{
                    JSONObject json = new JSONObject(body);
                    int retureCode = json.getInt("returncode");
                    if (retureCode == ErrorCode.SUCCESS.GetValue()){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        intent.putExtra(Symbol.USER_ID.GetValue(), json.getString("userid"));

                        startActivity(intent);
                    }
                    else
                    {
                        HideLoading();
                    }
                } catch (JSONException e) {
                    HideLoading();
                    e.printStackTrace();
                }
            }
        }
    }
}
