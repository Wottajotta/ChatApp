package com.example.myapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.myapplication.model.UserModel;

public class AndroidUtil {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    // Получаем данные о пользователе из базы данных
    public static void passUserModelAsIntent(Intent intent, UserModel model) {
        intent.putExtra("username",model.getUsername());
        intent.putExtra("phone",model.getPhone());
        intent.putExtra("userId",model.getUserId());
    }

    // Создаем пользователя из полученных данных
    public static UserModel getUserModelFromIntent(Intent intent) {
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        return userModel;
    }

}
