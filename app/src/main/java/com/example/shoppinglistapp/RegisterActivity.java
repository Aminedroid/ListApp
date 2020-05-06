package com.example.shoppinglistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class RegisterActivity extends AppCompatActivity {

    private EditText etMailRegsiter;
    private EditText etPassRegister;
    private Button btRegister;
    private TextView tvToLogin;


    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        etMailRegsiter = findViewById(R.id.etMailRegister);
        etPassRegister = findViewById(R.id.etPassRegister);
        btRegister = findViewById(R.id.btRegister);
        tvToLogin = findViewById(R.id.tvToLogin);

        final LoadingDialog loadingDialog = new LoadingDialog(this);

        tvToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = etMailRegsiter.getText().toString().trim();
                String password = etPassRegister.getText().toString().trim();

                if (TextUtils.isEmpty(mail)) {
                    etMailRegsiter.setError("Email field is empty ...");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etPassRegister.setError("Password field is empty ...");
                    return;
                }

                if (TextUtils.isEmpty(mail) && TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                }

                loadingDialog.startLoadingDialog();

                auth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            try {
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                Toast.makeText(RegisterActivity.this, "User have been successfully registred", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismissDialog();
                                finish();
                            } catch (Exception e) {
                                loadingDialog.dismissDialog();
                                FirebaseAuthException ex = (FirebaseAuthException) task.getException();
                                Toast.makeText(RegisterActivity.this, "User registration failed: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            loadingDialog.dismissDialog();
                            Toast.makeText(RegisterActivity.this, "Internet connexion error/Task failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
