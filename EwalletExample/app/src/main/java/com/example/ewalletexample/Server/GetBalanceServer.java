package com.example.ewalletexample.Server;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ewalletexample.service.ServerAPI;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class GetBalanceServer extends AsyncTask<String,Void, ResponseEntity<String>> {
    @Override
    protected ResponseEntity<String> doInBackground(String... params) {
        String userid_json = params[0];
        final String url = ServerAPI.GET_BALANCE_API.GetUrl();

        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            HttpEntity<String> entity = new HttpEntity<>(userid_json, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            return response;
        }
        catch (Exception e){
            Log.w("Warn",e.getMessage());
        }

        return null;
    }
}
