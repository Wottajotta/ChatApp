package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.adapter.ChatRecyclerAdapter;
import com.example.myapplication.model.ChatMessageModel;
import com.example.myapplication.model.ChatRoomModel;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomId;
    ChatRoomModel chatRoomModel;
    ChatRecyclerAdapter adapter;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView;

    ImageView messageCounter;

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
        imageView = findViewById(R.id.profile_pic_image_view);
        messageCounter = findViewById(R.id.message_counter);

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(this, uri, imageView);
                    }
                });

        // Кнопка "назад"
        backBtn.setOnClickListener((View view) -> onBackPressed());
        otherUsername.setText(otherUser.getUsername());

        // Отправка сообщения
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
        setupChatRecyclerView();
    }

    void setupChatRecyclerView() {
        // Создаём запрос
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        // Получаем данные из запроса
        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        // Преобразовываем запрос
        adapter  = new ChatRecyclerAdapter(options,getApplicationContext());
        //Выстраиваем сообщения в правильном порядке
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        //Отрисовываем
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        // Прокрутка сообщений
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessageToUser(String message) {

        // Время последнего сообщения и имя пользователя, от которого пришло сообщение
        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currenntUserId());
        chatRoomModel.setLastMessage(message);
        // Привязываем к БД
        FirebaseUtil.getChatroomReference(chatroomId).set(chatRoomModel);

        // Отправляем сообщение
        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currenntUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                messageInput.setText("");
                sendNotification(message);
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

    // Метод отправки уведомлений
    void sendNotification(String message) {

       FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
          if(task.isSuccessful()){
              UserModel currentUser = task.getResult().toObject(UserModel.class);
              try {
                  JSONObject jsonObject = new JSONObject();

                  // Задаём форму уведомления
                  JSONObject notificationObj = new JSONObject();
                  assert currentUser != null;
                  notificationObj.put("title", currentUser.getUsername());
                  notificationObj.put("body",message);

                  // Получаем данные о пользователе
                  JSONObject dataObj = new JSONObject();
                  dataObj.put("userId", currentUser.getUserId());

                  // Созадем json-объект с уведомлением
                  jsonObject.put("notification", notificationObj);
                  jsonObject.put("data", dataObj);
                  jsonObject.put("to", otherUser.getFcmToken());

                  callApi(jsonObject);

              }catch (Exception e) {
                  throw new RuntimeException(e);
              }
          }
       });

    }

    // Метод вызова API для уведомлений
    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer AAAAdZCnOVI:APA91bEMkqA6UlrKSm83vpSkE5kQdwm0jtVp5UmEKpwk1sp7ImCsRMDaBnVBgvrSK4bAcRYTfRpYLEIsbAqIvVe-QiHQzRXNP_3s4JGugfunMl4mcEo065OVkjkt7RyGVOTtH-BtTfPi")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

            }
        });
    }

}