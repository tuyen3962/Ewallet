package com.example.ewalletexample.service.realtimeDatabase;

import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

public class FirebaseDatabaseHandler<T> implements DatabaseValueListenerFunction<T> {

    private DatabaseReference mDatabase;

    HandleDataFromFirebaseDatabase<T> dataHandler;

    ValueEventListener databaseValueEvent;

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

    public T GetUserModelByKey(DataSnapshot dataSnapshot, String key){
        if(ContainsKey(dataSnapshot.child(Symbol.CHILD_NAME_FIREBASE_DATABASE.GetValue()), key)){
            return (T) dataSnapshot.child(Symbol.CHILD_NAME_FIREBASE_DATABASE.GetValue()).child(key).getValue();
        }

        return null;
    }

    private boolean ContainsKey(DataSnapshot dataSnapshot, String key){
        return dataSnapshot.hasChild(key);
    }

    public void PushDataIntoDatabase(String childName, String id,T data){
        mDatabase.child(childName).setValue(id);
        mDatabase.child(childName).child(id).setValue(data);
    }

    public void UpdateData(String childName){

    }
}
