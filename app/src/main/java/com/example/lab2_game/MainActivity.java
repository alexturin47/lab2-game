package com.example.lab2_game;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
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
    private int RECORD;
    SharedPreferences sPref;

    private int score;

    String LOGIN;
    public static int BUG_COUNT = 10;

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
        LOGIN = sPref.getString("LOGIN","");
        RECORD = sPref.getInt("SCORES", 0);
        name.setText(LOGIN);
        record.setText(""+RECORD);
    }

    private void saveSettings() {
        sPref = getPreferences(MODE_PRIVATE);
        if(name.getText().length() != 0){
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("LOGIN", name.getText().toString());
            if(score > RECORD){
                ed.putInt("SCORES", score);
            }
            ed.commit();
        }
    }

    @Override
    public void onClick(View v) {
        int bug;

        if(v.getId() == R.id.btStart){
            if(bugsNum.getText().length() == 0){
                bug = 10;
                bugsNum.setText(""+bug);
            } else bug = Integer.parseInt(bugsNum.getText().toString());
            if(bug <= 0 || bug > 50) bug = BUG_COUNT;
            if(name.getText().length() == 0){
                Toast.makeText(this,"Для старта игры необходимо ввести имя игрока", Toast.LENGTH_SHORT).show();
            } else {
                saveSettings();
                Intent intent = new Intent(this, DrawActivity.class);
                intent.putExtra("bugs", bug);
                startActivityForResult(intent, 444);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 444){
            loadSettings();
            score = data.getIntExtra("SCORE", 0);
            scores.setText(""+score);
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        saveSettings();
        super.onDestroy();

    }

}