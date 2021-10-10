package com.cyph.somanlpannotator.Network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class QueryNetworkClass extends AsyncTaskLoader<String> {
    private String query;

    public QueryNetworkClass(@NonNull Context context, String query) {
        super(context);
        this.query = query;
    }

    @Nullable
    @Override
    public String loadInBackground() {
        return null;
    }
}
