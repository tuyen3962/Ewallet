package com.example.ewalletexample.utilies;

import com.google.gson.Gson;

public class GsonUtils {
    public static final Gson gson = new Gson();

    public static String toJsonString(Object obj) {
        return gson.toJson(obj);
    }

    public static Gson getGson(){
        return gson;
    }
}
