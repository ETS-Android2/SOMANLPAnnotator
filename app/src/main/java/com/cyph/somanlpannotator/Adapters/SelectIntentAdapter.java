package com.cyph.somanlpannotator.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
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

//        TypedValue typedValue = new TypedValue();
//        Resources.Theme theme = context.getTheme();
//        theme.resolveAttribute(R.attr.IntentBackgroundColorSelected, typedValue, true);
//        @ColorInt int color = typedValue.data;

        if (selectedIntent.equals(intent.getIntent())) {
            holder.intent.setTextColor(context.getResources().getColor(R.color.white));
            holder.intent.setBackground(ContextCompat.getDrawable(context, R.drawable.intent_background_selected));
        } else {
            holder.intent.setTextColor(context.getResources().getColor(R.color.black));
            holder.intent.setBackground(ContextCompat.getDrawable(context, R.drawable.intent_background));
        }

        holder.view.setOnClickListener(v -> {
//            animateTextView(holder.intent);
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

    public void setSelectedIntent() {
        this.selectedIntent = intentList.get(0).getIntent();
    }

    public void setSelectedIntent(String selectedIntent) {
        this.selectedIntent = selectedIntent;
    }

//    public void animateTextView(final TextView textView) {
//        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.15f, 0.0f, 1.15f,
//                Animation.RELATIVE_TO_SELF, 0.75f, Animation.RELATIVE_TO_SELF, 0.5f);
//        prepareAnimation(scaleAnimation);
//
//        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
//        prepareAnimation(alphaAnimation);
//
//        AnimationSet animation = new AnimationSet(true);
//        animation.addAnimation(alphaAnimation);
//        animation.addAnimation(scaleAnimation);
//        animation.setDuration(10000);
//        animation.setFillAfter(false);
//
//        textView.startAnimation(animation);
//    }
//
//    private Animation prepareAnimation(Animation animation){
//        animation.setRepeatCount(0);
//        animation.setRepeatMode(Animation.REVERSE);
//        return animation;
//    }
}
