package com.example.ewalletexample.Server.request;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.example.ewalletexample.Symbol.ErrorCode;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

import androidx.annotation.RequiresApi;

public class RequestServerAPI extends AsyncTask<String, Void, ResponseEntity<String>> {

    RequestServerFunction requestFunction;

    public void SetRequestServerFunction(RequestServerFunction newRequestServer){
        requestFunction = newRequestServer;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected ResponseEntity<String> doInBackground(String... params) {
        final String url = params[0];
        String data = params[1];
        String header = "";
        if (params.length > 2){
            header = params[2];
        }

        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (header != null && !header.equalsIgnoreCase("")){
                headers.set("Authorization", header);
            }
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));

            HttpEntity<String> entity = new HttpEntity<>(data, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            return response;
        }
        catch (Exception e){
            Log.w("Warn",e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(ResponseEntity<String> responseEntity){
        if(responseEntity != null){
            String body = responseEntity.getBody();
            try{
                JSONObject json = new JSONObject(body);
                int retureCode = json.getInt("returncode");
                Log.d("TAG", "onPostExecute: " + retureCode + " " + json.toString());
                boolean resultRequest = requestFunction.CheckReturnCode(retureCode);
                if (resultRequest){
                    requestFunction.DataHandle(json);
                }
            } catch (JSONException e) {
                requestFunction.ShowError(ErrorCode.EXCEPTION.GetValue(), e.getMessage());
            }
        }
    }
}
