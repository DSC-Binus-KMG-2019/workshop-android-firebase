package com.example.firebaseandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getName();
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get view elements by Id
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText fullname = (EditText) findViewById(R.id.fullname);
        Button signUpButton = findViewById(R.id.button);
        TextView goToSignIn = findViewById(R.id.goToSignIn);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Sign In action
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Throw exception if registration has an error
                                if(!task.isSuccessful()){
                                    try{
                                        throw task.getException();
                                    } catch(Exception ex){
                                        Toast.makeText(getApplicationContext(), "Unknown exception occured", Toast.LENGTH_LONG).show();
                                        Log.w(TAG, "An exception occured: ", ex);
                                    }
                                }
                                else{
                                    // Get created Firebase user
                                    FirebaseUser firebaseUser = auth.getCurrentUser();

                                    // Access a Cloud Firestore instance from your Activity
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                                    // Create a new user with a first and last name
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("uid", firebaseUser.getUid());
                                    user.put("fullname", fullname.getText().toString());

                                    // Add a new document with a generated ID
                                    db.collection("users")
                                        .add(user)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                Toast.makeText(getApplicationContext(), "Successfully registered " + fullname.getText().toString(), Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding document", e);
                                            }
                                        });

                                    // Start new activity
                                    Intent intent = new Intent(RegisterActivity.this, FirestoreActivity.class);
                                    intent.putExtra("uid", firebaseUser.getUid());
                                    startActivity(intent);
                                }
                            }
                        });
                }
                catch(IllegalArgumentException ex){
                    Toast.makeText(v.getContext(), "Please fill in your fullname, email and password!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Go to sign in
        goToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            RegisterActivity.this.finish();
            }
        });
    }
}
