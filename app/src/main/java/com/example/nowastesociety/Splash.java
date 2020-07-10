package com.example.nowastesociety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nowastesociety.session.SessionManager;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {

    private Timer timer;
    private ProgressBar progressBar;
    private int i=0;
    TextView loadingbar;
    private static final String TAG = "myapp";
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(getApplicationContext());
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        loadingbar = (TextView)findViewById(R.id.loadingbar);
        loadingbar.setText("Loading...");

        final long period = 30;
        timer=new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //this repeats every 100 ms
                if (i<100){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                    progressBar.setProgress(i);
                    i++;
                }else{
                    //closing the timer
                    timer.cancel();
                    if (sessionManager.isLoggedIn()){

                        Intent intent = new Intent(Splash.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }else {

                        Intent intent = new Intent(Splash.this, Login.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }
        }, 0, period);
    }
}
