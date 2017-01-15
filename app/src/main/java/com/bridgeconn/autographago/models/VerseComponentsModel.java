package com.bridgeconn.autographago.models;

import com.bridgeconn.autographago.utils.Constants;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;

public class VerseComponentsModel extends RealmObject implements Comparable<VerseComponentsModel> {

    private String chapterId;
    private String type;
    @Index
    private int verseNumber;
    private String text;
    @Ignore
    private boolean added;
    @Ignore
    private Constants.ParagraphMarker marker;
    @Ignore
    private boolean selected;

    public VerseComponentsModel(VerseComponentsModel model) {
        chapterId = model.getChapterId();
        type = model.getType();
        verseNumber = model.getVerseNumber();
        text = model.getText();
        added = model.isAdded();
        marker = model.getMarker();
        selected = model.isSelected();
    }

    public VerseComponentsModel() {
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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