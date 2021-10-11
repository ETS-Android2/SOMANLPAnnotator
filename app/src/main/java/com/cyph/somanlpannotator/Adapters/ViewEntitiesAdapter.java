package com.cyph.somanlpannotator.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cyph.somanlpannotator.Models.Entity;
import com.cyph.somanlpannotator.R;

import java.util.ArrayList;

/***
 * Adapter class responsible for displaying entities
 * in the ViewAnnotationActivity
 */
public class ViewEntitiesAdapter extends RecyclerView.Adapter<ViewEntitiesAdapter.MyViewHolder> {

    private final ArrayList<Entity> entityList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView entity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            entity = itemView.findViewById(R.id.entity);
        }
    }

    /***
     * Default constructor for ViewEntitiesAdapter
     * @param entityList Grabs the list of entities from the calling activity
     */
    public ViewEntitiesAdapter(ArrayList<Entity> entityList) {
        this.entityList = entityList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_entities_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Entity entity = entityList.get(position);
        holder.entity.setText(entity.getEntity());
    }

    /***
     * Gets the number of entities for display
     * @return Number of entities
     */
    @Override
    public int getItemCount() {
        return entityList.size();
    }
}
