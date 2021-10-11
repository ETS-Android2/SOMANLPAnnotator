package com.cyph.somanlpannotator.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cyph.somanlpannotator.Adapters.SelectIntentAdapter;
import com.cyph.somanlpannotator.Adapters.ViewEntitiesAdapter;
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
    Context context;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private String queryString = "";
    private ArrayList<Intent> intentArrayList;
    private ArrayList<Entity> entityArrayList;
    private ArrayList<EntityModel> entityModelArrayList;

    private ScrollView scrollView;
    private TextView typeYourQuery, intentLabel, entityLabel;
    private EditText queryEditText;
    private RecyclerView intentList, entityList;
    private Button logAnnotation;
    private ProgressBar progressBar;
    private MenuItem annotateEntityMenuItem;

    private SelectIntentAdapter selectIntentAdapter;
    private ViewEntitiesAdapter viewEntitiesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_annotation);

        context = this;
        firebaseDatabase = FirebaseDatabase.getInstance();

        Bundle bundle = getIntent().getExtras();
        queryString = bundle.getString(ViewAnnotationActivity.QUERY_STRING_KEY);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        annotateEntityMenuItem = (MenuItem) findViewById(R.id.annotate_entity);
        annotateEntityMenuItem.setVisible(false);
        this.invalidateOptionsMenu();

        scrollView = findViewById(R.id.scroll_view);
        typeYourQuery = findViewById(R.id.type_your_query);
        intentLabel = findViewById(R.id.intent_label);
        entityLabel = findViewById(R.id.entity_label);
        queryEditText = findViewById(R.id.query);
        intentList = findViewById(R.id.intent_list);
        entityList = findViewById(R.id.entity_list);
        logAnnotation = findViewById(R.id.log_annotation);
        progressBar = findViewById(R.id.progress_bar);

        scrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(context);
        flexboxLayoutManager.setFlexDirection(FlexDirection.COLUMN);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_END);
        intentList.setLayoutManager(flexboxLayoutManager);
        entityList.setLayoutManager(flexboxLayoutManager);

        intentArrayList = new ArrayList<>();
        entityArrayList = new ArrayList<>();
        selectIntentAdapter = new SelectIntentAdapter(intentArrayList, context);
        viewEntitiesAdapter = new ViewEntitiesAdapter(entityArrayList);
        intentList.setAdapter(selectIntentAdapter);
        entityList.setAdapter(viewEntitiesAdapter);

        loadIntentsAndEntitiesFromFirebase();

        queryEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int startSelection = queryEditText.getSelectionStart();
                int endSelection = queryEditText.getSelectionEnd();

                if (startSelection > 0) {
                    annotateEntityMenuItem.setVisible(startSelection != endSelection);
                } else {
                    annotateEntityMenuItem.setVisible(false);
                }
                MakeAnnotationActivity.this.invalidateOptionsMenu();
                return true;
            }
        });

        logAnnotation.setOnClickListener(v -> {

        });
    }

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
                            viewEntitiesAdapter.notifyDataSetChanged();
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
                String selectedText = queryEditText.getText().toString().substring(startSelection, endSelection);
                EntityModel entityModel = new EntityModel();
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