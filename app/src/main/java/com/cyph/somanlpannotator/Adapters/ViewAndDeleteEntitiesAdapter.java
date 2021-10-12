package com.cyph.somanlpannotator.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cyph.somanlpannotator.Models.EntityModel;
import com.cyph.somanlpannotator.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ViewAndDeleteEntitiesAdapter extends RecyclerView.Adapter<ViewAndDeleteEntitiesAdapter.MyViewHolder> {
    private final ArrayList<EntityModel> entityModelList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView entity;
        public ImageButton cancelButton;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            entity = itemView.findViewById(R.id.entity);
            cancelButton = itemView.findViewById(R.id.cancel_button);
        }
    }

    public ViewAndDeleteEntitiesAdapter(ArrayList<EntityModel> entityModelList) {
        this.entityModelList = entityModelList;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_and_delete_entity_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        EntityModel entityModel = entityModelList.get(position);

        String entityValueString = entityModel.getValue() + " : " + entityModel.getEntity();
        holder.entity.setText(entityValueString);

        holder.cancelButton.setOnClickListener(v -> {
            entityModelList.remove(entityModel);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return entityModelList.size();
    }
}
