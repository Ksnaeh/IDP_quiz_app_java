package com.example.javaquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    Button mButton;
    EditText mEdit;


    ArrayList<String> sessionids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionids = new ArrayList<>();

        mButton = (Button)findViewById(R.id.buttonSession);
        mEdit = (EditText)findViewById(R.id.editSessionId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("sessions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());


                        sessionids.add((String) document.get("sessionid"));
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
                        buttonClick();
                    }
                });
    }

    public void buttonClick(){

        for (int x = 0; x < sessionids.size(); x++){
            Log.d(TAG, "Array: " + sessionids.toString());

            if (mEdit.getText().toString().equals(sessionids.get(x))){
                Intent myIntent = new Intent(HomeActivity.this, QuestionActivity.class);
                myIntent.putExtra("sessionid", mEdit.getText().toString()); //Optional parameters
                HomeActivity.this.startActivity(myIntent);
            }
            else {
                Toast.makeText(this, "Session not found", Toast.LENGTH_LONG).show();
            }
        }
    }
}