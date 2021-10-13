package com.cyph.somanlpannotator.Network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class QueryNetworkClass extends AsyncTaskLoader<String> {
    private final String query;

    /**
     * Class constructor that allows for the passing of the query string
     * into the AsyncLoaderClass
     * @param context Activity context making the call, context must implement
     *                LoaderManager.LoaderCallbacks to call QueryNetworkClass
     * @param query Query to be launched
     * @author Otakenne
     * @since 1
     */
    public QueryNetworkClass(@NonNull Context context, String query) {
        super(context);
        this.query = query;
    }

    /**
     * Make async network calls to http://196.1.184.22:5005/model/parse through
     * the {@link NetworkClass}
     * @return JSON representing the result of the query to SOMA
     * @author Otakenne
     * @since 1
     */
    @Nullable
    @Override
    public String loadInBackground() {
        //Make network call and return string
        return NetworkClass.getSomaResponse(query);
    }

    /**
     * Starts the async task
     */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
