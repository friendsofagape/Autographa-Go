package com.bridgeconn.autographago.models;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NotesModel extends RealmObject implements Parcelable {

    @PrimaryKey
    private long timestamp;
    private String title;
    private String text;
    private RealmList<SearchModel> verseIds = new RealmList<>();

    public NotesModel(NotesModel model) {
        timestamp = model.getTimestamp();
        title = model.getTitle();
        text = model.getText();
        verseIds = model.getVerseIds();
    }

    public NotesModel() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public RealmList<SearchModel> getVerseIds() {
        return verseIds;
    }

    public void setVerseIds(RealmList<SearchModel> verseIds) {
        this.verseIds = verseIds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.timestamp);
        dest.writeString(this.title);
        dest.writeString(this.text);
        dest.writeList(this.verseIds);
    }

    protected NotesModel(Parcel in) {
        this.timestamp = in.readLong();
        this.title = in.readString();
        this.text = in.readString();
        this.verseIds = new RealmList<>();
        in.readList(this.verseIds, SearchModel.class.getClassLoader());
    }

    public static final Creator<NotesModel> CREATOR = new Creator<NotesModel>() {
        @Override
        public NotesModel createFromParcel(Parcel source) {
            return new NotesModel(source);
        }

        @Override
        public NotesModel[] newArray(int size) {
            return new NotesModel[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SearchModel &&
                this.title != null &&
                this.text != null &&
                this.timestamp == ((NotesModel) obj).timestamp &&
                this.title.equals(((NotesModel) obj).title) &&
                this.text.equals(((NotesModel) obj).text) &&
                this.verseIds.equals(((NotesModel) obj).verseIds);
    }

}