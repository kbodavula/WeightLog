package com.bodavula.weightlog.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kbodavula on 8/2/16.
 *
 * Model object to represent weight entry.
 */
public class WeightEntry implements Parcelable {
    private int mId;
    private long mDateTime;
    private float mWeight;

    // Constructor.
    public WeightEntry(int id, long dateTime, float weight) {
        mId = id;
        mDateTime = dateTime;
        mWeight = weight;
    }

    // Getters and Setters fro fields.
    public long getDateTime() {
        return mDateTime;
    }

    public void setDateTime(long dateTime) {
        mDateTime = dateTime;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public float getWeight() {
        return mWeight;
    }

    public void setWeight(float weight) {
        mWeight = weight;
    }


    // Parcelable related.
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeLong(this.mDateTime);
        dest.writeFloat(this.mWeight);
    }

    protected WeightEntry(Parcel in) {
        this.mId = in.readInt();
        this.mDateTime = in.readLong();
        this.mWeight = in.readFloat();
    }

    public static final Parcelable.Creator<WeightEntry> CREATOR = new Parcelable.Creator<WeightEntry>() {
        @Override
        public WeightEntry createFromParcel(Parcel source) {
            return new WeightEntry(source);
        }

        @Override
        public WeightEntry[] newArray(int size) {
            return new WeightEntry[size];
        }
    };
}