package com.example.ewalletexample.service.MemoryPreference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferenceLocal {
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("WrongConstant")
    public SharedPreferenceLocal(Context context, String key){
        this.context = context;
        preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public String GetValueStringByKey(String key){
        return preferences.getString(key, "");
    }

    public int GetValueIntegerByKey(String key){
        return preferences.getInt(key, -1);
    }

    public void AddNewStringIntoSetString(String key, String value){
        Set<String> values = GetSetStringValueByKey(key);
        values.add(value);
        WriteStringValueByKey(key, values);
    }

    public Set<String> GetSetStringValueByKey(String key){
        return preferences.getStringSet(key, new HashSet<>());
    }

    public void WriteStringValueByKey(String key, Set<String> value){
        editor.putStringSet(key, value);
        editor.commit();
    }

    public void WriteValueByKey(String key, String value){
        editor.putString(key, value);
        editor.commit();
    }

    public void WriteIntegerValueByKey(String key, int value){
        editor.putInt(key, value);
        editor.commit();
    }

    public void DeleteKey(String key){
        editor.remove(key);
    }
}
