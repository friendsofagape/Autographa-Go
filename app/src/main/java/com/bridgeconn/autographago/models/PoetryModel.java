package com.bridgeconn.autographago.models;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

/**
 * Created by Admin on 18-12-2016.
 */

public class PoetryModel extends RealmObject implements Parcelable {

    private int poetryIndentLevel;
    private String poetry;

    public PoetryModel(PoetryModel model) {
        poetry = model.getPoetry();
        poetryIndentLevel = model.getPoetryIndentLevel();
    }

    public PoetryModel() {
    }

    public String getPoetry() {
        return poetry;
    }

    public void setPoetry(String poetry) {
        this.poetry = poetry;
    }

    public int getPoetryIndentLevel() {
        return poetryIndentLevel;
    }

    public void setPoetryIndentLevel(int poetryIndentLevel) {
        this.poetryIndentLevel = poetryIndentLevel;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PoetryModel && ((PoetryModel) o).getPoetryIndentLevel() == this.poetryIndentLevel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.poetry);
        dest.writeInt(this.poetryIndentLevel);
    }

    protected PoetryModel(Parcel in) {
        this.poetry = in.readString();
        this.poetryIndentLevel = in.readInt();
    }

    public static final Parcelable.Creator<PoetryModel> CREATOR = new Parcelable.Creator<PoetryModel>() {
        @Override
        public PoetryModel createFromParcel(Parcel source) {
            return new PoetryModel(source);
        }

        @Override
        public PoetryModel[] newArray(int size) {
            return new PoetryModel[size];
        }
    };

    @Override
    public int hashCode() {
        return poetry.hashCode();
    }
}