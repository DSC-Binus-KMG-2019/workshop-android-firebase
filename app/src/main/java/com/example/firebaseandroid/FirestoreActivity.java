package com.example.firebaseandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FirestoreActivity extends AppCompatActivity {

    private static final String TAG = FirestoreActivity.class.getName();
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firestore);

        // Initialize intent
        Intent intent = getIntent();

        // Get UID from Intent
        String uid = intent.getStringExtra("uid");

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get data for TextView
        final TextView tv = findViewById(R.id.title);
        db.collection("users")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                tv.setText("Welcome, " + document.getData().get("fullname").toString());
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        // Sign Out on click
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize FirebaseAuth
                auth = FirebaseAuth.getInstance();
                auth.signOut();

                // Move back to login page
                Intent intent = new Intent(FirestoreActivity.this, MainActivity.class);
                startActivity(intent);
                FirestoreActivity.this.finish();
            }
        });
    }
}
