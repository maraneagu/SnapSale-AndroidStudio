package com.example.snapsale.database.managers;

import androidx.annotation.NonNull;

import com.example.snapsale.models.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


// HELPFUL LINKS:
// 1). "Login and Registration using Firebase in Android, Android Knowledge", URL: https://www.youtube.com/watch?v=QAKq8UBv4GI&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=12&ab_channel=CodesEasy
// 2). "Login and Signup using Firebase Authentication in Android Studio | Java", Android Knowledge, URL: https://www.youtube.com/watch?v=TStttJRAPhE&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=16&ab_channel=AndroidKnowledge
// 3). "Android Firebase Course #11 : Firebase Anonymous Login | 2024", Android Coding with Harsha, URL: https://www.youtube.com/watch?v=HfVmgKugGCs&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=79&t=780s&ab_channel=AndroidCodingwithHarsha


public class FirebaseAuthentificationManager {
    private static FirebaseAuthentificationManager instance;
    private static FirebaseManager firebaseManager;
    private static FirebaseAuth firebaseAuth;
    private static DatabaseReference usersReference = FirebaseManager.getInstance().getUsersReference();

    private FirebaseAuthentificationManager() {
        firebaseManager = FirebaseManager.getInstance();
        firebaseAuth = firebaseManager.getFirebaseAuth();
        usersReference = firebaseManager.getUsersReference();
    }

    public static synchronized FirebaseAuthentificationManager getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthentificationManager();
        }
        return instance;
    }

    public interface SignUpCallback {
        void onSignUpSuccess();
        void onSignUpUsernameExists(String exceptionMessage);
        void onSignUpFailure(Exception exception);
    }

    public void signUp(String username, String email, String password, SignUpCallback callback) {
        usersReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    callback.onSignUpUsernameExists("The username is already in use by another account.");
                }
                else {
                    if (firebaseManager.isCurrentUserAnonymous()) {
                        AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);
                        firebaseManager.getCurrentUser().linkWithCredential(authCredential)
                            .addOnSuccessListener(result -> {
                                FirebaseUser currentUser = firebaseManager.getCurrentUser();
                                assert currentUser != null;

                                User user = new User(username);
                                usersReference.child(currentUser.getUid()).setValue(user)
                                        .addOnSuccessListener(s -> callback.onSignUpSuccess())
                                        .addOnFailureListener(callback::onSignUpFailure);
                            })
                            .addOnFailureListener(callback::onSignUpFailure);
                    }
                    else {
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(result -> {
                                FirebaseUser currentUser = firebaseManager.getCurrentUser();
                                assert currentUser != null;

                                User user = new User(username);
                                usersReference.child(currentUser.getUid()).setValue(user)
                                        .addOnSuccessListener(s -> callback.onSignUpSuccess())
                                        .addOnFailureListener(callback::onSignUpFailure);
                            })
                            .addOnFailureListener(callback::onSignUpFailure);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onSignUpFailure(error.toException());
            }
        });
    }

    public interface LoginCallback {
        void onLoginSuccess();
        void onLoginFailure(Exception exception);
    }

    public void login(String email, String password, LoginCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(result -> callback.onLoginSuccess())
            .addOnFailureListener(callback::onLoginFailure);
    }

    public interface SignInAnonymouslyCallback {
        void onSignInAnonymouslySuccess();
        void onSignInAnonymouslyFailure(Exception exception);
    }

    public void signInAnonymously(SignInAnonymouslyCallback callback) {
        firebaseAuth.signInAnonymously()
            .addOnSuccessListener(result -> callback.onSignInAnonymouslySuccess())
            .addOnFailureListener(callback::onSignInAnonymouslyFailure);
    }

    public void logout() {
        firebaseAuth.signOut();
    }
}
