package com.example.javaquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    EditText newUser;
    EditText newPass;
    EditText newEm;

    Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        newUser = (EditText) findViewById(R.id.newUsername);
        newPass = (EditText) findViewById(R.id.newPassword);
        newEm = (EditText) findViewById(R.id.newEmail);

        signUp = (Button) findViewById(R.id.signUpBtn);

        signUp.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        registerUser();
                    }
                });

    }

    public void registerUser(){

        String uname = newUser.getText().toString();
        String pword = newPass.getText().toString();
        String email = newEm.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference ref = db.collection("users").document();
        String myId = ref.getId();

        Map<String, Object> newuser = new HashMap<>();
        newuser.put("role", "student");
        newuser.put("email", email);
        newuser.put("id", myId);
        newuser.put("username", uname);
        newuser.put("password", pword);

        Log.d(TAG, "new user details =>" + newuser.toString());


        db.collection("users").document(myId)
                .set(newuser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }
}