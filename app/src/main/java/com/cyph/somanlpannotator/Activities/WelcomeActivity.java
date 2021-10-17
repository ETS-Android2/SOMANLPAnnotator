package com.cyph.somanlpannotator.Activities;

import android.app.Activity;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

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

    /**
     * Initializes the activity and inflates the "activity_welcome" layout for WelcomeActivity
     * @param savedInstanceState Persists the values across state changes
     * @author Otakenne
     * @since 1
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Initialize the context variable
        context = this;

        // Redraw the toolbar items to hide the AnnotateEntityMenuItem
        this.invalidateOptionsMenu();

        // Get a reference to the views in "activity_welcome"
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView pleaseEnterYourEmailDetailedMessage = findViewById(R.id.please_enter_your_email_detailed_message);
        emailEditText = findViewById(R.id.email);
        continueImageButton = findViewById(R.id.send_email);
        Button skipButton = findViewById(R.id.skip);

        // Setup the toolbar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        // Initialize the sharedPreferences variable
        SharedPreferences sharedPreferences = this.getSharedPreferences("Soma", MODE_PRIVATE);

        // Set the "emailEditText" text to the persisted email or set to empty
        if (sharedPreferences.contains(SHARED_PREFERENCES_EMAIL_KEY)) {
            emailEditText.setText(sharedPreferences.getString(SHARED_PREFERENCES_EMAIL_KEY, ""));
            continueImageButton.setVisibility(View.VISIBLE);
        } else {
            emailEditText.setText("");
            continueImageButton.setVisibility(View.GONE);
        }

        // Show or hide continue button
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

        // Persist email to "sharedPreferences" and continue
        continueImageButton.setOnClickListener(v -> {
            // Get the content of "emailEditText" and store in variable
            String email = emailEditText.getText().toString();

            // Validate email address
            if (!EmailUtility.validateEmail(email)) {
                ShowDialogWithMessage.showDialogWithMessage(context, getString(R.string.enter_valid_email));
                return;
            }

            // Commit the email to sharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHARED_PREFERENCES_EMAIL_KEY, email);
            editor.apply();

            // Create explicit intent
            Intent intent = new Intent(WelcomeActivity.this, ViewAnnotationActivity.class);

            // Animate with shared element transitions
            Pair<View, String> textViewTransition = Pair.create(pleaseEnterYourEmailDetailedMessage, "text_view_shared_element");
            Pair<View, String> editTextTransition = Pair.create(emailEditText, "edit_text_shared_element");
            Pair<View, String> buttonTransition = Pair.create(skipButton, "button_shared_element");
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                    textViewTransition, editTextTransition, buttonTransition);
            startActivity(intent, activityOptionsCompat.toBundle());

            // Start new activity
            startActivity(intent);
        });

        skipButton.setOnClickListener(v -> {
            // Remove existing email from sharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(SHARED_PREFERENCES_EMAIL_KEY);
            editor.apply();

            // Create explicit intent
            Intent intent = new Intent(WelcomeActivity.this, ViewAnnotationActivity.class);

            // Animate with shared element transitions
            Pair<View, String> textViewTransition = Pair.create(pleaseEnterYourEmailDetailedMessage, "text_view_shared_element");
            Pair<View, String> editTextTransition = Pair.create(emailEditText, "edit_text_shared_element");
            Pair<View, String> buttonTransition = Pair.create(skipButton, "button_shared_element");
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                    textViewTransition, editTextTransition, buttonTransition);

            // Start new activity
            startActivity(intent, activityOptionsCompat.toBundle());
        });
    }

    /**
     * Inflates "R.menu.theme_menu"
     * @param menu Menu instance
     * @return Boolean from superClass
     * @author Otakenne
     * @since 1
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.theme_menu, menu);
        MenuItem annotateEntityMenuItem = menu.findItem(R.id.annotate_entity);
        annotateEntityMenuItem.setVisible(showAnnotateEntityMenuItem);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Defines actions for when each item in the "R.menu.theme_menu"
     * @param item Menu item
     * @return Boolean from superClass
     * @author Otakenne
     * @since 1
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.annotate_entity) {
            return true;
        } else if (id == R.id.dark_mode){
            int nightMode = AppCompatDelegate.getDefaultNightMode();

            // if "night mode" is active, set to day and vice versa
            if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}