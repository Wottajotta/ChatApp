package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.myapplication.utils.FirebaseUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Переходим от экрана загрузки к экрану номера телефона
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Проверяем, авторизирован ли пользователь
            if(FirebaseUtil.isLoggedIn()){
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }else{
                startActivity(new Intent(SplashActivity.this, LoginPhoneNumberActivity.class));
            }
            finish();
        }, 1000);
    }
}