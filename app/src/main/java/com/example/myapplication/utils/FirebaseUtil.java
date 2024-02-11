package com.example.myapplication.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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

    // Метод для перехода к основной активности, минуя авторизацию
    public static boolean isLoggedIn() {
        return currenntUserId() != null;
    }

    // Получаем пользователей
    public static CollectionReference allUserCollectionReference() {
    return FirebaseFirestore.getInstance().collection("users");
    }

    // Получаем id комнаты чата
    public static DocumentReference getChatroomReference(String chatroomid) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomid);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    // Метод для создания уникального id чата
    public static String getChatroomId(String userId1, String userId2) {
        if(userId1.hashCode()<userId2.hashCode()) {
            return userId1+"_"+userId2;
        } else {
            return userId2+"_"+userId1;
        }
    }
}
