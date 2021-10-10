package com.cyph.somanlpannotator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cyph.somanlpannotator.R;

public class ViewAnnotationActivity extends AppCompatActivity {

    private EditText queryEditText;
    private Button queryButton, declineButton, acceptButton;
    private TextView intentTextView, entityTextView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_annotation);

        queryEditText = findViewById(R.id.query);
        queryButton = findViewById(R.id.query_button);
        declineButton = findViewById(R.id.decline_results);
        acceptButton = findViewById(R.id.accept_results);
        intentTextView = findViewById(R.id.intent_label);
        entityTextView = findViewById(R.id.entity_label);
        recyclerView = findViewById(R.id.entity_list);
        progressBar = findViewById(R.id.progress_bar);

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                queryButton.setEnabled(false);
            }
        });
    }


}