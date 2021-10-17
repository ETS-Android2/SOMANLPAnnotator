package com.cyph.somanlpannotator.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;

import com.airbnb.lottie.LottieAnimationView;
import com.cyph.somanlpannotator.Adapters.ViewEntitiesAdapter;
import com.cyph.somanlpannotator.HelperMethods.ShowDialogWithMessage;
import com.cyph.somanlpannotator.Models.Entity;
import com.cyph.somanlpannotator.Network.QueryNetworkClass;
import com.cyph.somanlpannotator.R;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class ViewAnnotationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private Context context;
    public static final String QUERY_STRING_KEY = "query_string";
    private static final String SAVED_ENTITY_LIST_KEY = "saved_entity_list_key";
    private static final String SAVED_INTENT_KEY = "saved_intent_key";
    private static final String SAVED_VISIBILITY_OF_RESULT_VIEWS_KEY = "saved_visibility_of_result_views_key";
    private static final boolean showAnnotateEntityMenuItem = false;
    private boolean hasLoaded = true;
    private boolean resultViewsAreVisible = false;
    private String intentString = "";

    private EditText queryEditText;
    private Button queryButton, declineButton, acceptButton;
    private TextView intentLabel, intentTextView, entityLabel;
    private RecyclerView recyclerView;
    private LottieAnimationView progressBar;

    private ArrayList<Entity> entityList;
    private ViewEntitiesAdapter viewEntitiesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_annotation);

        context = this;

        if (getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }

        this.invalidateOptionsMenu();

        Toolbar toolbar = findViewById(R.id.toolbar);
        queryEditText = findViewById(R.id.query);
        queryButton = findViewById(R.id.query_button);
        declineButton = findViewById(R.id.decline_results);
        acceptButton = findViewById(R.id.accept_results);
        intentLabel = findViewById(R.id.intent_label);
        intentTextView = findViewById(R.id.intent);
        entityLabel = findViewById(R.id.entity_label);
        recyclerView = findViewById(R.id.entity_list);
        progressBar = findViewById(R.id.progress_bar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("View Annotation");

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(context);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        recyclerView.setLayoutManager(flexboxLayoutManager);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVED_ENTITY_LIST_KEY)) {
                entityList = savedInstanceState.getParcelableArrayList(SAVED_ENTITY_LIST_KEY);

                if (entityList.size() < 1) {
                    entityList = new ArrayList<>();
                    recyclerView.setVisibility(View.GONE);
                    entityLabel.setText(R.string.no_named_entities_message);
                }
            } else {
                entityList = new ArrayList<>();
                recyclerView.setVisibility(View.GONE);
                entityLabel.setText(R.string.no_named_entities_message);
            }

            if (savedInstanceState.containsKey(SAVED_INTENT_KEY)) {
                intentString = savedInstanceState.getString(SAVED_INTENT_KEY);
            } else {
                intentString = "";
            }
            intentTextView.setText(intentString);

            if (savedInstanceState.containsKey(SAVED_VISIBILITY_OF_RESULT_VIEWS_KEY)) {
                resultViewsAreVisible = savedInstanceState.getBoolean(SAVED_VISIBILITY_OF_RESULT_VIEWS_KEY, false);

                if (resultViewsAreVisible) {
                    showResultViews();
                } else {
                    resetViewWithMessage("");
                }
            } else {
                resetViewWithMessage("");
                resultViewsAreVisible = false;
            }

            if (recyclerView.getVisibility() == View.VISIBLE) {
                queryButton.setVisibility(View.GONE);
            } else {
                queryButton.setVisibility(View.VISIBLE);
            }

        } else {
            resetViewWithMessage("");
            entityList = new ArrayList<>();
            resultViewsAreVisible = false;
            queryButton.setVisibility(View.VISIBLE);
        }

        viewEntitiesAdapter = new ViewEntitiesAdapter(entityList);
        recyclerView.setAdapter(viewEntitiesAdapter);

        queryButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setAlpha(0.0f);
            progressBar.animate().alpha(1.0f);
            queryButton.animate().translationY(0);
            queryButton.setEnabled(false);

            String queryString = queryEditText.getText().toString().trim();

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;

            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }

            if (queryString.equals("")) {
                progressBar.setVisibility(View.GONE);
                queryButton.setEnabled(true);
                String message = getString(R.string.empty_string_to_parse_error_message);
                ShowDialogWithMessage.showDialogWithMessage(context, message);
                return;
            }

            if (networkInfo == null) {
                progressBar.setVisibility(View.GONE);
                queryButton.setEnabled(true);
                String message = getString(R.string.network_error_message);
                ShowDialogWithMessage.showDialogWithMessage(context, message);
                return;
            }

            Bundle queryBundle = new Bundle();
            queryBundle.putString(QUERY_STRING_KEY, queryString);
            hasLoaded = false;
            getSupportLoaderManager().restartLoader(0, queryBundle, ViewAnnotationActivity.this);
        });

        declineButton.setOnClickListener(v -> {
            String queryString = queryEditText.getText().toString().trim();

            String message = "";
            resetViewWithMessage(message);

            Bundle bundle = new Bundle();
            bundle.putString(QUERY_STRING_KEY, queryString);
            Intent intent = new Intent(ViewAnnotationActivity.this, MakeAnnotationActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        acceptButton.setOnClickListener(v -> {
            String message = "Thank you, now return to continue.";
            resetViewWithMessage(message);
        });
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        //Initialize queryString
        String queryString = "";

        //Get queryString from passed in bundle if bundle is not null
        if (args != null) {
            queryString = args.getString(QUERY_STRING_KEY);
        }

        //Call subclassed AsyncTaskLoader through its constructor
        return new QueryNetworkClass(this, queryString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (hasLoaded) return;

        progressBar.animate().alpha(0.0f);
        progressBar.setVisibility(View.GONE);
        queryButton.animate().translationY(0);
        queryButton.setEnabled(true);

        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject intentObject = jsonObject.getJSONObject("intent");
            String queryIntent = intentObject.getString("name");
            intentString = getString(R.string.intent_prefix_for_view_annotation) + queryIntent;
            intentTextView.setText(intentString);

            JSONArray entitiesArray = jsonObject.getJSONArray("entities");

            if (entitiesArray.length() > 0) {
                entityList.clear();
                viewEntitiesAdapter.notifyDataSetChanged();
                for (int i = 0; i < entitiesArray.length(); i++) {
                    JSONObject entityObject = entitiesArray.getJSONObject(i);
                    String value = entityObject.getString("value");
                    String entity = entityObject.getString("entity");
                    String valueEntity = value + "_" + entity;

                    Entity entityModel = new Entity(valueEntity);
                    entityList.add(entityModel);
                }

                progressBar.setVisibility(View.GONE);
                queryButton.setVisibility(View.GONE);
                animateViewsIn();
            } else {
                //The query has no named entities
                progressBar.setVisibility(View.GONE);
                queryButton.setVisibility(View.GONE);
                animateViewsIn();
                recyclerView.setVisibility(View.GONE);
                entityLabel.setText(R.string.no_named_entities_message);
            }
        } catch (JSONException jsonException) {
            String message = jsonException.getMessage();
            assert message != null;
            resetViewWithMessage(message);
        } finally {
            hasLoaded = true;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    /***
     * Resets the entire view to take a fresh query
     * @param message Shows an optional message to the user
     * @author Otakenne
     * @since 1
     */
    private void resetViewWithMessage(String message) {
        resultViewsAreVisible = false;
        queryEditText.setText("");
        progressBar.setVisibility(View.GONE);
        queryButton.setEnabled(true);
        queryButton.setVisibility(View.VISIBLE);

        intentLabel.setVisibility(View.GONE);
        intentTextView.setVisibility(View.GONE);
        entityLabel.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        declineButton.setVisibility(View.GONE);
        acceptButton.setVisibility(View.GONE);

        if (!message.equals("")) {
            ShowDialogWithMessage.showDialogWithMessage(context, message);
        }
    }

    private void animateViewsIn() {
        resultViewsAreVisible = true;

        intentLabel.setVisibility(View.VISIBLE);
        intentTextView.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, 50, 0);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(animate);
        animation.addAnimation(alphaAnimation);
        animation.setStartOffset(100);
        animation.setDuration(300);
        intentLabel.startAnimation(animation);
        intentTextView.startAnimation(animation);

        entityLabel.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        TranslateAnimation animate2 = new TranslateAnimation(0, 0, 50, 0);
        AlphaAnimation alphaAnimation2 = new AlphaAnimation(0.0f, 1.0f);
        AnimationSet animation2 = new AnimationSet(true);
        animation2.addAnimation(animate2);
        animation2.addAnimation(alphaAnimation2);
        animation2.setStartOffset(400);
        animation2.setDuration(300);
        entityLabel.startAnimation(animation2);
        recyclerView.startAnimation(animation2);


        declineButton.setVisibility(View.VISIBLE);
        acceptButton.setVisibility(View.VISIBLE);
        TranslateAnimation animate3 = new TranslateAnimation(0, 0, 50, 0);
        AlphaAnimation alphaAnimation3 = new AlphaAnimation(0.0f, 1.0f);
        AnimationSet animation3 = new AnimationSet(true);
        animation3.addAnimation(animate3);
        animation3.addAnimation(alphaAnimation3);
        animation3.setStartOffset(700);
        animation3.setDuration(300);
        declineButton.startAnimation(animation3);
        acceptButton.startAnimation(animation3);
    }

    /**
     * Shows the result views and sets "resultViewsAreVisible" to true
     * @author Otakenne
     * @since 1
     */
    private void showResultViews() {
        resultViewsAreVisible = true;
        intentLabel.setVisibility(View.VISIBLE);
        intentTextView.setVisibility(View.VISIBLE);
        entityLabel.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        declineButton.setVisibility(View.VISIBLE);
        acceptButton.setVisibility(View.VISIBLE);
    }

    /**
     * Persist "entityList", "intentString" and "resultViewsAreVisible" across device state changes
     * @param outState KeyValue pair holding "entityList", "intentString" and "resultViewsAreVisible"
     * @author Otakenne
     * @since 1
     */
    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        outState.putParcelableArrayList(SAVED_ENTITY_LIST_KEY, entityList);
        outState.putString(SAVED_INTENT_KEY, intentString);
        outState.putBoolean(SAVED_VISIBILITY_OF_RESULT_VIEWS_KEY, resultViewsAreVisible);
        super.onSaveInstanceState(outState);
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
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.annotate_entity) {
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