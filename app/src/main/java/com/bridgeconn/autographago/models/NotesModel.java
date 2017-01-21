package com.bridgeconn.autographago.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NotesModel extends RealmObject {

    @PrimaryKey
    private String title;
    private String text;
    private RealmList<RealmString> verseIds = new RealmList<>();

    public NotesModel(NotesModel model) {
        title = model.getTitle();
        text = model.getText();
        verseIds = model.getVerseIds();
    }

    public NotesModel() {
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
