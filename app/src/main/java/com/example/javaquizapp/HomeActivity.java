package com.example.javaquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    Button mButton;
    EditText mEdit;

    //TODO: declare array for sessions table

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mButton = (Button)findViewById(R.id.buttonSession);
        mEdit = (EditText)findViewById(R.id.editSessionId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("sessions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());

                        //TODO: insert retrieved sessions into array
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

        //TODO: match input with db's session id here

        Intent myIntent = new Intent(HomeActivity.this, QuestionActivity.class);
        myIntent.putExtra("sessionid", mEdit.getText().toString()); //Optional parameters
        HomeActivity.this.startActivity(myIntent);
    }
}