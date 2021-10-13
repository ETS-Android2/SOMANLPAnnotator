package com.cyph.somanlpannotator.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Entity class
 */
public class Entity implements Parcelable {
    String entity;

    /**
     * Empty constructor to initialize an instance of the "Entity" class
     * @author Otakenne
     * @since 1
     */
    public Entity() {
        this.entity = "";
    }


    /**
     * Constructor to initialize an instance of the "Entity" class with 1 param
     * @param entity The recognised named entity
     * @author Otakenne
     * @since 1
     */
    public Entity(String entity) {
        this.entity = entity;
    }

    protected Entity(Parcel in) {
        entity = in.readString();
    }

    /**
     * Gets the value of the private field "entity"
     * Entity is the recognised named entity in a queryString
     * @return the value of the private field "entity"
     * @author Otakenne
     * @since 1
     */
    public String getEntity() {
        return entity;
    }

    /**
     * Sets the value of the private field "entity"
     * @param entity The recognised named entity
     * @author Otakenne
     * @since 1
     */
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
