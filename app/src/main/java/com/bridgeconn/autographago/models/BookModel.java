package com.bridgeconn.autographago.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Admin on 16-12-2016.
 */

public class BookModel extends RealmObject implements Parcelable, Comparable<BookModel> {

    private String bookAbbreviation;
    private String bookName;
    private RealmList<ChapterModel> chapterModels = new RealmList<>();
    // TODO this chapter number type may not work, use Integer type instea

    public BookModel(BookModel model) {
        bookAbbreviation = model.getBookAbbreviation();
        bookName = model.getBookName();
        chapterModels = model.getChapterModels();
    }

    public BookModel() {
    }

    public String getBookAbbreviation() {
        return bookAbbreviation;
    }

    public void setBookAbbreviation(String bookAbbreviation) {
        this.bookAbbreviation = bookAbbreviation;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public RealmList<ChapterModel> getChapterModels() {
        return chapterModels;
    }

    public void setChapterModels(RealmList<ChapterModel> chapterModels) {
        this.chapterModels = chapterModels;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BookModel && ((BookModel) o).getBookAbbreviation() == this.bookAbbreviation;
    }

    @Override
    public int compareTo(@NonNull BookModel another) {
        return (this.getBookAbbreviation().equals(another.getBookAbbreviation())) ? 1: -1;
    }

    public static class BookAbbreviationComparator implements Comparator<BookModel> {

        @Override
        public int compare(BookModel lhs, BookModel rhs) {
            return lhs.getBookAbbreviation() == null ?
                    -1 : rhs.getBookAbbreviation() == null ?
                    -1 : lhs.getBookAbbreviation().toLowerCase().compareTo(rhs.getBookAbbreviation().toLowerCase());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bookAbbreviation);
        dest.writeString(this.bookName);
        dest.writeList(this.chapterModels);
    }

    protected BookModel(Parcel in) {
        this.bookAbbreviation = in.readString();
        this.bookName = in.readString();
        this.chapterModels = new RealmList<>();
        in.readList(this.chapterModels, ChapterModel.class.getClassLoader());
    }

    public static final Creator<BookModel> CREATOR = new Creator<BookModel>() {
        @Override
        public BookModel createFromParcel(Parcel source) {
            return new BookModel(source);
        }

        @Override
        public BookModel[] newArray(int size) {
            return new BookModel[size];
        }
    };

    @Override
    public int hashCode() {
        return getBookAbbreviation().hashCode();
    }
}
