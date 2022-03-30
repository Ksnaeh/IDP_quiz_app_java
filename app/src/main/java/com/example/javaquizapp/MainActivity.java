package com.example.javaquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Button mButton;
    EditText mUsername;
    EditText mPassword;


    ArrayList<String> userid;
    ArrayList<String> username;
    ArrayList<String> password;

    //todo: declare java's version of sessionstorage


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button)findViewById(R.id.buttonLogin);
        mUsername = (EditText)findViewById(R.id.loginName);
        mPassword = (EditText)findViewById(R.id.loginPassword);

        userid = new ArrayList<>();
        username = new ArrayList<>();
        password = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //DocumentReference docRef = db.collection("users").document("akLaqO1j5ZZuwe1iUbnL");
        db.collection("users")
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());

                        //todo: use array to store users here
                        userid.add((String) document.get("id"));
                        username.add((String) document.get("username"));
                        password.add((String) document.get("password"));
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        mButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        authenticate();
                    }
                });



    }


    public void authenticate(){
        for (int i = 0; i < userid.size(); i++){
            if (mUsername.getText().toString().equals(username.get(i)) && mPassword.getText().toString().equals(password.get(i))){
                Intent myIntent = new Intent(MainActivity.this, HomeActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userid", username.get(i));
                editor.apply();

                MainActivity.this.startActivity(myIntent);
                return;
            }
            else if (i == userid.size()-1 && !mUsername.getText().toString().equals(username.get(i)) && !mPassword.getText().toString().equals(password.get(i))){
                Toast.makeText(this, "Invalid username / password", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
}