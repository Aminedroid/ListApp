package com.example.shoppinglistapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Binder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    //private Toolbar toolbar;
    private FloatingActionButton btFab;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btFab = findViewById(R.id.btFab);

        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        String id = user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("ShoppingList").child(id);






        btFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog();
            }
        });
    }

    private void customDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.input_data, null));
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();
        dialog.show();

        final EditText etDataType = findViewById(R.id.etDataType);
        final EditText etDataAmount = findViewById(R.id.etDataAmount);
        final EditText etDataNote = findViewById(R.id.etDataNote);
        Button btDataAdd = findViewById(R.id.btDataAdd);

        btDataAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = etDataType.getText().toString().trim();
                String amount = etDataAmount.getText().toString().trim();
                String note = etDataNote.getText().toString().trim();
                int amt = Integer.parseInt(amount);

                if (TextUtils.isEmpty(type)) {
                    etDataType.setError("Type field is empty ...");
                    return;
                }

                if (TextUtils.isEmpty(amount)) {
                    etDataAmount.setError("Amount field is empty ...");
                    return;
                }

                if (TextUtils.isEmpty(note)) {
                    etDataNote.setError("Note field is empty ...");
                    return;
                }


                String id = databaseReference.push().getKey();
                String data = DateFormat.getDateInstance().format(new Date());

                dialog.dismiss();
            }
        });

    }
}
