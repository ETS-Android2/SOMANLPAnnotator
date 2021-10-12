package com.cyph.somanlpannotator.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class EntityModel implements Parcelable {
    String entity, confidence, value, extractor;
    int start, end;

    public EntityModel() {
        this.entity = "";
        this.confidence = "";
        this.value = "";
        this.extractor = "";
        this.start = 0;
        this.end = 0;
    }

    public EntityModel(String entity, String confidence, String value, String extractor, int start, int end) {
        this.entity = entity;
        this.confidence = confidence;
        this.value = value;
        this.extractor = extractor;
        this.start = start;
        this.end = end;
    }

    protected EntityModel(Parcel in) {
        entity = in.readString();
        confidence = in.readString();
        value = in.readString();
        extractor = in.readString();
        start = in.readInt();
        end = in.readInt();
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getExtractor() {
        return extractor;
    }

    public void setExtractor(String extractor) {
        this.extractor = extractor;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(entity);
        dest.writeString(confidence);
        dest.writeString(value);
        dest.writeString(extractor);
        dest.writeInt(start);
        dest.writeInt(end);
    }

    public static final Creator<EntityModel> CREATOR = new Creator<EntityModel>() {
        @Override
        public EntityModel createFromParcel(Parcel in) {
            return new EntityModel(in);
        }

        @Override
        public EntityModel[] newArray(int size) {
            return new EntityModel[size];
        }
    };
}
