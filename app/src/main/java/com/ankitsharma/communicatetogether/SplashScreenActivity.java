package com.ankitsharma.communicatetogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_splash_screen);
        getSupportActionBar ().hide ();
        this.getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Thread td = new Thread (){

            @Override
            public void run() {
                try {
                    sleep (3000);
                }catch (Exception e){
                    e.printStackTrace ();
                }finally {
                    Intent i = new Intent (SplashScreenActivity.this,LoginActivity.class);
                    startActivity (i);
                }
            }
        };td.start ();

    }
}