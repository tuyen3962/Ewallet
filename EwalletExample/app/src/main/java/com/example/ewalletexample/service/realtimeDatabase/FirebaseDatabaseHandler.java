package com.example.ewalletexample.service.realtimeDatabase;

import com.example.ewalletexample.Symbol.Symbol;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import androidx.annotation.NonNull;

public class FirebaseDatabaseHandler<T> implements DatabaseValueListenerFunction<T> {

    private DatabaseReference mDatabase;

    HandleDataFromFirebaseDatabase<T> dataHandler;

    ValueEventListener databaseValueEvent;

    private String keyModel;
    private Class<T> tClass;
    private ResponseModelByKey response;
    ValueEventListener findDataEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            T model = dataSnapshot.child(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE.GetValue()).child(keyModel).getValue(tClass);
            UnregisterFindValue(model);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    public FirebaseDatabaseHandler(DatabaseReference mDatabase, HandleDataFromFirebaseDatabase<T> handleDataFromDatabase){
        this.mDatabase = mDatabase;
        SetRegisterDatabaseValueListener(handleDataFromDatabase);

        databaseValueEvent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataHandler.HandleDataSnapShot(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dataHandler.HandlerDatabaseError(databaseError);
            }
        };
    }

    public FirebaseDatabaseHandler(DatabaseReference mDatabase){
        this.mDatabase = mDatabase;
    }

    public void SetRegisterDatabaseValueListener(HandleDataFromFirebaseDatabase<T> registerInterface){
        this.dataHandler = registerInterface;
    }

    @Override
    public void RegisterDataListener(){
        mDatabase.addValueEventListener(databaseValueEvent);
    }

    @Override
    public void UnregisterValueListener(T model) {
        mDatabase.removeEventListener(databaseValueEvent);
        dataHandler.HandleDataModel(model);
    }

    public T GetUserModelByKey(DataSnapshot dataSnapshot, String key, Class<T> tClass){
        if(ContainsKey(dataSnapshot.child(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE.GetValue()), key)){
            return dataSnapshot.child(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE.GetValue()).child(key).getValue(tClass);
        }

        return null;
    }

    private boolean ContainsKey(DataSnapshot dataSnapshot, String key){
        return dataSnapshot.hasChild(key);
    }

    public void PushDataIntoDatabase(String childName, String id, T data){
        mDatabase.child(childName).child(id).setValue(data);
    }

    public void PushDataIntoDatabase(String childName, T data){
        mDatabase.child(childName).push().setValue(data);
    }

    public void UpdateData(String childName, String id, T data){
        mDatabase.child(childName).child(id).setValue(data);
    }

    public void UpdateData(String childName, String id, Map<String, Object> map){
        mDatabase.child(childName).child(id).updateChildren(map);
    }

    public void GetUserModelByKey(String key, Class<T> tClass, ResponseModelByKey response){
        keyModel = key;
        this.tClass = tClass;
        mDatabase.addValueEventListener(findDataEventListener);
        this.response = response;
    }

    private void UnregisterFindValue(T model){
        mDatabase.removeEventListener(findDataEventListener);
        response.GetModel(model);
    }
}
