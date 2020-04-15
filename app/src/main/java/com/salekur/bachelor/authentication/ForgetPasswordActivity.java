package com.salekur.bachelor.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.salekur.bachelor.R;

public class ForgetPasswordActivity extends AppCompatActivity {
    private TextInputLayout XmlUserInputEmail;
    private MaterialButton XmlButtonSendEmail;

    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        XmlUserInputEmail = (TextInputLayout) findViewById(R.id.forget_password_edit_email);
        XmlButtonSendEmail = (MaterialButton) findViewById(R.id.forget_password_button_send_email);

        LoadingBar = new ProgressDialog(this);

        XmlButtonSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckEmailAndSendVerificationLink();
            }
        });
    }

    private void CheckEmailAndSendVerificationLink() {
        String email = XmlUserInputEmail.getEditText().getText().toString();

        if (email.isEmpty()) {
            XmlUserInputEmail.setError("Enter email");
        } else {
            LoadingBar.setMessage("Sending email");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        LoadingBar.dismiss();
                        SendUserToLoginActivity();
                    } else {
                        LoadingBar.dismiss();
                        Toast.makeText(ForgetPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void SendUserToLoginActivity() {
        Intent LoginIntent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
        LoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
    }
}
