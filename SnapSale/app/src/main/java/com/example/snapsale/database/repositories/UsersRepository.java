package com.example.snapsale.database.repositories;

import android.app.AlertDialog;

import androidx.annotation.NonNull;

import com.example.snapsale.callbacks.DataCallback;
import com.example.snapsale.database.managers.FirebaseManager;
import com.example.snapsale.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


// HELPFUL LINKS:
// 1). "Store, Retrieve, Search, Delete and Update Data using Firebase Realtime Database in Android Studio", Android Knowledge, URL: https://www.youtube.com/watch?v=DWIGAkYkpg8&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=13&t=2s&ab_channel=AndroidKnowledge


public class UsersRepository {
    private static UsersRepository instance;
    private static FirebaseManager firebaseManager;

    private UsersRepository() {
        firebaseManager = FirebaseManager.getInstance();
    }

    public static synchronized UsersRepository getInstance() {
        if (instance == null) {
            instance = new UsersRepository();
        }
        return instance;
    }

    public void getUsername(DataCallback<String> callback) {
        DatabaseReference usersReference = firebaseManager.getUsersReference();
        FirebaseUser currentUser = firebaseManager.getCurrentUser();

        usersReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;

                    String username = user.getUsername();
                    callback.onGetData(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public String getEmail() {
        FirebaseUser currentUser = firebaseManager.getCurrentUser();
        return currentUser.getEmail();
    }

    public interface ChangePasswordCallback {
        void onChangePasswordSuccess(AlertDialog dialog);
        void onChangePasswordFailure(Exception exception);
    }

    public void changePassword(AlertDialog dialog, ChangePasswordCallback callback) {
        FirebaseAuth firebaseAuth = firebaseManager.getFirebaseAuth();

        firebaseAuth.sendPasswordResetEmail(getEmail())
            .addOnSuccessListener(s -> callback.onChangePasswordSuccess(dialog))
            .addOnFailureListener(callback::onChangePasswordFailure);
    }
}
