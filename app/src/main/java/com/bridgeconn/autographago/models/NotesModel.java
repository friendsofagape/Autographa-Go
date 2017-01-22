package com.bridgeconn.autographago.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NotesModel extends RealmObject {

    @PrimaryKey
    private long timestamp;
    private String title;
    private String text;
    private RealmList<RealmString> verseIds = new RealmList<>();

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

    public RealmList<RealmString> getVerseIds() {
        return verseIds;
    }

    public void setVerseIds(RealmList<RealmString> verseIds) {
        this.verseIds = verseIds;
    }
}
