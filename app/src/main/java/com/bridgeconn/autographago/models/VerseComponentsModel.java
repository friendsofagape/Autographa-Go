package com.bridgeconn.autographago.models;

import android.support.annotation.NonNull;

import com.bridgeconn.autographago.utils.Constants;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;

public class VerseComponentsModel extends RealmObject {

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
    private String languageCode;
    private String versionCode;

    public VerseComponentsModel(VerseComponentsModel model) {
        chapterId = model.getChapterId();
        type = model.getType();
        verseNumber = model.getVerseNumber();
        text = model.getText();
        added = model.isAdded();
        marker = model.getMarker();
        selected = model.isSelected();
        highlighted = model.isHighlighted();
        languageCode = model.getLanguageCode();
        versionCode = model.getVersionCode();
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

}