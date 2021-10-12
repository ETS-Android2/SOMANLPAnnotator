package com.cyph.somanlpannotator.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Intent implements Parcelable {
    String intent;

    public Intent() {
        this.intent = "";
    }

    public Intent(String intent) {
        this.intent = intent;
    }

    private Intent(Parcel in) {
        intent = in.readString();
    }

    public String getIntent() {
        return intent;
    }

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
