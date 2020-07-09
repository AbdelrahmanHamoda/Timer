package com.example.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static long intialValue; // in milliseconds
    private long remainingTime;
    private CountDownTimer cdTimer;
    private boolean state = false;

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
                start();
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state){
                    pause();
                }
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
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

    private void start(){
        remainingTime=translateToMilli();
        if(remainingTime==0){
            Toast.makeText(this,"at least one value should be set",Toast.LENGTH_SHORT).show();
        }else{
            cdTimer = new CountDownTimer(remainingTime,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    remainingTime=millisUntilFinished;
                    displayTimer();
                }

                @Override
                public void onFinish() {
                    state=false;
                    start.setEnabled(true);
                    pause.setEnabled(false);
                    reset.setEnabled(false);
                    hours.setText("");
                    minuets.setText("");
                    seconds.setText("");

                }
            }.start();
            state=true;
            start.setEnabled(false);
            pause.setEnabled(true);
            reset.setEnabled(false);
        }
    }

    private void pause(){
        //remainingTime =
        cdTimer.cancel();
        state=false;
        start.setEnabled(true);
        pause.setEnabled(false);
        reset.setEnabled(true);
    }

    private void reset(){
        remainingTime=0;
        displayTimer();
        hours.setText("");
        minuets.setText("");
        seconds.setText("");
        start.setEnabled(true);
        pause.setEnabled(false);
        reset.setEnabled(false);
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