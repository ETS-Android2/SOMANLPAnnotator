package com.cyph.somanlpannotator.Activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cyph.somanlpannotator.Adapters.SelectIntentAdapter;
import com.cyph.somanlpannotator.Adapters.ViewAndDeleteEntitiesAdapter;
import com.cyph.somanlpannotator.Adapters.ViewEntitiesForAlertDialogAdapter;
import com.cyph.somanlpannotator.BuildConfig;
import com.cyph.somanlpannotator.HelperMethods.ShowDialogWithMessage;
import com.cyph.somanlpannotator.Models.Entity;
import com.cyph.somanlpannotator.Models.EntityModel;
import com.cyph.somanlpannotator.Models.Intent;
import com.cyph.somanlpannotator.R;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MakeAnnotationActivity extends AppCompatActivity {
    private Context context;
    private SharedPreferences sharedPreferences;

    private static final String SHARED_PREFERENCES_EMAIL_KEY = "email";
    private static final String ACTION_ENTITY_CUSTOM_BROADCAST = BuildConfig.APPLICATION_ID + ".ACTION_ENTITY_CUSTOM_BROADCAST";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private String queryString = "";
    private EntityModel entityModel;
    private ArrayList<Intent> intentArrayList;
    private ArrayList<Entity> entityArrayList;
    private ArrayList<EntityModel> entityModelArrayList;

    private ScrollView scrollView;
    private EditText queryEditText;
    private ProgressBar progressBar;
    private MenuItem annotateEntityMenuItem;

    private FlexboxLayoutManager flexboxLayoutManager;
    private SelectIntentAdapter selectIntentAdapter;
    private ViewAndDeleteEntitiesAdapter viewAndDeleteEntitiesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_annotation);

        context = this;
        firebaseDatabase = FirebaseDatabase.getInstance();
        sharedPreferences = this.getSharedPreferences("Soma", MODE_PRIVATE);

        Bundle bundle = getIntent().getExtras();
        queryString = bundle.getString(ViewAnnotationActivity.QUERY_STRING_KEY);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        annotateEntityMenuItem = findViewById(R.id.annotate_entity);
        annotateEntityMenuItem.setVisible(false);
        this.invalidateOptionsMenu();

        scrollView = findViewById(R.id.scroll_view);
        queryEditText = findViewById(R.id.query);
        RecyclerView intentList = findViewById(R.id.intent_list);
        RecyclerView entityList = findViewById(R.id.entity_list);
        Button logAnnotation = findViewById(R.id.log_annotation);
        progressBar = findViewById(R.id.progress_bar);

        scrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        queryEditText.setText(queryString);

        flexboxLayoutManager = new FlexboxLayoutManager(context);
        flexboxLayoutManager.setFlexDirection(FlexDirection.COLUMN);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_END);
        intentList.setLayoutManager(flexboxLayoutManager);
        entityList.setLayoutManager(flexboxLayoutManager);

        intentArrayList = new ArrayList<>();
        entityArrayList = new ArrayList<>();
        entityModelArrayList = new ArrayList<>();
        selectIntentAdapter = new SelectIntentAdapter(intentArrayList, context);
        viewAndDeleteEntitiesAdapter = new ViewAndDeleteEntitiesAdapter(entityModelArrayList);
        intentList.setAdapter(selectIntentAdapter);
        entityList.setAdapter(viewAndDeleteEntitiesAdapter);

        loadIntentsAndEntitiesFromFirebase();

        queryEditText.setOnLongClickListener(v -> {
            int startSelection = queryEditText.getSelectionStart();
            int endSelection = queryEditText.getSelectionEnd();

            if (startSelection > 0) {
                annotateEntityMenuItem.setVisible(startSelection != endSelection);
            } else {
                annotateEntityMenuItem.setVisible(false);
            }
            MakeAnnotationActivity.this.invalidateOptionsMenu();
            return true;
        });

        logAnnotation.setOnClickListener(v -> {
            queryString = queryEditText.getText().toString().trim();

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;

            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }

            if (queryString.equals("")) {
                String message = getString(R.string.empty_string_to_parse_error_message);
                ShowDialogWithMessage.showDialogWithMessage(context, message);
                return;
            }

            if (networkInfo == null) {
                String message = getString(R.string.network_error_message);
                ShowDialogWithMessage.showDialogWithMessage(context, message);
                return;
            }

            String selectedIntent = selectIntentAdapter.getSelectedIntent();
            String email = sharedPreferences.getString(SHARED_PREFERENCES_EMAIL_KEY, "");


        });

        LocalBroadcastManager.getInstance(context).registerReceiver(mReceiver, new IntentFilter(ACTION_ENTITY_CUSTOM_BROADCAST));
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            String selectedEntity = intent.getStringExtra("entity");
            entityModel.setEntity(selectedEntity);
            entityModelArrayList.add(entityModel);
            viewAndDeleteEntitiesAdapter.notifyDataSetChanged();
        }
    };

    private void loadIntentsAndEntitiesFromFirebase() {
        databaseReference = firebaseDatabase.getReference().child("Intents");
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Intent intent = postSnapshot.getValue(Intent.class);
                        intentArrayList.add(intent);
                    }
                    selectIntentAdapter.notifyDataSetChanged();
                }

                databaseReference = firebaseDatabase.getReference().child("Entities");
                databaseReference.keepSynced(true);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                Entity entity = postSnapshot.getValue(Entity.class);
                                entityArrayList.add(entity);
                            }
                        }

                        scrollView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.theme_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.annotate_entity) {
            int startSelection = queryEditText.getSelectionStart();
            int endSelection = queryEditText.getSelectionEnd();

            if (startSelection != endSelection) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_entities_list_dialog);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                RecyclerView entityListRecyclerView = dialog.findViewById(R.id.entity_list);
                try {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                } catch (Exception e) {
                    return false;
                }

                entityListRecyclerView.setLayoutManager(flexboxLayoutManager);
                ViewEntitiesForAlertDialogAdapter viewEntitiesForAlertDialogAdapter = new ViewEntitiesForAlertDialogAdapter(entityArrayList, context, dialog);
                entityListRecyclerView.setAdapter(viewEntitiesForAlertDialogAdapter);

                String selectedText = queryEditText.getText().toString().substring(startSelection, endSelection);
                entityModel = new EntityModel();
                entityModel.setStart(startSelection);
                entityModel.setEnd(endSelection);
                entityModel.setValue(selectedText);
            }

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