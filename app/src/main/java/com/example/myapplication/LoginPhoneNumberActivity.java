package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOtpBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        // Определяем переменные
        countryCodePicker = findViewById(R.id.login_country_code);
        phoneInput = findViewById(R.id.login_phone_number);
        sendOtpBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        progressBar.setVisibility(View.GONE);

        // Проверяем корректность ведённого номера телефона
        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendOtpBtn.setOnClickListener(view -> {
           if(!countryCodePicker.isValidFullNumber()) {
               phoneInput.setError("Номер телефона недействителен!");
               return;
           }
           // Записываем номер телефона под ключом phone и переходим к следующей активности
           Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginOtpActivity.class);
           intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus());
           startActivity(intent);
        });

    }
}