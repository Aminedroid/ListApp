package com.example.shoppinglistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shoppinglistapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    //private Toolbar toolbar;
    private FloatingActionButton btFab;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    private RecyclerView rv;
    private ConstraintLayout dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        String id = user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child(id).child("ShoppingList");
        databaseReference.keepSynced(true);

        btFab = findViewById(R.id.btFab);
        rv = findViewById(R.id.rvHome);

        LinearLayoutManager llManager = new LinearLayoutManager(this);

        llManager.setStackFromEnd(true);
        llManager.setReverseLayout(true);

        rv.setHasFixedSize(true);
        rv.setLayoutManager(llManager);

        dialog = findViewById(R.id.dialog);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog();
            }
        });
    }

    private void customDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View view = inflater.inflate(R.layout.input_data, null);

        final AlertDialog dialog = builder.create();

        dialog.setView(view);

        final EditText etDataType = view.findViewById(R.id.etDataType);
        final EditText etDataAmount = view.findViewById(R.id.etDataAmount);
        final EditText etDataNote = view.findViewById(R.id.etDataNote);
        Button btDataAdd = view.findViewById(R.id.btDataAdd);


        btDataAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = etDataType.getText().toString().trim();
                String amount = etDataAmount.getText().toString().trim();
                String note = etDataNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    etDataType.setError("Type field is empty ...");
                } else if (TextUtils.isEmpty(amount)) {
                    etDataAmount.setError("Amount field is empty ...");
                } else if (TextUtils.isEmpty(note)) {
                    etDataNote.setError("Note field is empty ...");
                } else {
                    int amnt = Integer.parseInt(amount);
                    String id = databaseReference.push().getKey();
                    String date = DateFormat.getDateInstance().format(new Date());
                    Data data = new Data(id, amnt, type, note, date);

                    databaseReference.child(id).setValue(data);

                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (
                        Data.class,
                        R.layout.item_data,
                        MyViewHolder.class,
                        databaseReference
                ) {
            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, Data data, int i) {
                myViewHolder.setDate(data.getDate());
                myViewHolder.setType(data.getType());
                myViewHolder.setNote(data.getNote());
                myViewHolder.setAmount(data.getAmount());
            }

        };
        rv.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View view;

        public MyViewHolder(View item) {
            super(item);
            view = item;
        }

        public void setType(String typ) {
            TextView type = view.findViewById(R.id.tvItemType);
            type.setText(typ);
        }

        public void setNote(String nt) {
            TextView note = view.findViewById(R.id.tvItemNote);
            note.setText(nt);
        }

        public void setDate(String dt) {
            TextView date = view.findViewById(R.id.tvItemDate);
            date.setText(dt);
        }

        public void setAmount(int amt) {
            TextView amount = view.findViewById(R.id.tvItemAmount);
            String value = String.valueOf(amt);
            amount.setText(value);
        }

    }


}
