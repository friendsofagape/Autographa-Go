package com.bridgeconn.autographago.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 16-12-2016.
 */

public class AllBooksModel implements Parcelable {

    private List<BookModel> bookModels = new ArrayList<>();

    public List<BookModel> getBookModels() {
        return bookModels;
    }

    public void setBookModels(List<BookModel> bookModels) {
        this.bookModels = bookModels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(bookModels);
    }

    public AllBooksModel() {
    }

    protected AllBooksModel(Parcel in) {
        this.bookModels = in.createTypedArrayList(BookModel.CREATOR);
    }

    public static final Parcelable.Creator<AllBooksModel> CREATOR = new Parcelable.Creator<AllBooksModel>() {
        @Override
        public AllBooksModel createFromParcel(Parcel source) {
            return new AllBooksModel(source);
        }

        @Override
        public AllBooksModel[] newArray(int size) {
            return new AllBooksModel[size];
        }
    };

}