package com.bridgeconn.autographago.models;

import android.support.annotation.NonNull;

import com.bridgeconn.autographago.utils.Constants;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;

public class VerseComponentsModel extends RealmObject implements Comparable<VerseComponentsModel> {

    @Index
    @NonNull
    private String chapterId;
    private String type;
    @Index
    private String verseNumber;
    private String text;
    @Ignore
    private boolean added;
    @Ignore
    private Constants.ParagraphMarker marker;
    @Ignore
    private boolean selected;
    private boolean highlighted;

    public VerseComponentsModel(VerseComponentsModel model) {
        chapterId = model.getChapterId();
        type = model.getType();
        verseNumber = model.getVerseNumber();
        text = model.getText();
        added = model.isAdded();
        marker = model.getMarker();
        selected = model.isSelected();
        highlighted = model.isHighlighted();
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

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    @Override
    public int compareTo(VerseComponentsModel model) {
        if (verseNumber.compareTo(model.verseNumber) > 0) {
            return 1;
        } else if (verseNumber.compareTo(model.verseNumber) == 0) {
            return marker.compareTo(model.marker);
        }
        return -1;
    }

}