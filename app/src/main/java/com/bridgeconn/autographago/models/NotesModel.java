package com.bridgeconn.autographago.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NotesModel extends RealmObject implements Parcelable {

    @PrimaryKey
    private long timestamp;
    private String title;
    private String text;
    private RealmList<NotesStyleModel> notesStyleModels = new RealmList<>();
    private RealmList<VerseIdModel> verseIds = new RealmList<>();
    private String languageCode;
    private String versionCode;

    public NotesModel(NotesModel model) {
        timestamp = model.getTimestamp();
        title = model.getTitle();
        text = model.getText();
        notesStyleModels = model.getNotesStyleModels();
        verseIds = model.getVerseIds();
        languageCode = model.getLanguageCode();
        versionCode = model.getVersionCode();
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

    public RealmList<NotesStyleModel> getNotesStyleModels() {
        return notesStyleModels;
    }

    public void setNotesStyleModels(RealmList<NotesStyleModel> notesStyleModels) {
        this.notesStyleModels = notesStyleModels;
    }

    public RealmList<VerseIdModel> getVerseIds() {
        return verseIds;
    }

    public void setVerseIds(RealmList<VerseIdModel> verseIds) {
        this.verseIds = verseIds;
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
        dest.writeLong(this.timestamp);
        dest.writeString(this.title);
        dest.writeString(this.text);
        dest.writeList(this.notesStyleModels);
        dest.writeList(this.verseIds);
        dest.writeString(this.languageCode);
        dest.writeString(this.versionCode);
    }

    protected NotesModel(Parcel in) {
        this.timestamp = in.readLong();
        this.title = in.readString();
        this.text = in.readString();
        this.notesStyleModels = new RealmList<>();
        in.readList(this.notesStyleModels, NotesStyleModel.class.getClassLoader());
        this.verseIds = new RealmList<>();
        in.readList(this.verseIds, VerseIdModel.class.getClassLoader());
        this.languageCode = in.readString();
        this.versionCode = in.readString();
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
        return obj instanceof NotesModel &&
                this.title != null &&
                this.text != null &&
                this.timestamp == ((NotesModel) obj).timestamp &&
                this.title.equals(((NotesModel) obj).title) &&
                this.text.equals(((NotesModel) obj).text) &&
                this.notesStyleModels.equals(((NotesModel) obj).notesStyleModels) &&
                this.verseIds.equals(((NotesModel) obj).verseIds) &&
                this.languageCode.equals(((NotesModel) obj).languageCode) &&
                this.versionCode.equals(((NotesModel) obj).versionCode);
    }

}