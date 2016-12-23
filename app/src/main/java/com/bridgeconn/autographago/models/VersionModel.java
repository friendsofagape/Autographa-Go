package com.bridgeconn.autographago.models;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by Admin on 20-12-2016.
 */

public class VersionModel extends RealmObject {

    private String versionName;
    private String versionCode;
    private List<BookModel> bookModels = new ArrayList<>();

    public VersionModel(VersionModel model) {
        versionName = model.getVersionName();
        versionCode = model.getVersionCode();
        bookModels = model.getBookModels();
    }

    public VersionModel() {
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public List<BookModel> getBookModels() {
        return bookModels;
    }

    public void setBookModels(List<BookModel> bookModels) {
        this.bookModels = bookModels;
    }
}
