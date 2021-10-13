package com.cyph.somanlpannotator.Utility;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Subclassed Application class which acts as the Application class for the project
 */
public class MApplication extends Application {

    /**
     * onCreate
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Allows firebase to persist results from remote queries (allows
        // application to work offline)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
