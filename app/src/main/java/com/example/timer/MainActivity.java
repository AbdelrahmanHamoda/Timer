package com.example.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private long remainingTime=0;
    private long endTime;
    private CountDownTimer cdTimer;
    private boolean state;

    private Button start;
    private Button pause;
    private Button reset;
    private TextView timer;
    private EditText hours;
    private EditText minuets;
    private EditText seconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_timer();
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        // bunch of code to save data of running timer even if app is closed
        SharedPreferences pref = getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("rem",remainingTime);
        editor.putLong("end",endTime);
        editor.putBoolean("state",state);
        editor.apply();

        if(cdTimer!=null){
            cdTimer.cancel();
            cdTimer=null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        hours.setEnabled(true);
        minuets.setEnabled(true);
        seconds.setEnabled(true);

        SharedPreferences pref = getSharedPreferences("prefs", MODE_PRIVATE);
        remainingTime = pref.getLong("rem",0);
        endTime = pref.getLong("end",0);
        state = pref.getBoolean("state",false);

        Log.v("state", String.valueOf(state));
        Log.v("remaining from on start",String.valueOf(remainingTime));

        if(state){
            // test if timer is able to continue in background or not
            remainingTime = endTime - System.currentTimeMillis();
            if(remainingTime<0){
                state=false;
                remainingTime = 0;
                displayTimer();
                start.setText(R.string.start);
                start.setEnabled(true);
                pause.setEnabled(false);
                reset.setEnabled(false);
                hours.setEnabled(false);
                minuets.setEnabled(false);
                seconds.setEnabled(false);
                hours.setText("");
                minuets.setText("");
                seconds.setText("");
                if(cdTimer!=null){
                    cdTimer.cancel();
                    cdTimer=null;
                }
            }else {
                start_timer();
            }
        }
    }

    public void start_timer(){
        Log.v("start", String.valueOf(remainingTime));
        if(remainingTime==0){ // it didn't come from another timer
            remainingTime = translateToMilli(); // getting input from user and storing it in remainingTime
            Log.v("user input","taking user input");
        }
        // testing user input
        if(remainingTime<1000){
            Toast.makeText(this,"at least one field mustn't be empty",Toast.LENGTH_SHORT).show();
            Log.v("remaining", String.valueOf(remainingTime));
        }else{
            state=true;
            pause.setEnabled(true);
            start.setEnabled(false);
            start.setText(R.string.resume);
            reset.setEnabled(false);
            hours.setEnabled(false);
            minuets.setEnabled(false);
            seconds.setEnabled(false);
            hours.setText("");
            minuets.setText("");
            seconds.setText("");
            endTime = System.currentTimeMillis() + remainingTime; // detecting value of end time according to the system
            // initializing timer
            cdTimer = new CountDownTimer(remainingTime,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    remainingTime = millisUntilFinished;
                    displayTimer();
                }

                @Override
                public void onFinish() {
                    state=false;
                    remainingTime=0;
                    pause.setEnabled(false);
                    start.setEnabled(true);
                    start.setText(R.string.start);
                    reset.setEnabled(false);
                    hours.setEnabled(true);
                    minuets.setEnabled(true);
                    seconds.setEnabled(true);
                }
            }.start();
        }
    }

    public void pause(){
        state=false;
        cdTimer.cancel();
        cdTimer=null;
        pause.setEnabled(false);
        start.setEnabled(true);
        reset.setEnabled(true);
    }

    public void reset(){
        if(cdTimer!=null){
            cdTimer.cancel();
            cdTimer=null;
        }
        timer.setText("00:00:00");
        state = false;
        remainingTime = 0;
        reset.setEnabled(false);
        pause.setEnabled(false);
        start.setEnabled(true);
        start.setText(R.string.start);
        hours.setEnabled(true);
        minuets.setEnabled(true);
        seconds.setEnabled(true);
        hours.setText("");
        minuets.setText("");
        seconds.setText("");
    }

    public void setupUI(){
        start=findViewById(R.id.start);
        pause=findViewById(R.id.pause);
        reset=findViewById(R.id.reset);
        timer=findViewById(R.id.timer);
        hours=findViewById(R.id.hour);
        minuets=findViewById(R.id.min);
        seconds=findViewById(R.id.sec);
    }

    private long translateToMilli(){
        int hour;
        int minutes;
        int second;

        if(hours.getText().toString().equals("")){
            hour=0;
        }else{
           hour=Integer.parseInt(hours.getText().toString());
        }

        if(minuets.getText().toString().equals("")){
            minutes=0;
        }else{
            minutes = Integer.parseInt(minuets.getText().toString());
        }

        if(seconds.getText().toString().equals("")){
            second=0;
        }else{
            second=Integer.parseInt(seconds.getText().toString());
        }

        long total = hour*60*60*1000 + minutes*60*1000 + second*1000;
        return total;
    }

    private void displayTimer(){
        int hours = (int) remainingTime/1000/60/60;
        int minutes;
        if(hours>0){
            minutes = (int) remainingTime/1000/60 - hours*60;
        }else{
            minutes = (int) remainingTime/1000/60;
        }

        int sec = (int) remainingTime/1000%60;
        String timeFormat = String.format(Locale.getDefault(),"%02d:%02d:%02d",hours,minutes,sec);
        timer.setText(timeFormat);
    }
}