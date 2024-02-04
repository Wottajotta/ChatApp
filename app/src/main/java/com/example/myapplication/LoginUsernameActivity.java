package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.FirebaseUtil;
import com.google.firebase.Timestamp;

import java.util.Objects;

public class LoginUsernameActivity extends AppCompatActivity {

    EditText usernameInput;
    Button letMeInBtn;
    ProgressBar progressBar;
    String phoneNumber;
    UserModel userModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);

        usernameInput = findViewById(R.id.login_username);
        letMeInBtn = findViewById(R.id.login_let_me_in_btn);
        progressBar = findViewById(R.id.login_progress_bar);


        phoneNumber = Objects.requireNonNull(getIntent().getExtras()).getString("phone");
        getUserName();

        letMeInBtn.setOnClickListener(view -> setUsername());

    }

    // Метод для ввода имени пользователя впервые
    void setUsername() {
        String username = usernameInput.getText().toString();
        if(username.isEmpty() || username.length() <3){
            usernameInput.setError("Имя пользователя должно быть не менее 3-х символов!");
            return;
        }
        // Проверяем, существует ли пользователь, если нет - создаём
        setInProgress(true);
        if(userModel !=null) {
            userModel.setUsername(username);
        }else{
            userModel = new UserModel(phoneNumber,username, Timestamp.now());
        }

        // Заносим нового пользователя в базу данных
        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if(task.isSuccessful()) {
                Intent intent = new Intent(LoginUsernameActivity.this, MainActivity.class);
                // При повторном входе в приложение, пропускаем окна регистрации и переходим в основную активность
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }


    // Получение имени пользователя из базы данных
    void getUserName(){
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if(task.isSuccessful()){
               userModel = task.getResult().toObject(UserModel.class);
               if(userModel != null) {
                   usernameInput.setText(userModel.getUsername());
               }
            }
        });
    }


    // Отображение индикатора загрузки/кнопки
    void setInProgress(boolean inProgress) {
        if(inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            letMeInBtn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            letMeInBtn.setVisibility(View.VISIBLE);
        }
    }
}