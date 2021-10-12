package com.cyph.somanlpannotator.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Entity implements Parcelable {
    String entity;

    public Entity() {
        this.entity = "";
    }

    public Entity(String entity) {
        this.entity = entity;
    }

    protected Entity(Parcel in) {
        entity = in.readString();
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(entity);
    }

    public static final Creator<Entity> CREATOR = new Creator<Entity>() {
        @Override
        public Entity createFromParcel(Parcel in) {
            return new Entity(in);
        }

        @Override
        public Entity[] newArray(int size) {
            return new Entity[size];
        }
    };
}
