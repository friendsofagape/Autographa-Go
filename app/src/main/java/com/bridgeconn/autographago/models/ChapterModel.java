package com.bridgeconn.autographago.models;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Admin on 16-12-2016.
 */

public class ChapterModel extends RealmObject {

    private int chapterNumber;
    @PrimaryKey
    private String chapterId;
    private List<VerseComponentsModel> verseComponentsModels = new ArrayList<>();

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

    public List<VerseComponentsModel> getVerseComponentsModels() {
        return verseComponentsModels;
    }

    public void setVerseComponentsModels(List<VerseComponentsModel> verseComponentsModels) {
        this.verseComponentsModels = verseComponentsModels;
    }
}
