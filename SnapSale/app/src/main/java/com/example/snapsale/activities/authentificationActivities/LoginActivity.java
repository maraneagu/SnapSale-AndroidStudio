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
import java.util.Objects;


// HELPFUL LINKS:
// 1). "Login and Registration using Firebase in Android, Android Knowledge", URL: https://www.youtube.com/watch?v=QAKq8UBv4GI&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=12&ab_channel=CodesEasy
// 2). "Login and Signup using Firebase Authentication in Android Studio | Java", Android Knowledge, URL: https://www.youtube.com/watch?v=TStttJRAPhE&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=16&ab_channel=AndroidKnowledge
// 3). "Android Firebase Course #11 : Firebase Anonymous Login | 2024", Android Coding with Harsha, URL: https://www.youtube.com/watch?v=HfVmgKugGCs&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=79&t=780s&ab_channel=AndroidCodingwithHarsha
// 4). "Material Design EditText in Android | Customized TextInputLayout | Android Programming Basics", iRekha Tech Solutions, URL: https://www.youtube.com/watch?v=q-DnUKbGsgA&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=80&t=710s&ab_channel=iRekhaTechSolutions
// 5). "Progress Bar in Android Studio - Mastering Android Course #34", Master Coding, URL: https://www.youtube.com/watch?v=VpnZ1wt5uDA&t=398s&ab_channel=MasterCoding


public class LoginActivity extends AppCompatActivity implements FirebaseAuthentificationManager.LoginCallback,
        FirebaseAuthentificationManager.SignInAnonymouslyCallback {
    private static FirebaseAuthentificationManager firebaseAuthentificationManager;
    private TextInputLayout emailLayout, passwordLayout;
    private EditText email, password;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuthentificationManager = FirebaseAuthentificationManager.getInstance();
        initializeViews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.login_progress_bar);

        emailLayout = findViewById(R.id.login_email_layout);
        email = findViewById(R.id.login_email);
        email.addTextChangedListener(setTextChangedListener(emailLayout));

        passwordLayout = findViewById(R.id.login_password_layout);
        password = findViewById(R.id.login_password);
        password.addTextChangedListener(setTextChangedListener(passwordLayout));

        Button loginButton = findViewById(R.id.login_btn);
        loginButton.setOnClickListener(view -> validate());

        TextView signUpNavigation = findViewById(R.id.login_sign_up_navigation);
        signUpNavigation.setOnClickListener(view -> ActivityNavigator.navigateToSignUpActivity(this));

        TextView anonymousNavigation = findViewById(R.id.login_anonymous_navigation);
        anonymousNavigation.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuthentificationManager.signInAnonymously(this);
        });
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
    public void onLoginSuccess() {
        ActivityNavigator.navigateToHomeActivity(this);
    }

    @Override
    public void onLoginFailure(Exception e) {
        try {
            throw Objects.requireNonNull(e);
        }
        catch (FirebaseAuthInvalidCredentialsException exception) {
            String errorMessage = "The email or the password is not correct.";

            emailLayout.setErrorEnabled(true);
            emailLayout.setError(errorMessage);

            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError(errorMessage);

            progressBar.setVisibility(View.GONE);
        }
        catch (Exception exception) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSignInAnonymouslySuccess() {
        ActivityNavigator.navigateToHomeGuestActivity(this);
    }

    @Override
    public void onSignInAnonymouslyFailure(Exception exception) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void validate() {
        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();

        boolean validateEmail = validateEmail(email);
        boolean validatePassword = validatePassword(password);

        if (validateEmail && validatePassword) {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuthentificationManager.login(email, password, this);
        }
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
        return true;
    }
}