package com.example.ewalletexample.service.realtimeDatabase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface HandleDataFromFirebaseDatabase<T> {
    void HandleDataModel(T data);

    void HandleDataSnapShot(DataSnapshot dataSnapshot);

    void HandlerDatabaseError(DatabaseError databaseError);
}
