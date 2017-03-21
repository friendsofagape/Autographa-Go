package com.bridgeconn.autographago.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SearchHistoryModel extends RealmObject {

    @PrimaryKey
    private String searchText;
    private long lastSearchTime;
    private long searchCount;

    public SearchHistoryModel() {
    }

    public SearchHistoryModel(SearchHistoryModel model) {
        searchText = model.getSearchText();
        lastSearchTime = model.getLastSearchTime();
        searchCount = model.getSearchCount();
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public long getLastSearchTime() {
        return lastSearchTime;
    }

    public void setLastSearchTime(long lastSearchTime) {
        this.lastSearchTime = lastSearchTime;
    }

    public long getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(long searchCount) {
        this.searchCount = searchCount;
    }

}
