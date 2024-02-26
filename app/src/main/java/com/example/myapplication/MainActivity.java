package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.myapplication.fragments.ChatFragment;
import com.example.myapplication.fragments.ProfileFragment;
import com.example.myapplication.utils.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;
    ChatFragment chatFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchButton = findViewById(R.id.main_search_btn);

        // Метод для перехода в поиск пользователей (по нажатию кнопки)
        searchButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, SearchUserActivity.class)));

        // Переход на выбранные пункты меню (чат/профиль)
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId()==R.id.menu_chat){
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
            }
            if(item.getItemId()==R.id.menu_profile){
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment).commit();
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);

        getFCMToken();
    }

    // Получаем токен для уведомлений
    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                // Сгенерировали токен для уведомления
                FirebaseUtil.currentUserDetails().update("fcmToken", token);
            }
        });
    }
}