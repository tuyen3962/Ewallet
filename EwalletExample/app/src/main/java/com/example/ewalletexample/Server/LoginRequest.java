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

public class LoginRequest extends AsyncTask<String, Void, ResponseEntity<String>> {
    @Override
    protected ResponseEntity<String> doInBackground(String... params) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            HttpEntity<String> entity = new HttpEntity<>(params[0], headers);

            ResponseEntity<String> response = restTemplate.exchange(ServerAPI.LOGIN_API.GetUrl(), HttpMethod.POST, entity, String.class);

            return response;
        }
        catch (Exception e){
            Log.w("Warn",e.getMessage());
        }

        return null;
    }
}
