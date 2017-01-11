package com.bridgeconn.autographago.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BookmarkModel extends RealmObject {

    @PrimaryKey
    private String chapterId;

    public BookmarkModel(BookmarkModel model) {
        chapterId = model.getChapterId();
    }

    public BookmarkModel() {
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }
}
