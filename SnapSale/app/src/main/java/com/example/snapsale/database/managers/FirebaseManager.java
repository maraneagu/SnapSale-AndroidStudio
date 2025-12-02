package com.example.snapsale.database.managers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {
    private static FirebaseManager instance;
    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference usersReference;
    private final DatabaseReference storesReference;

    private FirebaseManager() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        usersReference = firebaseDatabase.getReference().child("users");
        storesReference = firebaseDatabase.getReference().child("stores");
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
    public boolean isCurrentUserAnonymous() {
        if (getCurrentUser() != null)
            return getCurrentUser().isAnonymous();
        return false;
    }

    public DatabaseReference getUsersReference() {
        return usersReference;
    }

    public DatabaseReference getStoresReference() {
        return storesReference;
    }
    public DatabaseReference getFavoredSalesReference() {
        DatabaseReference favoredSalesReference;
        FirebaseUser currentUser = getCurrentUser();

        if (!isCurrentUserAnonymous()) {
            favoredSalesReference = usersReference.child(currentUser.getUid()).child("favoredSales");
        } else {
            favoredSalesReference = null;
        }
        return favoredSalesReference;
    }
}
