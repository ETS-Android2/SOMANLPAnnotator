package com.cyph.somanlpannotator.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.cyph.somanlpannotator.HelperMethods.EmailUtility;
import com.cyph.somanlpannotator.R;

public class WelcomeActivity extends AppCompatActivity {

    private static final String SHARED_PREFERENCES_EMAIL_KEY = "email";

    private EditText emailEditText;
    private ImageButton continueImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        emailEditText = findViewById(R.id.email);
        continueImageButton = findViewById(R.id.send_email);
        Button skipButton = findViewById(R.id.skip);

        SharedPreferences sharedPreferences = this.getSharedPreferences("Soma", MODE_PRIVATE);
        emailEditText.setText(sharedPreferences.getString(SHARED_PREFERENCES_EMAIL_KEY, ""));

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    continueImageButton.setVisibility(View.VISIBLE);
                } else {
                    continueImageButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        continueImageButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();

            if (email.equals("")) {
                return;
            }

            if (!EmailUtility.validateEmail(email)) {
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHARED_PREFERENCES_EMAIL_KEY, email);
            editor.apply();

            Intent intent = new Intent(WelcomeActivity.this, ViewAnnotationActivity.class);
            startActivity(intent);
            finishAffinity();
        });

        skipButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, ViewAnnotationActivity.class);
            startActivity(intent);
            finishAffinity();
        });
    }
}