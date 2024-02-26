package com.example.myapplication.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.utils.AndroidUtil;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginOtpActivity extends AppCompatActivity {

    String phoneNumber;
    Long timeoutSeconds = 30L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;

    EditText otpInput;
    ProgressBar progressBar;
    Button nextBtn;
    TextView resendOtpTextView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        // Инициализация переменных
        otpInput = findViewById(R.id.login_otp);
        nextBtn = findViewById(R.id.login_next_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTextView = findViewById(R.id.resend_otp_textview);


        // Получаем номер телефона из прошлой активности
        phoneNumber = Objects.requireNonNull(getIntent().getExtras()).getString("phone");

        sendOtp(phoneNumber);

        // Настройки кнопки далее
        nextBtn.setOnClickListener(view -> {
            String enteredOtp = otpInput.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
            signIn(credential);
            setInProgress(true);
        });

        resendOtpTextView.setOnClickListener(view -> sendOtp(phoneNumber));

    }

    // Проверка и отправка одноразового кода на телефон
    void sendOtp(String phoneNumber) {
        mAuth.setLanguageCode("ru");
        startResendTimer();
        setInProgress(true);
        //Задаем параметры проверки входа
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            // Входим в систему при успешной проверке
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            // Сообщаем о неудачной попытке входа
                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtil.showToast(getApplicationContext(), "Не удалось отправить код!");
                                setInProgress(false);
                            }

                            // Высылаем одноразовый код на введённый номер телефона
                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                AndroidUtil.showToast(getApplicationContext(), "Код отправлен!");
                                setInProgress(false);
                            }
                        });
        // Верефицируем номер телефона
        PhoneAuthProvider.verifyPhoneNumber(builder.build());
    }

    // Отображение индикатора загрузки/кнопки
    void setInProgress(boolean inProgress) {
        if(inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    // Авторизация и переход в следующую активность
    void signIn(PhoneAuthCredential phoneAuthCredential) {
        setInProgress(true);
        //Проверяем одноразовый код и переходим к следующей активности
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            setInProgress(false);
            if(task.isSuccessful()) {
                Intent intent = new Intent(LoginOtpActivity.this, LoginUsernameActivity.class);
                intent.putExtra("phone", phoneNumber);
                startActivity(intent);
            }else{
                AndroidUtil.showToast(getApplicationContext(),"Не удалось проверить одноразовый код!");
            }
        });
    }

    // Настройка таймера для повторной отправки кода
    void startResendTimer(){
        resendOtpTextView.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                resendOtpTextView.setText("Отправить код повторно через "+ timeoutSeconds +" секунд");
                if(timeoutSeconds<=0){
                    timeoutSeconds=30L;
                    timer.cancel();
                    runOnUiThread(() -> resendOtpTextView.setEnabled(true));
                }
            }
        }, 0, 1000);
    }
}