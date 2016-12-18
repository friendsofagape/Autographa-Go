package com.bridgeconn.autographago.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by Admin on 16-12-2016.
 */

public class ChapterModel extends RealmObject implements Parcelable {

    private long chapterNumber;
    private List<VerseModel> verseModels = new ArrayList<>();

    public ChapterModel(ChapterModel model) {
        chapterNumber = model.getChapterNumber();
        verseModels = model.getVerseModels();
    }

    public ChapterModel() {
    }

    public long getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(long chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public List<VerseModel> getVerseModels() {
        return verseModels;
    }

    public void setVerseModels(List<VerseModel> verseModels) {
        this.verseModels = verseModels;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ChapterModel && ((ChapterModel) o).getChapterNumber() == this.chapterNumber;
    }

    public static class ChapterNumberComparator implements Comparator<ChapterModel> {

        @Override
        public int compare(ChapterModel lhs, ChapterModel rhs) {
            return lhs.getChapterNumber() > (rhs.getChapterNumber()) ? 1 : lhs.getChapterNumber() == (rhs.getChapterNumber()) ? 0 : -1;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.chapterNumber);
        dest.writeList(this.verseModels);
    }

    protected ChapterModel(Parcel in) {
        this.chapterNumber = in.readLong();
        this.verseModels = new ArrayList<>();
        in.readList(this.verseModels, VerseModel.class.getClassLoader());
    }

    public static final Creator<ChapterModel> CREATOR = new Creator<ChapterModel>() {
        @Override
        public ChapterModel createFromParcel(Parcel source) {
            return new ChapterModel(source);
        }

        @Override
        public ChapterModel[] newArray(int size) {
            return new ChapterModel[size];
        }
    };

    @Override
    public int hashCode() {
        return ((Long) getChapterNumber()).hashCode();
    }
}
