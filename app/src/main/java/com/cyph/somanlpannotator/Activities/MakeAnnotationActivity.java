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
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
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

import com.airbnb.lottie.LottieAnimationView;
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
import com.cyph.somanlpannotator.Utility.CustomProgressDialog;
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

/**
 * This activity provides facility to annotate a query string and log annotations to a firebase realtime database
 */
public class MakeAnnotationActivity extends AppCompatActivity {
    private Context context;
    // SharedPreference instance stores the email of the annotator with is logged for every annotated query
    private SharedPreferences sharedPreferences;

    // Key to get
    private static final String SHARED_PREFERENCES_EMAIL_KEY = "email";
    private static final String ACTION_ENTITY_CUSTOM_BROADCAST = BuildConfig.APPLICATION_ID + ".ACTION_ENTITY_CUSTOM_BROADCAST";
    private final static String ACTION_EDITTEXT_CUSTOM_BROADCAST = BuildConfig.APPLICATION_ID + ".ACTION_EDITTEXT_CUSTOM_BROADCAST";
    private static final String SAVED_SELECTED_INTENT_KEY = "saved_selected_intent_key";
    private static final String SAVED_ENTITY_MODEL_LIST_KEY = "saved_entity_model_list_key";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private CustomProgressDialog customProgressDialog;

    private String queryString = "";
    private EntityModel entityModel;
    private ArrayList<Intent> intentArrayList;
    private ArrayList<Entity> entityArrayList;
    private ArrayList<EntityModel> entityModelArrayList;
    private boolean showAnnotateEntityMenuItem;

    private ScrollView scrollView;
    private CustomEditText queryEditText;
    private TextView typeYourQuery, intentLabel, entityLabel, emptyEntitiesMessage;
    private RecyclerView intentList, entityList;
    private Button logAnnotation;
    private LottieAnimationView progressBar;

    private SelectIntentAdapter selectIntentAdapter;
    private ViewAndDeleteEntitiesAdapter viewAndDeleteEntitiesAdapter;

