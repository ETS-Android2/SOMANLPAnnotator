package com.cyph.somanlpannotator.Activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cyph.somanlpannotator.Adapters.SelectIntentAdapter;
import com.cyph.somanlpannotator.Adapters.ViewAndDeleteEntitiesAdapter;
import com.cyph.somanlpannotator.Adapters.ViewEntitiesForAlertDialogAdapter;
import com.cyph.somanlpannotator.BuildConfig;
import com.cyph.somanlpannotator.CustomViews.CustomEditText;
import com.cyph.somanlpannotator.HelperMethods.Date;
import com.cyph.somanlpannotator.HelperMethods.ShowDialogWithMessage;
import com.cyph.somanlpannotator.Models.Annotation;
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
import java.util.HashMap;
import java.util.Objects;

public class MakeAnnotationActivity extends AppCompatActivity {
    private Context context;
    private SharedPreferences sharedPreferences;

    private static final String SHARED_PREFERENCES_EMAIL_KEY = "email";
    private static final String ACTION_ENTITY_CUSTOM_BROADCAST = BuildConfig.APPLICATION_ID + ".ACTION_ENTITY_CUSTOM_BROADCAST";
    private final static String ACTION_EDITTEXT_CUSTOM_BROADCAST = BuildConfig.APPLICATION_ID + ".ACTION_EDITTEXT_CUSTOM_BROADCAST";
    private static final String SAVED_SELECTED_INTENT_KEY = "saved_selected_intent_key";
    private static final String SAVED_ENTITY_MODEL_LIST_KEY = "saved_entity_model_list_key";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private String queryString = "";
    private EntityModel entityModel;
    private ArrayList<Intent> intentArrayList;
    private ArrayList<Entity> entityArrayList;
    private ArrayList<EntityModel> entityModelArrayList;
    private boolean showAnnotateEntityMenuItem;

    private ScrollView scrollView;
    private CustomEditText queryEditText;
    private TextView emptyEntitiesMessage;
    private ProgressBar progressBar;

    private SelectIntentAdapter selectIntentAdapter;
    private ViewAndDeleteEntitiesAdapter viewAndDeleteEntitiesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_annotation);

        context = this;
        firebaseDatabase = FirebaseDatabase.getInstance();
        sharedPreferences = this.getSharedPreferences("Soma", MODE_PRIVATE);

//        int nightMode = AppCompatDelegate.getDefaultNightMode();
//        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        }

