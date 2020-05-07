package com.example.ewalletexample.Server.api.checklist;

import android.util.Log;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.model.UserSearchModel;
import com.example.ewalletexample.service.ServerAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckListAPI {
    private List<String> list;
    private CheckListResponse response;
    private String phone;

    public CheckListAPI(CheckListResponse response){
        list = new ArrayList<>();
        this.response = response;
    }

    public void SetList(List<String> list){
        this.list = list;
    }

    public void SetPhone(String phone){
        this.phone = phone;
    }

    public void StartRequest(){
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray array = new JSONArray();
            for (String phone : list){
                array.put(phone);
            }

            jsonObject.put("listPhone", array);

            new CheckListPhoneContact().execute(ServerAPI.CHECK_LIST_PHONE.GetUrl(), jsonObject.toString());
        } catch (JSONException e){
            Log.d("TAG", "LoadContact: " + e.getMessage());
        }
    }

    class CheckListPhoneContact extends RequestServerAPI implements RequestServerFunction {
        public CheckListPhoneContact(){
            SetRequestServerFunction(this);
        }

        @Override
        public boolean CheckReturnCode(int code) {
            if (code == ErrorCode.SUCCESS.GetValue()){
                return true;
            }

            return false;
        }

        @Override
        public void DataHandle(JSONObject jsonData) throws JSONException {
            List<UserSearchModel> listResponse = new ArrayList<>();

            JSONArray contacts = jsonData.getJSONArray("phones");
            if (contacts.length() == 0){
                response.Response(listResponse);
                return;
            }
            for (int i = 0; i < contacts.length(); i++){
                JSONObject contact = contacts.getJSONObject(i);
                String phone = contact.getString("phone");
                if(phone.equalsIgnoreCase(phone)){
                    continue;
                }
                UserSearchModel model = new UserSearchModel();
                model.setUserid(contact.getString("userid"));
                model.setPhone(contact.getString("phone"));
                model.setImgLink(contact.getString("avatar"));
                model.setFullName(contact.getString("fullname"));

                listResponse.add(model);
            }

            response.Response(listResponse);
        }

        @Override
        public void ShowError(int errorCode, String message) {

        }
    }
}
