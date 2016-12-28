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

public class BookModel extends RealmObject{// implements Parcelable, Comparable<BookModel> {

    @PrimaryKey
    private String bookId;
    private String bookName;
    private RealmList<ChapterModel> chapterModels = new RealmList<>();
    // TODO this chapter number type may not work, use Integer type instead

    public BookModel(BookModel model) {
        bookId = model.getBookId();
        bookName = model.getBookName();
        chapterModels = model.getChapterModels();
    }

    public BookModel() {
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
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

    /*
    @Override
    public boolean equals(Object o) {
        return o instanceof BookModel && ((BookModel) o).getBookId() == this.bookId;
    }

    @Override
    public int compareTo(@NonNull BookModel another) {
        return (this.getBookId().equals(another.getBookId())) ? 1: -1;
    }

    public static class BookAbbreviationComparator implements Comparator<BookModel> {

        @Override
        public int compare(BookModel lhs, BookModel rhs) {
            return lhs.getBookId() == null ?
                    -1 : rhs.getBookId() == null ?
                    -1 : lhs.getBookId().toLowerCase().compareTo(rhs.getBookId().toLowerCase());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bookId);
        dest.writeString(this.bookName);
        dest.writeList(this.chapterModels);
    }

    protected BookModel(Parcel in) {
        this.bookId = in.readString();
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
        return getBookId().hashCode();
    }
    */
}
