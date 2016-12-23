package com.bridgeconn.autographago.models;

import io.realm.RealmObject;

/**
 * Created by Admin on 23-12-2016.
 */

public class VerseComponentsModel extends RealmObject {

    private String type;
    private int verseNumber;
    private String text;

    public VerseComponentsModel(VerseComponentsModel model) {
        type = model.getType();
        verseNumber = model.getVerseNumber();
        text = model.getText();
    }

    public VerseComponentsModel() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getVerseNumber() {
        return verseNumber;
    }

    public void setVerseNumber(int verseNumber) {
        this.verseNumber = verseNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}