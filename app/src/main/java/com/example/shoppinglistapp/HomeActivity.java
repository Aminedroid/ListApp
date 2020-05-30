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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView tvTotalAmount;
    private ImageView ivItemEdit;
    private ImageView ivItemRemove;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    private RecyclerView rv;
    private ConstraintLayout dialog;

    //Global variables
    private String typ;
    private int amt;
    private String not;
    private String post_key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        String id = user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child(id).child("ShoppingList");
        databaseReference.keepSynced(true);

        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btFab = findViewById(R.id.btFab);
        rv = findViewById(R.id.rvHome);
        ivItemEdit = findViewById(R.id.ivItemEdit);
        ivItemRemove = findViewById(R.id.ivItemRemove);

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
                if (!dataSnapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "Empty list", Toast.LENGTH_LONG).show();
                }

                int totalAmount = 0;
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Data data = snap.getValue(Data.class);
                    totalAmount += data.getAmount();
                    String sum = String.valueOf(totalAmount + " DT");
                    tvTotalAmount.setText(sum);
                }
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
            protected void populateViewHolder(final MyViewHolder myViewHolder, final Data data, final int i) {
                myViewHolder.setDate(data.getDate());
                myViewHolder.setType(data.getType());
                myViewHolder.setNote(data.getNote());
                myViewHolder.setAmount(data.getAmount());

                myViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LinearLayout llItemDesc = v.findViewById(R.id.llItemNote);

                        if (llItemDesc.getVisibility() == View.GONE) {
                            llItemDesc.setVisibility(View.VISIBLE);
                        } else if (llItemDesc.getVisibility() == View.VISIBLE) {
                            llItemDesc.setVisibility(View.GONE);
                        }
                    }
                });

                myViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        post_key = getRef(i).getKey();
                        typ = data.getType();
                        not = data.getNote();
                        amt = data.getAmount();
                        updateData();
                        return true;
                    }
                });
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

    public void updateData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View view = inflater.inflate(R.layout.update_data, null);
        final AlertDialog dialog = builder.create();
        dialog.setView(view);


        final EditText etUpdateType = view.findViewById(R.id.etUpdateType);
        final EditText etUpdateAmount = view.findViewById(R.id.etUpdateAmount);
        final EditText etUpdateNote = view.findViewById(R.id.etUpdateNote);

        Button btDataUpdate = view.findViewById(R.id.btDataUpdate);
        Button btDataDelete = view.findViewById(R.id.btDataDelete);

        etUpdateType.setText(typ);
        etUpdateType.setSelection(typ.length());

        etUpdateAmount.setText(String.valueOf(amt));
        etUpdateAmount.setSelection(String.valueOf(amt).length());

        etUpdateNote.setText(not);
        etUpdateNote.setSelection(not.length());

        btDataUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = etUpdateType.getText().toString().trim();
                String amount = etUpdateAmount.getText().toString().trim();
                String note = etUpdateNote.getText().toString().trim();

                int intAmount = Integer.parseInt(amount);

                String date = DateFormat.getDateInstance().format(new Date());

                //Watch the object attribut order
                Data data = new Data(post_key, intAmount, type, note, date);

                databaseReference.child(post_key).setValue(data);
                dialog.dismiss();
            }
        });

        btDataDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(post_key).removeValue();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /* //Commented for later fix and use
    public void updateItem(View view) {
        Data data = new Data();

        /*
        //Suspected error could come form getting key
        post_key = databaseReference.getKey();
        typ = data.getType();
        amt = data.getAmount();
        not = data.getNote();
        updateData();
    }*/

    /*
    public void deleteItem(View view) {
        deleteData();
    }
     //Commented for later fix and use
    public void deleteData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View view = inflater.inflate(R.layout.delete_dialog, null);
        AlertDialog dialog = builder.create();
        dialog.setView(view);
        dialog.show();
    }*/


}
