package com.example.javaquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {
    private static final String TAG = "ResultsActivity";

    ArrayList<String> questionNos;
    ArrayList<Long> scores;
    ArrayList<String> sessionids;

    TextView scoreText;
    Button buttonEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        questionNos = new ArrayList<>();
        scores = new ArrayList<>();
        sessionids = new ArrayList<>();

        buttonEnd = (Button) findViewById(R.id.buttonReturn);

        scoreText = (TextView) findViewById(R.id.textScore);
        scoreText.setText(" ");


        Intent intent = getIntent();
        String value = intent.getStringExtra("sessionid");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sharedPref.getString("userid", "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("responses")
                .whereEqualTo("sessionid", value)
                .whereEqualTo("username", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());

                                questionNos.add((String) document.get("questionNo"));
                                scores.add((Long) document.get("correctWrong"));


                            }

                            Log.d(TAG, "Question no.: " + questionNos.toString());
                            Log.d(TAG, "Score: "+ scores.toString());

                            displayTotalScore();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        buttonEnd.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {

                        backToHome();
                    }
                });
    }


    public void displayTotalScore(){
        int count = 0;

        for (int i = 0; i < scores.size(); i++){
            if (scores.get(i) == 1){
                count = count + 1;
                Log.d(TAG, "score: " + count);
            }

            if(i == scores.size()-1){
                break;
            }
        }

        Log.d(TAG, "score aft loop: " + count);

        scoreText.setText(String.valueOf(count));

    }

    public void backToHome(){
        Intent newIntent = new Intent(ResultsActivity.this, HomeActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newIntent);
    }
}