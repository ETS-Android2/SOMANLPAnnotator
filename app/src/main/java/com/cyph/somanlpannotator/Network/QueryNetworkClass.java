package com.cyph.somanlpannotator.Network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class QueryNetworkClass extends AsyncTaskLoader<String> {
    private final String query;

    /***
     * Class constructor that allows for the passing of the query string
     * into the AsyncLoaderClass
     * @param context Application context making the call, context must implement
     *                LoaderManager.LoaderCallbacks to call QueryNetworkClass
     * @param query Query to be launched
     */
    public QueryNetworkClass(@NonNull Context context, String query) {
        super(context);
        this.query = query;
    }

    @Nullable
    @Override
    public String loadInBackground() {
        //Make network call and return string
        return NetworkClass.getSomaResponse(query);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