    /**
     * Initializes the activity and inflates the "activity_make_annotation" layout for MakeAnnotationActivity
     * @param savedInstanceState Persists the values of "selectedIntent" and "entityModelArrayList" across state
     *                           changes
     * @author Otakenne
     * @since 1
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_annotation);

        // Initialize "context", "firebaseDatabase" and "sharedPreferences"
        context = this;
        firebaseDatabase = FirebaseDatabase.getInstance();
        sharedPreferences = this.getSharedPreferences("Soma", MODE_PRIVATE);

        // Initialize the progressDialog
        customProgressDialog = new CustomProgressDialog(context);

//        int nightMode = AppCompatDelegate.getDefaultNightMode();
//        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        }

        // Get the "queryString" from the calling activity so user doesn't have to reenter it
        Bundle bundle = getIntent().getExtras();
        queryString = bundle.getString(ViewAnnotationActivity.QUERY_STRING_KEY);

        // Initialize the flag the controls the visibility of the AnnotateEntityMenuItem, use invalidateOptionsMenu()
        // to force re-rendering
        showAnnotateEntityMenuItem = false;
        this.invalidateOptionsMenu();

        // Get a reference to the views in "activity_make_annotation"
        Toolbar toolbar = findViewById(R.id.toolbar);
        typeYourQuery = findViewById(R.id.type_your_query);
        intentLabel = findViewById(R.id.intent_label);
        intentList = findViewById(R.id.intent_list);
        entityLabel = findViewById(R.id.entity_label);
        entityList = findViewById(R.id.entity_list);
        logAnnotation = findViewById(R.id.log_annotation);
        scrollView = findViewById(R.id.scroll_view);
        queryEditText = findViewById(R.id.query);
        emptyEntitiesMessage = findViewById(R.id.empty_entities_message);
        progressBar = findViewById(R.id.progress_bar);

        // Setup the toolbar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Make Annotation");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);

        // Show "progressBar" until firebase loads the intents and entities
        scrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        // Set the "queryString" to the "queryEditText"
        queryEditText.setText(queryString);

        // Create and set the properties of the FlexboxLayoutManager that acts as the LayoutManager for
        // the "intentList" recyclerView
        FlexboxLayoutManager intentListFlexboxLayoutManager = new FlexboxLayoutManager(context);
        intentListFlexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        intentListFlexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        intentList.setLayoutManager(intentListFlexboxLayoutManager);

        // Create and set the properties of the FlexboxLayoutManager that acts as the LayoutManager for
        // the "entityList" recyclerView
        FlexboxLayoutManager entityListFlexboxLayoutManager = new FlexboxLayoutManager(context);
        entityListFlexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        entityListFlexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        entityList.setLayoutManager(entityListFlexboxLayoutManager);

        // Initialize "intentArrayList", "entityArrayList" and "selectIntentAdapter"
        // Set "selectIntentAdapter" as the adapter for the "intentList" recyclerView
        intentArrayList = new ArrayList<>();
        entityArrayList = new ArrayList<>();
        selectIntentAdapter = new SelectIntentAdapter(intentArrayList, context);
        intentList.setAdapter(selectIntentAdapter);

        // Set the "selectedIntent" variable in "SelectIntentAdapter" with its setter
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVED_SELECTED_INTENT_KEY)) {
                selectIntentAdapter.setSelectedIntent(savedInstanceState.getString(SAVED_SELECTED_INTENT_KEY));
            }
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVED_ENTITY_MODEL_LIST_KEY)) {
                entityModelArrayList = savedInstanceState.getParcelableArrayList(SAVED_ENTITY_MODEL_LIST_KEY);
                if (entityModelArrayList.size() > 0) {
                    emptyEntitiesMessage.setVisibility(View.GONE);
                } else {
                    emptyEntitiesMessage.setVisibility(View.VISIBLE);
                }
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
            logAnnotation.setEnabled(false);
            queryString = Objects.requireNonNull(queryEditText.getText()).toString().trim();

            // Validate the query
            if (queryString.equals("")) {
                String message = getString(R.string.empty_string_to_parse_error_message);
                ShowDialogWithMessage.showDialogWithMessage(context, message);
                return;
            }

            // Show the progressDialog
            customProgressDialog.show();

            // Get the selected intent
            String selectedIntent = selectIntentAdapter.getSelectedIntent();

            // Get stored email from sharedPreferences
            String email = sharedPreferences.getString(SHARED_PREFERENCES_EMAIL_KEY, "");

            // Get the current date and time
            String date = Date.getDate();

            // Convert the current date and time to sortable forms
            String sortableDate = Date.convertToSortableDate(date);

            // Create an "Annotation" instance/object to push to firebase
            Annotation annotation = new Annotation(selectedIntent, email, queryString, date, sortableDate);

            String pushKey = firebaseDatabase.getReference().child("Annotations").push().getKey();
            HashMap<String, Object> updateAnnotationMap = new HashMap<>();
            updateAnnotationMap.put("Annotation/" + pushKey, annotation);

            if (!email.isEmpty()) {
                updateAnnotationMap.put("Emails/" + email + "/" + pushKey, true);
            } else {
                updateAnnotationMap.put("Emails/No Email/" + pushKey, true);
            }

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

        // Register receivers for local broadcasts
        LocalBroadcastManager.getInstance(context).registerReceiver(mEntityReceiver, new IntentFilter(ACTION_ENTITY_CUSTOM_BROADCAST));
        LocalBroadcastManager.getInstance(context).registerReceiver(mEditTextReceiver, new IntentFilter(ACTION_EDITTEXT_CUSTOM_BROADCAST));
    }

    /**
     * Receive the LocalBroadcast that is sent when a string is annotated with an entity
     */
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

