package com.cyph.somanlpannotator.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cyph.somanlpannotator.BuildConfig;
import com.cyph.somanlpannotator.Models.Entity;
import com.cyph.somanlpannotator.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ViewEntitiesForAlertDialogAdapter extends RecyclerView.Adapter<ViewEntitiesForAlertDialogAdapter.MyViewHolder> {
    private final ArrayList<Entity> entityArrayList;
    private final Context context;
    private final Dialog dialog;
    private final static String ACTION_ENTITY_CUSTOM_BROADCAST = BuildConfig.APPLICATION_ID + ".ACTION_ENTITY_CUSTOM_BROADCAST";

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView entity;
        public View view;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            entity = itemView.findViewById(R.id.entity);
            view = itemView;
        }
    }

    public ViewEntitiesForAlertDialogAdapter(ArrayList<Entity> entityArrayList, Context context, Dialog dialog) {
        this.entityArrayList = entityArrayList;
        this.context = context;
        this.dialog = dialog;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_entities_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Entity entity = entityArrayList.get(position);
        holder.entity.setText(entity.getEntity());

        holder.view.setOnClickListener(v -> {
            Intent entityIntent = new Intent(ACTION_ENTITY_CUSTOM_BROADCAST);
            entityIntent.putExtra("entity", entity.getEntity());
            LocalBroadcastManager.getInstance(context).sendBroadcast(entityIntent);
            dialog.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return entityArrayList.size();
    }
}
