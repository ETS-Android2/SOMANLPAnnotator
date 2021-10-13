package com.cyph.somanlpannotator.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * EntityModel class, a more detailed entity class
 */
public class EntityModel implements Parcelable {
    String entity, confidence, value, extractor;
    int start, end;

    /**
     * Empty constructor to initialize an instance of the "EntityModel" class
     * @author Otakenne
     * @since 1
     */
    public EntityModel() {
        this.entity = "";
        this.confidence = "";
        this.value = "";
        this.extractor = "";
        this.start = 0;
        this.end = 0;
    }

    /**
     * Constructor to initialize an instance of the "EntityModel" class with 6 params
     * @param entity The recognised named entity
     * @param confidence The confidence value for entity recognition
     * @param value The string sequence in the wider queryString recognised by the NLP engine
     * @param extractor The mathematical extractor used to recognise the named entity
     * @param start The start index of the {@value} parameter within the larger query string
     * @param end The end index of the {@value} parameter within the larger query string
     * @author Otakenne
     * @since 1
     */
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

    /**
     * Gets the value of the private field "confidence"
     * Confidence is the confidence value for the recognised named entity
     * @return the value of the private field "confidence"
     * @author Otakenne
     * @since 1
     */
    public String getConfidence() {
        return confidence;
    }

    /**
     * Sets the value of the private field "confidence"
     * @param confidence The confidence value for entity recognition
     * @author Otakenne
     * @since 1
     */
    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    /**
     * Gets the value of the private field "value"
     * Value is the string sequence in the wider queryString recognised by the NLP engine
     * @return the value of the private field "value"
     * @author Otakenne
     * @since 1
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the private field "value"
     * @param value The string sequence in the wider queryString recognised by the NLP engine
     * @author Otakenne
     * @since 1
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the private field "extractor"
     * Extractor is the mathematical extractor used to recognise the named entity
     * @return the value of the private field "extractor"
     * @author Otakenne
     * @since 1
     */
    public String getExtractor() {
        return extractor;
    }

    /**
     * Sets the value of the private field "extractor"
     * @param extractor The mathematical extractor used to recognise the named entity
     * @author Otakenne
     * @since 1
     */
    public void setExtractor(String extractor) {
        this.extractor = extractor;
    }

    /**
     * Gets the value of the private field "start"
     * Start is the start index of the {@value} parameter within the larger query string
     * @return the value of the private field "start"
     * @author Otakenne
     * @since 1
     */
    public int getStart() {
        return start;
    }

    /**
     * Sets the value of the private field "start"
     * @param start The start index of the {@value} parameter within the larger query string
     * @author Otakenne
     * @since 1
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * Gets the value of the private field "end"
     * End is the end index of the {@value} parameter within the larger query string
     * @return the value of the private field "end"
     * @author Otakenne
     * @since 1
     */
    public int getEnd() {
        return end;
    }

    /**
     * Sets the value of the private field "end"
     * @param end The end index of the {@value} parameter within the larger query string
     * @author Otakenne
     * @since 1
     */
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
