package com.cyph.somanlpannotator.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.cyph.somanlpannotator.HelperMethods.EmailUtility;
import com.cyph.somanlpannotator.HelperMethods.ShowDialogWithMessage;
import com.cyph.somanlpannotator.R;

import java.util.Objects;
/**
 * This activity provides facility to get user email (or not).
 * Relevant for logging annotations later
 */
public class WelcomeActivity extends AppCompatActivity {

    private Context context;
    private static final String SHARED_PREFERENCES_EMAIL_KEY = "email";
    private static final boolean showAnnotateEntityMenuItem = false;

    private EditText emailEditText;
    private ImageButton continueImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        context = this;
        this.invalidateOptionsMenu();

        Toolbar toolbar = findViewById(R.id.toolbar);
        emailEditText = findViewById(R.id.email);
        continueImageButton = findViewById(R.id.send_email);
        Button skipButton = findViewById(R.id.skip);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        SharedPreferences sharedPreferences = this.getSharedPreferences("Soma", MODE_PRIVATE);

        if (sharedPreferences.contains(SHARED_PREFERENCES_EMAIL_KEY)) {
            emailEditText.setText(sharedPreferences.getString(SHARED_PREFERENCES_EMAIL_KEY, ""));
            continueImageButton.setVisibility(View.VISIBLE);
        } else {
            emailEditText.setText("");
            continueImageButton.setVisibility(View.GONE);
        }

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
                ShowDialogWithMessage.showDialogWithMessage(context, getString(R.string.please_enter_your_email));
                return;
            }

            if (!EmailUtility.validateEmail(email)) {
                ShowDialogWithMessage.showDialogWithMessage(context, getString(R.string.enter_valid_email));
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
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(SHARED_PREFERENCES_EMAIL_KEY);
            editor.apply();
            Intent intent = new Intent(WelcomeActivity.this, ViewAnnotationActivity.class);
            startActivity(intent);
            finishAffinity();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.theme_menu, menu);
        MenuItem annotateEntityMenuItem = menu.findItem(R.id.annotate_entity);
        annotateEntityMenuItem.setVisible(showAnnotateEntityMenuItem);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.annotate_entity) {
            return true;
        } else if (id == R.id.dark_mode){
            int nightMode = AppCompatDelegate.getDefaultNightMode();

            if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}