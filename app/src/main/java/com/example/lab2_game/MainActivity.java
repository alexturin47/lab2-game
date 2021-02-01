package com.example.lab2_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button start;
    private EditText name;
    private EditText bugsNum;
    private TextView scores;
    private TextView record;
    SharedPreferences sPref;
    private DrawView drawView;

    private int score;

    final String LOGIN = "Login";
    final String SCORES = "Scores";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.btStart);
        name = findViewById(R.id.name);
        bugsNum = findViewById(R.id.bugs);
        scores = findViewById(R.id.scores);
        record = findViewById(R.id.record);

        start.setOnClickListener(this);

        loadSettings();

    }

    private void loadSettings() {
        sPref = getPreferences(MODE_PRIVATE);
        String s = sPref.getString(LOGIN,"");
        int sc = sPref.getInt(SCORES, 0);
        name.setText(s);
        record.setText(""+sc);
    }

    private void saveSettings() {
        sPref = getPreferences(MODE_PRIVATE);
        if(name.getText().length() != 0){
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(LOGIN, name.getText().toString());
            if(score > Integer.valueOf(record.getText().toString())){
                ed.putInt(SCORES, score);
            }
            ed.commit();
        }
    }

    @Override
    public void onClick(View v) {

        //setContentView(R.layout.activity_main);

        drawView = new DrawView(this);
        setContentView(drawView);
        //drawView.setZOrderOnTop(true);

    }

    @Override
    protected void onDestroy() {
        saveSettings();

        super.onDestroy();

    }
}