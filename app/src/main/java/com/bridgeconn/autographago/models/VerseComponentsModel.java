package com.bridgeconn.autographago.models;

import com.bridgeconn.autographago.utils.Constants;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by Admin on 23-12-2016.
 */

public class VerseComponentsModel extends RealmObject implements Comparable<VerseComponentsModel> {

    private String type;
    private int verseNumber;
    private String text;
    @Ignore
    private boolean added;
    @Ignore
    Constants.ParagraphMarker marker;

    public VerseComponentsModel(VerseComponentsModel model) {
        type = model.getType();
        verseNumber = model.getVerseNumber();
        text = model.getText();
        added = model.isAdded();
        marker = model.getMarker();
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

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public Constants.ParagraphMarker getMarker() {
        return marker;
    }

    public void setMarker(Constants.ParagraphMarker marker) {
        this.marker = marker;
    }

    @Override
    public int compareTo(VerseComponentsModel model) {
        if (verseNumber > model.verseNumber) {
            return 1;
        } else if (verseNumber == model.verseNumber) {
            return marker.compareTo(model.marker);
        }
        return -1;
    }

}