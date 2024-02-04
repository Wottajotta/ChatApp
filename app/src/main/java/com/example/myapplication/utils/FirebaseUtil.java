package com.example.myapplication.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    // Получаем ID пользователя
    public static String currenntUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    // Проверяем полученный ID и возвращаем его
    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currenntUserId());
    }
}