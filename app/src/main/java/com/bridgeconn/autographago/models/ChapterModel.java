package com.bridgeconn.autographago.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Admin on 16-12-2016.
 */

public class ChapterModel extends RealmObject {

    private int chapterNumber;
    @PrimaryKey
    private String chapterId;
    private RealmList<VerseComponentsModel> verseComponentsModels = new RealmList<>();

    public ChapterModel(ChapterModel model) {
        chapterNumber = model.getChapterNumber();
        chapterId = model.getChapterId();
        verseComponentsModels = model.getVerseComponentsModels();
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

    public RealmList<VerseComponentsModel> getVerseComponentsModels() {
        return verseComponentsModels;
    }

    public void setVerseComponentsModels(RealmList<VerseComponentsModel> verseComponentsModels) {
        this.verseComponentsModels = verseComponentsModels;
    }
}
