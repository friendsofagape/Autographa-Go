package com.bridgeconn.autographago.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class ChapterModel extends RealmObject {

    private int chapterNumber;
    @PrimaryKey
    private String chapterId;
    private String languageCode;
    private String versionCode;
    private int numberOfVerses;
    private RealmList<VerseComponentsModel> verseComponentsModels = new RealmList<>();
    @Ignore
    private boolean selected;
    @Ignore
    private RealmList<RealmString> selectedPositions = new RealmList<>();

    public ChapterModel(ChapterModel model) {
        chapterNumber = model.getChapterNumber();
        chapterId = model.getChapterId();
        languageCode = model.getLanguageCode();
        versionCode = model.getVersionCode();
        verseComponentsModels = model.getVerseComponentsModels();
        selected = model.isSelected();
        selectedPositions = model.getSelectedPositions();
        numberOfVerses = model.getNumberOfVerses();
    }

    public ChapterModel() {
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
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

    public RealmList<VerseComponentsModel> getVerseComponentsModels() {
        return verseComponentsModels;
    }

    public void setVerseComponentsModels(RealmList<VerseComponentsModel> verseComponentsModels) {
        this.verseComponentsModels = verseComponentsModels;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public RealmList<RealmString> getSelectedPositions() {
        return selectedPositions;
    }

    public void setSelectedPositions(RealmList<RealmString> selectedPositions) {
        this.selectedPositions = selectedPositions;
    }

    public int getNumberOfVerses() {
        return numberOfVerses;
    }

    public void setNumberOfVerses(int numberOfVerses) {
        this.numberOfVerses = numberOfVerses;
    }
}