//        Bundle bundle = getIntent().getExtras();
        queryString = "What is the gravity of inca"; //bundle.getString(ViewAnnotationActivity.QUERY_STRING_KEY);

        showAnnotateEntityMenuItem = false;
        this.invalidateOptionsMenu();

        Toolbar toolbar = findViewById(R.id.toolbar);
        RecyclerView intentList = findViewById(R.id.intent_list);
        RecyclerView entityList = findViewById(R.id.entity_list);
        Button logAnnotation = findViewById(R.id.log_annotation);
        scrollView = findViewById(R.id.scroll_view);
        queryEditText = findViewById(R.id.query);
        emptyEntitiesMessage = findViewById(R.id.empty_entities_message);
        progressBar = findViewById(R.id.progress_bar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Make Annotation");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);

        scrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        queryEditText.setText(queryString);

        FlexboxLayoutManager intentListFlexboxLayoutManager = new FlexboxLayoutManager(context);
        intentListFlexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        intentListFlexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        intentList.setLayoutManager(intentListFlexboxLayoutManager);

        FlexboxLayoutManager entityListFlexboxLayoutManager = new FlexboxLayoutManager(context);
        entityListFlexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        entityListFlexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        entityList.setLayoutManager(entityListFlexboxLayoutManager);

        intentArrayList = new ArrayList<>();
        entityArrayList = new ArrayList<>();
        selectIntentAdapter = new SelectIntentAdapter(intentArrayList, context);
        intentList.setAdapter(selectIntentAdapter);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVED_SELECTED_INTENT_KEY)) {
                selectIntentAdapter.setSelectedIntent(savedInstanceState.getString(SAVED_SELECTED_INTENT_KEY));
            }
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVED_ENTITY_MODEL_LIST_KEY)) {
                entityModelArrayList = savedInstanceState.getParcelableArrayList(SAVED_ENTITY_MODEL_LIST_KEY);
                emptyEntitiesMessage.setVisibility(View.GONE);
            } else {
                entityModelArrayList = new ArrayList<>();
                emptyEntitiesMessage.setVisibility(View.VISIBLE);
            }
        } else {
            entityModelArrayList = new ArrayList<>();
            emptyEntitiesMessage.setVisibility(View.VISIBLE);
        }

        viewAndDeleteEntitiesAdapter = new ViewAndDeleteEntitiesAdapter(entityModelArrayList);
        entityList.setAdapter(viewAndDeleteEntitiesAdapter);

        loadIntentsAndEntitiesFromFirebase();

        logAnnotation.setOnClickListener(v -> {
            queryString = Objects.requireNonNull(queryEditText.getText()).toString().trim();

            if (queryString.equals("")) {
                String message = getString(R.string.empty_string_to_parse_error_message);
                ShowDialogWithMessage.showDialogWithMessage(context, message);
                return;
            }

            String selectedIntent = selectIntentAdapter.getSelectedIntent();
            String email = sharedPreferences.getString(SHARED_PREFERENCES_EMAIL_KEY, "");
            String date = Date.getDate();
            String sortableDate = Date.convertToSortableDate(date);
            Annotation annotation = new Annotation(selectedIntent, email, queryString, date, sortableDate);

            String pushKey = firebaseDatabase.getReference().child("Annotations").push().getKey();
            HashMap<String, Object> updateAnnotationMap = new HashMap<>();
            updateAnnotationMap.put("Annotation/" + pushKey, annotation);
            updateAnnotationMap.put("Emails/" + email + "/" + pushKey, true);

            for (EntityModel entityModel: entityModelArrayList) {
                if (!queryString.contains(entityModel.getValue())) {
                    String message = getString(R.string.entity_not_in_query_string_error_message);
                    ShowDialogWithMessage.showDialogWithMessage(context, message);
                    return;
                }

                assert pushKey != null;
                String entityKey = firebaseDatabase.getReference().child("Annotated Entities").child(pushKey).push().getKey();
                updateAnnotationMap.put("Annotated Entities/" + pushKey + "/" + entityKey, entityModel);
            }

            databaseReference = firebaseDatabase.getReference();
            databaseReference.updateChildren(updateAnnotationMap, (error, ref) -> resetView());
        });

        LocalBroadcastManager.getInstance(context).registerReceiver(mEntityReceiver, new IntentFilter(ACTION_ENTITY_CUSTOM_BROADCAST));
        LocalBroadcastManager.getInstance(context).registerReceiver(mEditTextReceiver, new IntentFilter(ACTION_EDITTEXT_CUSTOM_BROADCAST));
    }

    public BroadcastReceiver mEntityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            String selectedEntity = intent.getStringExtra("entity");
            entityModel.setEntity(selectedEntity);
            entityModelArrayList.add(entityModel);
            viewAndDeleteEntitiesAdapter.notifyDataSetChanged();

            if (entityModelArrayList.size() > 0) {
                emptyEntitiesMessage.setVisibility(View.GONE);
            }
        }
    };

    public BroadcastReceiver mEditTextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            int startSelection = intent.getIntExtra("selStart", 0);
            int endSelection = intent.getIntExtra("selEnd", 0);

            if (startSelection > 0) {
                showAnnotateEntityMenuItem = startSelection != endSelection;
            } else {
                showAnnotateEntityMenuItem = false;
            }
            MakeAnnotationActivity.this.invalidateOptionsMenu();
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
                        Intent intent = new Intent(postSnapshot.getValue(String.class));
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
                                Entity entity = new Entity(postSnapshot.getValue(String.class));
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
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mEntityReceiver);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mEditTextReceiver);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        String selectedIntent = selectIntentAdapter.getSelectedIntent();
        outState.putString(SAVED_SELECTED_INTENT_KEY, selectedIntent);
        outState.putParcelableArrayList(SAVED_ENTITY_MODEL_LIST_KEY, entityModelArrayList);
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

        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.annotate_entity) {
            int startSelection = queryEditText.getSelectionStart();
            int endSelection = queryEditText.getSelectionEnd();

            if (startSelection != endSelection) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_entities_list_dialog);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                RecyclerView entityListRecyclerView = dialog.findViewById(R.id.entity_list);
//                Button cancel = findViewById(R.id.option_one);

                try {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                } catch (Exception e) {
                    return false;
                }

//                cancel.setText(R.string.cancel);
//                cancel.setOnClickListener(v -> dialog.dismiss() );

                FlexboxLayoutManager entityListRecyclerViewFlexboxLayoutManager = new FlexboxLayoutManager(context);
                entityListRecyclerViewFlexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
                entityListRecyclerViewFlexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
                entityListRecyclerView.setLayoutManager(entityListRecyclerViewFlexboxLayoutManager);
                ViewEntitiesForAlertDialogAdapter viewEntitiesForAlertDialogAdapter = new ViewEntitiesForAlertDialogAdapter(entityArrayList, context, dialog);
                entityListRecyclerView.setAdapter(viewEntitiesForAlertDialogAdapter);

                String selectedText = Objects.requireNonNull(queryEditText.getText()).toString().substring(startSelection, endSelection);
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

    private void resetView() {
        queryEditText.setText("");
        selectIntentAdapter.setSelectedIntent();
        emptyEntitiesMessage.setVisibility(View.VISIBLE);
        entityModelArrayList.clear();
        viewAndDeleteEntitiesAdapter.notifyDataSetChanged();
    }
}