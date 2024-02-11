package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.model.ChatMessageModel;
import com.example.myapplication.model.ChatRoomModel;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomId;
    ChatRoomModel chatRoomModel;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Модель пользователя
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        // Индификатор комнаты чата
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currenntUserId(), otherUser.getUserId());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);

        // Кнопка "назад"
        backBtn.setOnClickListener(view -> onBackPressed());
        otherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty()) {
                return;
            }
            // Отправляем сообщение
            sendMessageToUser(message);
        });

        // Создаем комнату чата
        getOrCreateChatroomModel();
    }

    void sendMessageToUser(String message) {

        // Время последнего сообщения и имя пользователя, от которого пришло сообщение
        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currenntUserId());
        // Привязываем к БД
        FirebaseUtil.getChatroomReference(chatroomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currenntUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                messageInput.setText("");
            }
        });
    }

    void getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                // Получаем созданную комнату чата
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if(chatRoomModel == null) {
                    // Создаем впервые
                    chatRoomModel = new ChatRoomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currenntUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    // Добавляем id чата в базу данных
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatRoomModel);
                }
            }
        });
    }
}