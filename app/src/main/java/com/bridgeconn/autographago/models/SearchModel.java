package com.bridgeconn.autographago.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SearchModel extends RealmObject implements Parcelable {//}, Serializable {

    private String bookId;
    private String bookName;
    private int chapterNumber;
    private String verseNumber;
    private String text;
    private long timeStamp;
    @PrimaryKey
    private String searchId;
    private String section;
    private int bookNumber;
    private String languageCode;
    private String versionCode;

    public SearchModel() {
    }

    public SearchModel(SearchModel searchModel) {
        bookId = searchModel.getBookId();
        bookName = searchModel.getBookName();
        chapterNumber = searchModel.getChapterNumber();
        verseNumber = searchModel.getVerseNumber();
        text = searchModel.getText();
        timeStamp = searchModel.getTimeStamp();
        searchId = searchModel.getSearchId();
        section = searchModel.getSection();
        bookNumber = searchModel.getBookNumber();
        languageCode = searchModel.getLanguageCode();
        versionCode = searchModel.getVersionCode();
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

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getVerseNumber() {
        return verseNumber;
    }

    public void setVerseNumber(String verseNumber) {
        this.verseNumber = verseNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bookId);
        dest.writeString(this.bookName);
        dest.writeInt(this.chapterNumber);
        dest.writeString(this.verseNumber);
        dest.writeString(this.text);
        dest.writeLong(this.timeStamp);
        dest.writeString(this.searchId);
        dest.writeString(this.section);
        dest.writeInt(this.bookNumber);
        dest.writeString(this.languageCode);
        dest.writeString(this.versionCode);
    }

    protected SearchModel(Parcel in) {
        this.bookId = in.readString();
        this.bookName = in.readString();
        this.chapterNumber = in.readInt();
        this.verseNumber = in.readString();
        this.text = in.readString();
        this.timeStamp = in.readLong();
        this.searchId = in.readString();
        this.section = in.readString();
        this.bookNumber = in.readInt();
        this.languageCode = in.readString();
        this.versionCode = in.readString();
    }

    public static final Creator<SearchModel> CREATOR = new Creator<SearchModel>() {
        @Override
        public SearchModel createFromParcel(Parcel source) {
            return new SearchModel(source);
        }

        @Override
        public SearchModel[] newArray(int size) {
            return new SearchModel[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SearchModel &&
                this.bookId != null &&
                this.bookId.equals(((SearchModel) obj).bookId) &&
                this.chapterNumber == ((SearchModel) obj).chapterNumber &&
                this.verseNumber.equals(((SearchModel) obj).verseNumber);
    }

    @Override
    public int hashCode() {
        return (this.getBookId()+ " " + this.getChapterNumber()+ " " + this.getVerseNumber()).hashCode();
    }

    public static class BookNumberComparator implements Comparator<SearchModel> {

        @Override
        public int compare(SearchModel obj1, SearchModel obj2) {
            return (obj1.getBookNumber() < obj2.getBookNumber()) ? -1
                    : (obj1.getBookNumber() > obj2.getBookNumber()) ? 1
                    : (obj1.getChapterNumber() < obj2.getChapterNumber()) ? -1
                    : (obj1.getChapterNumber() >  obj2.getChapterNumber()) ? 1
                    : 0;
        }
    }
}