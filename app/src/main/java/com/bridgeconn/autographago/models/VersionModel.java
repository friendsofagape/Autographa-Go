package com.bridgeconn.autographago.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class VersionModel extends RealmObject {

    private String versionName;
    private String versionCode;
    private String languageCode;
    @PrimaryKey
    private String versionId;
    private RealmList<BookModel> bookModels = new RealmList<>();
    private String source;
    private String license;
    private int year;
    @Ignore
    private boolean selected;

    public VersionModel(VersionModel model) {
        versionName = model.getVersionName();
        versionCode = model.getVersionCode();
        bookModels = model.getBookModels();
        languageCode = model.getLanguageCode();
        versionId = model.getVersionId();
        source = model.getSource();
        license = model.getLicense();
        year = model.getYear();
        selected = model.isSelected();
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

    public RealmList<BookModel> getBookModels() {
        return bookModels;
    }

    public void setBookModels(RealmList<BookModel> bookModels) {
        this.bookModels = bookModels;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
