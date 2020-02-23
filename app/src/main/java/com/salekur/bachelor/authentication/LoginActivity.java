package com.salekur.bachelor.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.salekur.bachelor.R;

public class LoginActivity extends AppCompatActivity {
    private EditText UserLoginInputEmail, UserLoginInputPassword;
    private Button UserLoginButton;
    private TextView UserLoginRegisterButton, UserLoginForgetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserLoginInputEmail = (EditText) findViewById(R.id.login_edit_email);
        UserLoginInputPassword = (EditText) findViewById(R.id.login_edit_password);
        UserLoginButton = (Button) findViewById(R.id.login_button_signin);
        UserLoginRegisterButton = (TextView) findViewById(R.id.login_text_register);
        UserLoginForgetPasswordButton = (TextView) findViewById(R.id.login_text_forget_password);

        //
        UserLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckLoginInputs();
            }
        });

        UserLoginRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        UserLoginForgetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToForgetPasswordActivity();
            }
        });
    }

    private void CheckLoginInputs() {
        String email = UserLoginInputEmail.getText().toString();
        String password = UserLoginInputPassword.getText().toString();

        if (email.isEmpty()) {
            UserLoginInputEmail.setError("Please enter email");
        } else if (password.isEmpty()) {
            UserLoginInputPassword.setError("Please enter password");
        } else {
            LoginUser(email, password);
        }
    }

    private void LoginUser(String email, String password) {
        //...........
    }

    private void SendUserToRegisterActivity() {
        //........
    }

    private void SendUserToForgetPasswordActivity() {
        //.......
    }
}
