package com.bridgeconn.autographago.models;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Admin on 20-12-2016.
 */

public class LanguageModel extends RealmObject {

    @PrimaryKey
    private String languageCode;
    private String languageName;
    private List<VersionModel> versionModels;

    public LanguageModel(LanguageModel model) {
        languageCode = model.getLanguageCode();
        languageName = model.getLanguageName();
        versionModels = model.getVersionModels();
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

    public List<VersionModel> getVersionModels() {
        return versionModels;
    }

    public void setVersionModels(List<VersionModel> versionModels) {
        this.versionModels = versionModels;
    }
}
