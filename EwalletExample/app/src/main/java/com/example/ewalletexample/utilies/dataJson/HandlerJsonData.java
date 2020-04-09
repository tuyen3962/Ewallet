package com.example.ewalletexample.utilies.dataJson;

import com.example.ewalletexample.model.UserModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class HandlerJsonData {
    public static String ExchangeUserModelToJsonData(UserModel model) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("phone", model.getPhone());
        jsonObject.put("phoneToken", model.getPhoneToken());
        jsonObject.put("email", model.getEmail());
        jsonObject.put("emailToken", model.getEmailToken());
        jsonObject.put("fullname", model.getFullname());
        jsonObject.put("imgLink", model.getImgLink());

        return jsonObject.toString();
    }

    public static String ExchangeToJsonString(String[] listNameVariables) throws JSONException{
        JSONObject json = new JSONObject();

        for(int i = 0; i < listNameVariables.length; i++){
            String[] split = listNameVariables[i].split(":");
            if(split.length == 1){
                json.put(split[0],"");
            }
            else {
                json.put(split[0], split[1]);
            }
        }

        return json.toString();
    }
}
