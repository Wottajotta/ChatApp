package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class SearchUserActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.search_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        searchInput.requestFocus();

        // Функционал кнопки "назад"
        backButton.setOnClickListener(view -> {
            onBackPressed();
        });

        // Функционал кнопки "поиск"
        searchButton.setOnClickListener(view -> {

            String searchTerm = searchInput.getText().toString();
            //Проверяем правильность введённого имени пользователя
            if(searchTerm.isEmpty() || searchTerm.length()<3) {
                searchInput.setError("Неверное имя пользователя");
                return;
            }
            // Получаем список найденных пользователей
            setupSearchRecyclerView(searchTerm);
        });
    }

    // Метод для отображения всех пользователей в поиске
    void setupSearchRecyclerView(String searchTerm) {

    }
}