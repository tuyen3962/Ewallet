package com.example.ewalletexample.Server.api.checklist;

import android.os.Build;
import android.util.Log;

import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.model.UserSearchModel;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.utilies.SecurityUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import androidx.annotation.RequiresApi;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void StartRequest(String publicKeyString, String secretKeyString1, String secretKeyString2){
        try {
            PublicKey publicKey = SecurityUtils.generatePublicKey(publicKeyString);
            SecretKey secretKey1 = SecurityUtils.generateAESKeyFromText(secretKeyString1);
            SecretKey secretKey2 = SecurityUtils.generateAESKeyFromText(secretKeyString2);

            String encryptSecondKeyByRSA = SecurityUtils.encryptRSA(publicKey, secretKeyString2);

            String encryptKeyByRSA = SecurityUtils.encryptRSA(publicKey, secretKeyString1);
            String encryptKeyByAES = SecurityUtils.decryptAES(secretKey2, encryptKeyByRSA);

            String header = "";

            JSONObject jsonObject = new JSONObject();
            JSONArray array = new JSONArray();
            for (String phone : list){
                header += phone;
                array.put(phone);
            }

            header += secretKeyString1 + secretKeyString2;

            String hashHeader = SecurityUtils.encryptHmacSha256(secretKey1, header);
            String encryptHeader = SecurityUtils.encryptAES(secretKey2, hashHeader);
            String encryptHeaderByRSA = SecurityUtils.encryptRSA(publicKey, encryptHeader);

            jsonObject.put("key", encryptKeyByAES);
            jsonObject.put("secondKey", encryptSecondKeyByRSA);
            jsonObject.put("phones", array);

            new CheckListPhoneContact().execute(ServerAPI.CHECK_LIST_PHONE.GetUrl(), jsonObject.toString(), encryptHeaderByRSA);
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
