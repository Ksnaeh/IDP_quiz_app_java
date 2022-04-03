package com.example.javaquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuestionActivity extends AppCompatActivity {
    private static final String TAG = "QuestionActivity";

    //for getting sessionid from intent
    String value;

    ArrayList<String> questions;
    ArrayList<String> question_no;
    ArrayList<String> answers;
    ArrayList<String> choice_a;
    ArrayList<String> choice_b;
    ArrayList<String> choice_c;
    ArrayList<String> choice_d;


    private TextView showQuestion;

    private CountDownTimer cdt;
    private int startingindex;
    private int iterations;

    private int isSelected;

    //declare buttons for pressing
    Button button_a;
    Button button_b;
    Button button_c;
    Button button_d;

    Button btnselected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        showQuestion = (TextView)  findViewById(R.id.showQuestion);

        questions = new ArrayList<>();
        question_no = new ArrayList<>();
        answers = new ArrayList<>();

        choice_a = new ArrayList<>();
        choice_b = new ArrayList<>();
        choice_c = new ArrayList<>();
        choice_d = new ArrayList<>();

        button_a = (Button) findViewById(R.id.buttonA);
        button_b = (Button) findViewById(R.id.buttonB);
        button_c = (Button) findViewById(R.id.buttonC);
        button_d = (Button) findViewById(R.id.buttonD);

        isSelected = 0;
        iterations = 2;


        Intent intent = getIntent();
        value = intent.getStringExtra("sessionid"); //if it's a string you stored.
        Log.d(TAG, "qliao " + value);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //DocumentReference docRef = db.collection("users").document("akLaqO1j5ZZuwe1iUbnL");
        db.collection("questions")
                .whereEqualTo("sessionid", value)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());


                                questions.add((String) document.get("question"));
                                question_no.add((String) document.get("questionno"));
                                answers.add((String) document.get("answer"));

                                choice_a.add((String) document.get("a"));
                                choice_b.add((String) document.get("b"));
                                choice_c.add((String) document.get("c"));
                                choice_d.add((String) document.get("d"));

                                Log.d(TAG, "Questions: " + questions.toString());

                                //todo: use 3000 for debugging (default 10000)
                                cdt = new CountDownTimer(3000, 1000) {
                                    public void onTick(long millisRemaining) {
                                        showQuestion.setText("Question: " + questions.get(startingindex));

                                        button_a.setText(choice_a.get(startingindex));
                                        button_b.setText(choice_b.get(startingindex));
                                        button_c.setText(choice_c.get(startingindex));
                                        button_d.setText(choice_d.get(startingindex));

                                        if (isSelected == 0){
                                            button_a.setEnabled(true);
                                            button_a.setBackgroundColor(Color.parseColor("#8FBFE0"));


                                            button_b.setEnabled(true);
                                            button_b.setBackgroundColor(Color.parseColor("#8FBFE0"));

                                            button_c.setEnabled(true);
                                            button_c.setBackgroundColor(Color.parseColor("#8FBFE0"));

                                            button_d.setEnabled(true);
                                            button_d.setBackgroundColor(Color.parseColor("#8FBFE0"));
                                        }

                                    }
                                    public void onFinish() {
                                        showQuestion.setText("End of Quiz!");
                                        //onTick(10000);
                                        countOnce = 0;
                                        if (isSelected == 0){
                                            endQuestion();
                                        }
                                        else{
                                            onUpdate(btnselected);
                                        }

                                        if (startingindex == iterations){
                                            new CountDownTimer(5000, 1000) {
                                                public void onTick(long millisRemaining) {

                                                }
                                                public void onFinish() {
                                                    Log.d(TAG, "GoTo ResultsActivity");

                                                    Intent myIntent = new Intent(QuestionActivity.this, ResultsActivity.class);
                                                    myIntent.putExtra("sessionid", value); //Optional parameters
                                                    QuestionActivity.this.startActivity(myIntent);
                                                }
                                            }.start();
                                        }

                                    }
                                };

                                cdt.start();
                                startingindex = 0;

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        button_a.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        String buttonText = button_a.getText().toString();
                        addResponse(button_a, buttonText);
                    }
                });

        button_b.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        String buttonText = button_b.getText().toString();
                        addResponse(button_b, buttonText);
                    }
                });
        button_c.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        String buttonText = button_c.getText().toString();
                        addResponse(button_c, buttonText);
                    }
                });
        button_d.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        String buttonText = button_d.getText().toString();
                        addResponse(button_d, buttonText);
                    }
                });

    }


    protected void endQuestion(){

        //todo: use 2000 for debugging (default 15000)
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisRemaining) {
                if (startingindex < iterations) {
                    showQuestion.setText("End of Question Results");
                }

                if (button_a.getText().toString().equals(answers.get(startingindex))){
                    button_a.setClickable(false);
                    button_a.setBackgroundColor(Color.parseColor("#A8C256"));
                }
                else {
                    button_a.setBackgroundColor(Color.LTGRAY);
                    button_a.setEnabled(false);
                }
                if (button_b.getText().toString().equals(answers.get(startingindex))){
                    button_b.setClickable(false);
                    button_b.setBackgroundColor(Color.parseColor("#A8C256"));
                }
                else {
                    button_b.setBackgroundColor(Color.LTGRAY);
                    button_b.setEnabled(false);
                }
                if (button_c.getText().toString().equals(answers.get(startingindex))){
                    button_c.setClickable(false);
                    button_c.setBackgroundColor(Color.parseColor("#A8C256"));
                }
                else {
                    button_c.setBackgroundColor(Color.LTGRAY);
                    button_c.setEnabled(false);
                }
                if (button_d.getText().toString().equals(answers.get(startingindex))){
                    button_d.setClickable(false);
                    button_d.setBackgroundColor(Color.parseColor("#A8C256"));
                }
                else {
                    button_d.setBackgroundColor(Color.LTGRAY);
                    button_d.setEnabled(false);
                }
            }
            public void onFinish() {
                if (startingindex < iterations){
                    if (countOnce == 0){
                        startingindex++;
                        countOnce = 1;

                        button_a.setEnabled(true);
                        //button_a.setBackgroundColor(0xFF673AB7);

                        button_b.setEnabled(true);
                        //button_b.setBackgroundColor(0xFF673AB7);

                        button_c.setEnabled(true);
                        //button_c.setBackgroundColor(0xFF673AB7);

                        button_d.setEnabled(true);
                        //button_d.setBackgroundColor(0xFF673AB7);

                        cdt.start();
                    }

                    Log.d(TAG, "counter: " + startingindex);

                }

            }
        }.start();



    }


    int countOnce = 0;
    public void addResponse(Button btn, String btnText){

        button_a.setEnabled(false);
        button_a.setBackgroundColor(Color.LTGRAY);
        button_b.setEnabled(false);
        button_b.setBackgroundColor(Color.LTGRAY);
        button_c.setEnabled(false);
        button_c.setBackgroundColor(Color.LTGRAY);
        button_d.setEnabled(false);
        button_d.setBackgroundColor(Color.LTGRAY);

        isSelected = 1;

        int correctWrong;

        Log.d(TAG, "answers: " + answers.get(startingindex));
        if (btnText.equals(answers.get(startingindex))){
            correctWrong = 1;
            //Toast.makeText(this, "Correct ans!", Toast.LENGTH_LONG).show();

        }
        else {
            correctWrong = 0;
            //Toast.makeText(this, "Wrong ans!", Toast.LENGTH_LONG).show();
        }

        btn.setBackgroundColor(getResources().getColor(R.color.selected));
        btnselected = btn; //set global variable for onUpdate();


        //add responses to db
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference ref = db.collection("responses").document();
        String myId = ref.getId();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sharedPref.getString("userid", "");

        Map<String, Object> response = new HashMap<>();
        response.put("choice", btnText);
        response.put("correctWrong", correctWrong);
        response.put("questionNo", question_no.get(startingindex));
        response.put("responseid", myId);
        response.put("sessionid", value);
        response.put("username", name);

        db.collection("responses").document(myId)
                .set(response)
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

    protected void onUpdate(Button btn){

        new CountDownTimer(15000, 1000) {
            public void onTick(long millisRemaining) {
                if (startingindex < iterations) {
                    showQuestion.setText("End of Question Results");
                }

                //for displaying correct ans
                if (button_a.getText().toString().equals(answers.get(startingindex))){
                    button_a.setClickable(false);
                    button_a.setBackgroundColor(Color.parseColor("#A8C256"));
                    button_a.setTextColor(Color.WHITE);
                }
                else {
                    button_a.setBackgroundColor(Color.LTGRAY);
                    button_a.setTextColor(Color.WHITE);
                    button_a.setEnabled(false);
                }
                if (button_b.getText().toString().equals(answers.get(startingindex))){
                    button_b.setClickable(false);
                    button_b.setBackgroundColor(Color.parseColor("#A8C256"));
                    button_a.setTextColor(Color.WHITE);
                }
                else {
                    button_b.setBackgroundColor(Color.LTGRAY);
                    button_b.setTextColor(Color.WHITE);
                    button_b.setEnabled(false);
                }
                if (button_c.getText().toString().equals(answers.get(startingindex))){
                    button_c.setClickable(false);
                    button_c.setBackgroundColor(Color.parseColor("#A8C256"));
                    button_a.setTextColor(Color.WHITE);
                }
                else {
                    button_c.setBackgroundColor(Color.LTGRAY);
                    button_c.setTextColor(Color.WHITE);
                    button_c.setEnabled(false);
                }
                if (button_d.getText().toString().equals(answers.get(startingindex))){
                    button_d.setClickable(false);
                    button_d.setBackgroundColor(Color.parseColor("#A8C256"));
                    button_a.setTextColor(Color.WHITE);
                }
                else {
                    button_d.setBackgroundColor(Color.LTGRAY);
                    button_d.setTextColor(Color.WHITE);
                    button_d.setEnabled(false);
                }


                //for displaying user's answer
                if (btn.getText().toString().equals(answers.get(startingindex))){
                    btn.setBackgroundColor(Color.parseColor("#A8C256"));
                    //Toast.makeText(this, "Correct ans!", Toast.LENGTH_LONG).show();

                }
                else {
                    btn.setBackgroundColor(Color.parseColor("#EF5F5F"));
                    //Toast.makeText(this, "Wrong ans!", Toast.LENGTH_LONG).show();
                }


            }
            public void onFinish() {
                if (startingindex < iterations){
                    if (countOnce == 0){
                        startingindex++;
                        countOnce = 1;
                        isSelected = 0;


                        button_a.setClickable(true);
                        button_b.setClickable(true);
                        button_c.setClickable(true);
                        button_d.setClickable(true);

                        cdt.start();
                    }

//                    isSelected = 0;
//
//                    cdt.start();

                }
            }
        }.start();
    }



}