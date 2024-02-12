package com.example.myapplication.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.List;

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

    // Получаем все комнаты чата
    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if(userIds.get(0).equals(FirebaseUtil.currenntUserId())){
            // Возвращаем первого пользователя (себя)
            return allUserCollectionReference().document(userIds.get(1));
        } else {
            // Возвращаем второго пользователя
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }
}
