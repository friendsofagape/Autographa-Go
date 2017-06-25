package com.bridgeconn.autographago.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class LanguageModel extends RealmObject {

    @PrimaryKey
    private String languageCode;
    private String languageName;
    private RealmList<VersionModel> versionModels = new RealmList<>();
    @Ignore
    private boolean selected;

    public LanguageModel(LanguageModel model) {
        languageCode = model.getLanguageCode();
        languageName = model.getLanguageName();
        versionModels = model.getVersionModels();
        selected  = model.isSelected();
    }

    public LanguageModel() {
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public RealmList<VersionModel> getVersionModels() {
        return versionModels;
    }

    public void setVersionModels(RealmList<VersionModel> versionModels) {
        this.versionModels = versionModels;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
