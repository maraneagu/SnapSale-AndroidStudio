package com.example.snapsale.activities.authentificationActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.snapsale.R;
import com.example.snapsale.helpers.ActivityNavigator;
import com.example.snapsale.database.managers.FirebaseAuthentificationManager;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.Objects;


// HELPFUL LINKS:
// 1). "Login and Registration using Firebase in Android, Android Knowledge", URL: https://www.youtube.com/watch?v=QAKq8UBv4GI&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=12&ab_channel=CodesEasy
// 2). "Login and Signup using Firebase Authentication in Android Studio | Java", Android Knowledge, URL: https://www.youtube.com/watch?v=TStttJRAPhE&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=16&ab_channel=AndroidKnowledge
// 3). "Android Firebase Course #11 : Firebase Anonymous Login | 2024", Android Coding with Harsha, URL: https://www.youtube.com/watch?v=HfVmgKugGCs&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=79&t=780s&ab_channel=AndroidCodingwithHarsha
// 4). "Material Design EditText in Android | Customized TextInputLayout | Android Programming Basics", iRekha Tech Solutions, URL: https://www.youtube.com/watch?v=q-DnUKbGsgA&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=80&t=710s&ab_channel=iRekhaTechSolutions
// 5). "Progress Bar in Android Studio - Mastering Android Course #34", Master Coding, URL: https://www.youtube.com/watch?v=VpnZ1wt5uDA&t=398s&ab_channel=MasterCoding


public class SignUpActivity extends AppCompatActivity implements FirebaseAuthentificationManager.SignUpCallback,
        FirebaseAuthentificationManager.SignInAnonymouslyCallback {
    private static FirebaseAuthentificationManager firebaseAuthentificationManager;
    private TextInputLayout usernameLayout, emailLayout, passwordLayout;
    private EditText username, email, password;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuthentificationManager = FirebaseAuthentificationManager.getInstance();
        initializeViews();
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.sign_up_progress_bar);

        usernameLayout = findViewById(R.id.sign_up_username_layout);
        username = findViewById(R.id.sign_up_username);
        username.addTextChangedListener(setTextChangedListener(usernameLayout));

        emailLayout = findViewById(R.id.sign_up_email_layout);
        email = findViewById(R.id.sign_up_email);
        email.addTextChangedListener(setTextChangedListener(emailLayout));

        passwordLayout = findViewById(R.id.sign_up_password_layout);
        password = findViewById(R.id.sign_up_password);
        password.addTextChangedListener(setTextChangedListener(passwordLayout));

        Button signUpButton = findViewById(R.id.sign_up_btn);
        signUpButton.setOnClickListener(view -> validate());

        TextView loginNavigation = findViewById(R.id.sign_up_login_navigation);
        loginNavigation.setOnClickListener(view -> navigateToLoginActivity());

        TextView anonymousNavigation = findViewById(R.id.sign_up_anonymous_navigation);
        anonymousNavigation.setOnClickListener(view -> signInAnonymously());
    }

    private TextWatcher setTextChangedListener(TextInputLayout layout) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                layout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };
    }

    @Override
    public void onSignUpSuccess() {
        ActivityNavigator.navigateToHomeActivity(this);
    }

    @Override
    public void onSignUpUsernameExists(String exceptionMessage) {
        usernameLayout.setErrorEnabled(true);
        usernameLayout.setError(exceptionMessage);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSignUpFailure(Exception e) {
        try {
            throw Objects.requireNonNull(e);
        }
        catch (FirebaseAuthUserCollisionException exception) {
            String errorMessage = "The email is already in use by another account.";

            emailLayout.setErrorEnabled(true);
            emailLayout.setError(errorMessage);

            progressBar.setVisibility(View.GONE);
        }
        catch (Exception exception) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(SignUpActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSignInAnonymouslySuccess() {
        ActivityNavigator.navigateToHomeGuestActivity(this);
    }

    @Override
    public void onSignInAnonymouslyFailure(Exception exception) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(SignUpActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void signInAnonymously() {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuthentificationManager.signInAnonymously(this);
    }

    private void validate() {
        String username = this.username.getText().toString().trim();
        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();

        boolean validateUsername = validateUsername(username);
        boolean validateEmail = validateEmail(email);
        boolean validatePassword = validatePassword(password);

        if (validateUsername && validateEmail && validatePassword) {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuthentificationManager.signUp(username, email, password, this);
        }
    }

    private Boolean validateUsername(String username) {
        if (username.isEmpty()) {
            String errorMessage = "The username provided is not valid.";

            usernameLayout.setErrorEnabled(true);
            usernameLayout.setError(errorMessage);

            return false;
        }
        return true;
    }

    private Boolean validateEmail(String email) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            String errorMessage = "The email provided is not valid.";

            emailLayout.setErrorEnabled(true);
            emailLayout.setError(errorMessage);

            return false;
        }
        return true;
    }

    private Boolean validatePassword(String password) {

        if (password.isEmpty()) {
            String errorMessage = "The password provided is not valid.";

            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError(errorMessage);

            return false;
        }

        if (password.length() < 8) {
            String errorMessage = "The password should be at least 8 characters long.";

            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError(errorMessage);

            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            String errorMessage = "The password should contain at least one uppercase letter.";

            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError(errorMessage);

            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            String errorMessage = "The password should contain at least one lowercase letter.";

            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError(errorMessage);

            return false;
        }

        if (!password.matches(".*\\d.*")) {
            String errorMessage = "The password should contain at least one digit.";

            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError(errorMessage);

            return false;
        }

        return true;
    }

    private void navigateToLoginActivity() {
        ActivityNavigator.navigateToLoginActivity(this);

        username.setText("");
        email.setText("");
        password.setText("");
    }
}