package com.cyph.somanlpannotator.CustomViews;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cyph.somanlpannotator.BuildConfig;

import org.jetbrains.annotations.NotNull;

/**
 * Subclassed AppCompatEditText that will allow for the overriding of the onSelectionChanged
 * method, which is then patched to the calling activity through a LocalBroadcastManager
 */
public class CustomEditText extends AppCompatEditText {
    private final static String ACTION_EDITTEXT_CUSTOM_BROADCAST =
            BuildConfig.APPLICATION_ID + ".ACTION_EDITTEXT_CUSTOM_BROADCAST";

    public CustomEditText(@NonNull @NotNull Context context) {
        super(context);
    }

    public CustomEditText(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Overrides the "onSelectionChanged" method of the EditText class and
     * and broadcasts the "selStart" and selEnd positions to receivers
     * within the application
     * @param selStart Start position of selection
     * @param selEnd End position of selection
     */
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        Intent entityIntent = new Intent(ACTION_EDITTEXT_CUSTOM_BROADCAST);
        entityIntent.putExtra("selStart", selStart);
        entityIntent.putExtra("selEnd", selEnd);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(entityIntent);
        super.onSelectionChanged(selStart, selEnd);
    }
}
