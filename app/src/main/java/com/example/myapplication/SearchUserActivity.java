package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.myapplication.adapter.SearchUserRecyclerAdapter;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;

    SearchUserRecyclerAdapter adapter;

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
        backButton.setOnClickListener(view -> onBackPressed());

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

        // Создаём запрос
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username", searchTerm)
                .whereLessThanOrEqualTo("username",searchTerm+'\uf8ff');

        // Получаем данные из запроса
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class).build();

        // Преобразовываем запрос
        adapter  = new SearchUserRecyclerAdapter(options,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}