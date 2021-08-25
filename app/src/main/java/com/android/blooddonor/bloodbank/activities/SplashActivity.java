package com.android.blooddonor.bloodbank.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.blooddonor.bloodbank.R;

public class SplashActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);

            Thread td = new Thread(){
                public void run(){
                    try {
                        sleep(2200);
                    }catch (Exception exp){
                        exp.printStackTrace();
                    }finally {
                        Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            };td.start();

        }
    }