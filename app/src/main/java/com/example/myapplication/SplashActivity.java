package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.myapplication.login.LoginPhoneNumberActivity;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Проверяем, вошёл ли пользователь
        if(FirebaseUtil.isLoggedIn()  && getIntent().getExtras()!=null){
            //Из уведомления
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtil.allUserCollectionReference().document(userId).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    UserModel model = task.getResult().toObject(UserModel.class);

                    Intent mainIntent = new Intent(this, MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(mainIntent);

                    Intent intent = new Intent(this, ChatActivity.class);
                    AndroidUtil.passUserModelAsIntent(intent, model);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else {
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
}