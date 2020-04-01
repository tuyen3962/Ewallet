package com.example.ewalletexample.service.realtimeDatabase;

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

    public void PushDataIntoDatabase(String childName, T data){
        mDatabase.child(childName).push().setValue(data);
    }

    public void UpdateData(String childName){

    }
}
