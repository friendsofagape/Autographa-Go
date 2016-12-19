package com.bridgeconn.autographago.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Admin on 16-12-2016.
 */

public class VerseModel extends RealmObject implements Parcelable, Comparable<VerseModel> {
//    private long id;
    @PrimaryKey
    private long verseNumber;
    private String verseText;
    private boolean paragraphStart;
//    private boolean lineBreak;
    private RealmList<PoetryModel> poetryModels = new RealmList<>();
//    private List<PoetryModel> poetryModels = new ArrayList<>();

    //Local Fields
//    @Ignore
//    private ArrayList<Integer> models = new ArrayList<>();

    public VerseModel(VerseModel model) {
//        id = model.getId();
        verseNumber = model.getVerseNumber();
        verseText = model.getVerseText();
        paragraphStart = model.isParagraphStart();
//        lineBreak = model.isLineBreak();
        poetryModels = model.getPoetryModels();
    }

    public VerseModel() {
    }

//    public long getId() {
//        return id;
//    }

//    public void setId(long id) {
//        this.id = id;
//    }

    public long getVerseNumber() {
        return verseNumber;
    }

    public void setVerseNumber(long verseNumber) {
        this.verseNumber = verseNumber;
    }

    public String getVerseText() {
        return verseText;
    }

    public void setVerseText(String verseText) {
        this.verseText = verseText;
    }

    public boolean isParagraphStart() {
        return paragraphStart;
    }

    public void setParagraphStart(boolean paragraphStart) {
        this.paragraphStart = paragraphStart;
    }

//    public boolean isLineBreak() {
//        return lineBreak;
//    }

//    public void setLineBreak(boolean lineBreak) {
//        this.lineBreak = lineBreak;
//    }

    public RealmList<PoetryModel> getPoetryModels() {
        return poetryModels;
    }

    public void setPoetryModels(RealmList<PoetryModel> poetryModels) {
        this.poetryModels = poetryModels;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof VerseModel && ((VerseModel) o).getVerseNumber() == this.verseNumber;
    }

    @Override
    public int compareTo(@NonNull VerseModel another) {
        return (this.getVerseNumber() < another.getVerseNumber() ) ? -1: (this.getVerseNumber() > another.getVerseNumber()) ? 1 : 0;
    }

    public static class VerseNumberComparator implements Comparator<VerseModel> {

        @Override
        public int compare(VerseModel lhs, VerseModel rhs) {
            return lhs.getVerseNumber() > (rhs.getVerseNumber()) ? 1 : (lhs.getVerseNumber() == (rhs.getVerseNumber())) ? 0 : -1;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeLong(this.id);
        dest.writeLong(this.verseNumber);
        dest.writeString(this.verseText);
        dest.writeByte(this.paragraphStart ? (byte) 1 : (byte) 0);
//        dest.writeByte(this.lineBreak ? (byte) 1 : (byte) 0);
        dest.writeList(this.poetryModels);
    }

    protected VerseModel(Parcel in) {
//        this.id = in.readLong();
        this.verseNumber = in.readLong();
        this.verseText = in.readString();
        this.paragraphStart = in.readByte() != 0;
//        this.lineBreak = in.readByte() != 0;
        this.poetryModels = new RealmList<>();
        in.readList(this.poetryModels, PoetryModel.class.getClassLoader());
}

    public static final Creator<VerseModel> CREATOR = new Creator<VerseModel>() {
        @Override
        public VerseModel createFromParcel(Parcel source) {
            return new VerseModel(source);
        }

        @Override
        public VerseModel[] newArray(int size) {
            return new VerseModel[size];
        }
    };

    @Override
    public int hashCode() {
        return ((Long) getVerseNumber()).hashCode();
    }
}