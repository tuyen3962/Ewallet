package com.example.ewalletexample.Server.request;

import org.json.JSONException;
import org.json.JSONObject;

public interface RequestServerFunction {
    public boolean CheckReturnCode(int code);
    public void DataHandle(JSONObject jsonData) throws JSONException;
    public void ShowError(int errorCode, String message);
}
