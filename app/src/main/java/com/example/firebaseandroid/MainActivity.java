package com.example.firebaseandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get view elements by Id
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText password = (EditText) findViewById(R.id.password);
        Button signInButton = findViewById(R.id.button);
        TextView goToSignUp = findViewById(R.id.goToSignUp);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Sign In action
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // Throw exception if sign in has an error
                                    if(!task.isSuccessful()){
                                        try{
                                            throw task.getException();
                                        } catch(FirebaseAuthInvalidCredentialsException ex){
                                            Toast.makeText(getApplicationContext(), "Invalid email or password!", Toast.LENGTH_LONG).show();
                                        } catch(Exception ex){
                                            Toast.makeText(getApplicationContext(), "Unknown exception occured", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    else{
                                        // Get created Firebase user
                                        FirebaseUser user = auth.getCurrentUser();

                                        // Start new activity
                                        Intent intent = new Intent(MainActivity.this, FirestoreActivity.class);
                                        intent.putExtra("uid", user.getUid());
                                        startActivity(intent);
                                    }
                                }
                            });
                }
                catch(IllegalArgumentException ex){
                    Toast.makeText(v.getContext(), "Please fill in your email and password!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Go to sign up
        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });
    }
}
