package com.cyph.somanlpannotator.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cyph.somanlpannotator.Models.Intent;
import com.cyph.somanlpannotator.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SelectIntentAdapter extends RecyclerView.Adapter<SelectIntentAdapter.MyViewHolder> {
    private final ArrayList<Intent> intentList;
    private final Context context;
    private String selectedIntent;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView intent;
        public View view;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            intent = itemView.findViewById(R.id.intent);
            view = itemView;
        }
    }

    public SelectIntentAdapter(ArrayList<Intent> intentList, Context context) {
        this.intentList = intentList;
        this.context = context;
        this.selectedIntent = "";
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_intent_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Intent intent = intentList.get(position);
        holder.intent.setText(intent.getIntent());

        if (selectedIntent.equals("")) {
            selectedIntent = intentList.get(0).getIntent();
        }

        if (selectedIntent.equals(intent.getIntent())) {
            holder.intent.setBackground(ContextCompat.getDrawable(context, R.drawable.intent_background_selected));
        } else {
            holder.intent.setBackground(ContextCompat.getDrawable(context, R.drawable.intent_background));
        }

        holder.view.setOnClickListener(v -> {
            selectedIntent = intent.getIntent();
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return intentList.size();
    }

    public String getSelectedIntent() {
        return selectedIntent;
    }
}
