package com.example.shoppinglistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class MainActivity extends AppCompatActivity {

    private EditText etMailLogin;
    private EditText etPassLogin;
    private Button btLogin;
    private TextView tvToSingUp;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        etMailLogin = findViewById(R.id.etMailLogin);
        etPassLogin = findViewById(R.id.etPassLogin);
        btLogin = findViewById(R.id.btLogin);
        tvToSingUp = findViewById(R.id.tvToSignUp);

        final LoadingDialog loadingDialog = new LoadingDialog(this);

        tvToSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mail = etMailLogin.getText().toString().trim();
                String password = etPassLogin.getText().toString().trim();

                if (TextUtils.isEmpty(mail)) {
                    etMailLogin.setError("Email field is empty");
                } else if (TextUtils.isEmpty(password)) {
                    etPassLogin.setError("Password field is empty");
                } else if (mail.equals("") && password.equals("")) {
                    Toast.makeText(MainActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                } else {
                    loadingDialog.startLoadingDialog();

                    auth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                try {
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    Toast.makeText(MainActivity.this, "Welcome back " + mail, Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismissDialog();
                                    finish();
                                } catch (Exception e) {
                                    loadingDialog.dismissDialog();
                                    FirebaseAuthException ex = (FirebaseAuthException) task.getException();
                                    Toast.makeText(MainActivity.this, "Login failed: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                loadingDialog.dismissDialog();
                                Toast.makeText(MainActivity.this, "Internet connexion error/Task failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
