package com.salekur.bachelor.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.salekur.bachelor.MainActivity;
import com.salekur.bachelor.R;
import com.salekur.bachelor.SupportActivity;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout UserLoginInputEmail, UserLoginInputPassword;
    private MaterialButton UserLoginButton;
    private TextView UserLoginRegisterButton, UserLoginNeedHelpButton;

    private FirebaseAuth mAuth;
    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserLoginInputEmail = (TextInputLayout) findViewById(R.id.login_edit_email);
        UserLoginInputPassword = (TextInputLayout) findViewById(R.id.login_edit_password);
        UserLoginButton = (MaterialButton) findViewById(R.id.login_button);
        UserLoginRegisterButton = (TextView) findViewById(R.id.login_text_register);
        UserLoginNeedHelpButton = (TextView) findViewById(R.id.login_text_need_help);

        mAuth = FirebaseAuth.getInstance();
        LoadingBar = new ProgressDialog(this);

        // on click handler for buttons
        UserLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckUserLoginInputs();
            }
        });

        UserLoginRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        UserLoginNeedHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToSupportActivity();
            }
        });
    }

    private void CheckUserLoginInputs() {
        String email = UserLoginInputEmail.getEditText().getText().toString();
        String password = UserLoginInputPassword.getEditText().getText().toString();

        if (email.isEmpty()) {
            UserLoginInputEmail.setError("Please enter email");
        } else if (password.isEmpty()) {
            UserLoginInputPassword.setError("Please enter password");
        } else {
            LoginUserWithEmailPassword(email, password);
        }
    }

    private void LoginUserWithEmailPassword(String email, String password) {
        LoadingBar.setMessage("Logging in");
        LoadingBar.setCanceledOnTouchOutside(false);
        LoadingBar.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    LoadingBar.dismiss();
                    SendUserToMainActivity();
                } else {
                    LoadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SendUserToMainActivity() {
        Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
        MainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
    }

    private void SendUserToRegisterActivity() {
        Intent RegisterIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(RegisterIntent);
    }

    private void SendUserToSupportActivity() {
        Intent SupportIntent = new Intent(LoginActivity.this, SupportActivity.class);
        startActivity(SupportIntent);
    }
}
