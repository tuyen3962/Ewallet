package com.example.ewalletexample.service.realtimeDatabase;

import com.google.firebase.database.ValueEventListener;

public interface DatabaseValueListenerFunction<T> {
    public void RegisterDataListener();

    public void UnregisterValueListener(T model);
}
