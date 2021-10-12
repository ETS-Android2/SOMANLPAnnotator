package com.cyph.somanlpannotator.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

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

public class ViewAnnotationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private Context context;
    public static final String QUERY_STRING_KEY = "query_string";
    private static final String SAVED_ENTITY_LIST_KEY = "saved_entity_list_key";
    private static final boolean showAnnotateEntityMenuItem = false;

    private EditText queryEditText;
    private Button queryButton, declineButton, acceptButton;
    private TextView intentTextView, entityTextView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

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

        queryEditText = findViewById(R.id.query);
        queryButton = findViewById(R.id.query_button);
        declineButton = findViewById(R.id.decline_results);
        acceptButton = findViewById(R.id.accept_results);
        intentTextView = findViewById(R.id.intent_label);
        entityTextView = findViewById(R.id.entity_label);
        recyclerView = findViewById(R.id.entity_list);
        progressBar = findViewById(R.id.progress_bar);

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(context);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        recyclerView.setLayoutManager(flexboxLayoutManager);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVED_ENTITY_LIST_KEY)) {
                entityList = savedInstanceState.getParcelableArrayList(SAVED_ENTITY_LIST_KEY);
            } else {
                entityList = new ArrayList<>();
            }
        } else {
            entityList = new ArrayList<>();
        }

        viewEntitiesAdapter = new ViewEntitiesAdapter(entityList);
        recyclerView.setAdapter(viewEntitiesAdapter);

        queryButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
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
            getSupportLoaderManager().restartLoader(0, queryBundle, ViewAnnotationActivity.this);
        });

        declineButton.setOnClickListener(v -> {
            String queryString = queryEditText.getText().toString().trim();

            Bundle bundle = new Bundle();
            bundle.putString(QUERY_STRING_KEY, queryString);
            Intent intent = new Intent(ViewAnnotationActivity.this, MakeAnnotationActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);

            String message = "";
            resetViewWithMessage(message);
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

        if (args != null) {
            //Get queryString from passed in bundle if bundle is not null
            queryString = args.getString(QUERY_STRING_KEY);
        }

        //Call subclassed AsyncTaskLoader through its constructor
        return new QueryNetworkClass(this, queryString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject intentObject = jsonObject.getJSONObject("intent");
            String queryIntent = intentObject.getString("name");
            String intentString = getString(R.string.intent_prefix_for_view_annotation) + queryIntent;
            intentTextView.setText(intentString);

            JSONArray entitiesArray = jsonObject.getJSONArray("entities");

            if (entitiesArray.length() > 0) {
                for (int i = 0; i < entitiesArray.length(); i++) {
                    JSONObject entityObject = entitiesArray.getJSONObject(i);
                    String value = entityObject.getString("value");
                    String entity = entityObject.getString("entity");
                    String valueEntity = value + "_" + entity;

                    Entity entityModel = new Entity(valueEntity);
                    entityList.add(entityModel);
                }

                viewEntitiesAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                queryButton.setVisibility(View.GONE);

                intentTextView.setVisibility(View.VISIBLE);
                entityTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                declineButton.setVisibility(View.VISIBLE);
                acceptButton.setVisibility(View.VISIBLE);
            } else {
                //The query has no named entities
                progressBar.setVisibility(View.GONE);
                queryButton.setVisibility(View.GONE);

                intentTextView.setVisibility(View.VISIBLE);
                entityTextView.setVisibility(View.VISIBLE);
                declineButton.setVisibility(View.VISIBLE);
                acceptButton.setVisibility(View.VISIBLE);

                recyclerView.setVisibility(View.GONE);
                entityTextView.setText(R.string.no_named_entities_message);
            }
        } catch (JSONException jsonException) {
            String message = jsonException.getMessage();
            assert message != null;
            resetViewWithMessage(message);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    /***
     * Resets the entire view to take a fresh query
     * @param message Shows an optional message to the user
     */
    private void resetViewWithMessage(String message) {
        queryEditText.setText("");
        progressBar.setVisibility(View.GONE);
        queryButton.setEnabled(true);
        queryButton.setVisibility(View.VISIBLE);

        intentTextView.setVisibility(View.GONE);
        entityTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        declineButton.setVisibility(View.GONE);
        acceptButton.setVisibility(View.GONE);

        if (!message.equals("")) {
            ShowDialogWithMessage.showDialogWithMessage(context, message);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        outState.putParcelableArrayList(SAVED_ENTITY_LIST_KEY, entityList);
        super.onSaveInstanceState(outState);
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