    /**
     * Receive the LocalBroadcast that is sent when the subclassed EditText (CustomEditText)
     * spots a text selection change
     */
    public BroadcastReceiver mEditTextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            int startSelection = intent.getIntExtra("selStart", 0);
            int endSelection = intent.getIntExtra("selEnd", 0);

            if (startSelection >= 0) {
                showAnnotateEntityMenuItem = startSelection != endSelection;
            } else {
                showAnnotateEntityMenuItem = false;
            }
            MakeAnnotationActivity.this.invalidateOptionsMenu();
        }
    };

    /**
     * Load intents and entities from firebase, dismiss progressBar and show scrollView
     * @author Otakenne
     * @since 1
     */
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

                        animateViewsIn();
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

    /**
     * Animates the views in "activity_make_annotation" and makes them visible
     * @author Otakenne
     * @since 1
     */
    private void animateViewsIn() {
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, 50, 0);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(animate);
        animation.addAnimation(alphaAnimation);
        animation.setStartOffset(100);
        animation.setDuration(300);
        typeYourQuery.startAnimation(animation);
        queryEditText.startAnimation(animation);

        TranslateAnimation animate2 = new TranslateAnimation(0, 0, 50, 0);
        AlphaAnimation alphaAnimation2 = new AlphaAnimation(0.0f, 1.0f);
        AnimationSet animation2 = new AnimationSet(true);
        animation2.addAnimation(animate2);
        animation2.addAnimation(alphaAnimation2);
        animation2.setStartOffset(400);
        animation2.setDuration(300);
        intentLabel.startAnimation(animation2);
        intentList.startAnimation(animation2);

        TranslateAnimation animate3 = new TranslateAnimation(0, 0, 50, 0);
        AlphaAnimation alphaAnimation3 = new AlphaAnimation(0.0f, 1.0f);
        AnimationSet animation3 = new AnimationSet(true);
        animation3.addAnimation(animate3);
        animation3.addAnimation(alphaAnimation3);
        animation3.setStartOffset(700);
        animation3.setDuration(300);
        entityLabel.startAnimation(animation3);
        entityList.startAnimation(animation3);
        emptyEntitiesMessage.startAnimation(animation3);

        TranslateAnimation animate4 = new TranslateAnimation(0, 0, 50, 0);
        AlphaAnimation alphaAnimation4 = new AlphaAnimation(0.0f, 1.0f);
        AnimationSet animation4 = new AnimationSet(true);
        animation4.addAnimation(animate4);
        animation4.addAnimation(alphaAnimation4);
        animation4.setStartOffset(1000);
        animation4.setDuration(300);
        logAnnotation.startAnimation(animation4);
    }

    /**
     * Unregister receivers for local broadcasts after activity is destroyed
     */
    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mEntityReceiver);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mEditTextReceiver);
        super.onDestroy();
    }

    /**
     * Persist "selectedIntent" and "entityModelArrayList" across device state changes
     * @param outState KeyValue pair holding "selectedIntent" and "entityModelArrayList"
     * @author Otakenne
     * @since 1
     */
    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        String selectedIntent = selectIntentAdapter.getSelectedIntent();
        outState.putString(SAVED_SELECTED_INTENT_KEY, selectedIntent);
        outState.putParcelableArrayList(SAVED_ENTITY_MODEL_LIST_KEY, entityModelArrayList);
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

    /**
     * Resets the views in the activity to prepare them for fresh input
     * @author Otakenne
     * @since 1
     */
    private void resetView() {
        logAnnotation.setEnabled(false);
        queryEditText.setText("");
        selectIntentAdapter.setSelectedIntent();
        selectIntentAdapter.notifyDataSetChanged();
        emptyEntitiesMessage.setVisibility(View.VISIBLE);
        entityModelArrayList.clear();
        viewAndDeleteEntitiesAdapter.notifyDataSetChanged();
        customProgressDialog.dismiss();
    }
}