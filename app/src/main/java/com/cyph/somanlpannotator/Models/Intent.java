package com.cyph.somanlpannotator.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Intent class
 */
public class Intent implements Parcelable {
    String intent;

    /**
     * Empty constructor to initialize an instance of the "Intent" class
     * @author Otakenne
     * @since 1
     */
    public Intent() {
        this.intent = "";
    }

    /**
     * Constructor to initialize an instance of the "Intent" class with 1 param
     * @param intent The intent detected by the NLP engine
     * @author Otakenne
     * @since 1
     */
    public Intent(String intent) {
        this.intent = intent;
    }

    private Intent(Parcel in) {
        intent = in.readString();
    }

    /**
     * Gets the value of the private field "intent"
     * Intent is the detected intent/intention for an NLP query
     * @return the value of the private field "intent"
     * @author Otakenne
     * @since 1
     */
    public String getIntent() {
        return intent;
    }

    /**
     * Sets the value of the private field "intent"
     * @param intent The intent detected by the NLP engine
     * @author Otakenne
     * @since 1
     */
    public void setIntent(String intent) {
        this.intent = intent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(intent);
    }

    public static final Creator<Intent> CREATOR = new Creator<Intent>() {
        @Override
        public Intent createFromParcel(Parcel in) {
            return new Intent(in);
        }

        @Override
        public Intent[] newArray(int size) {
            return new Intent[size];
        }
    };
}
