package com.bridgeconn.autographago.models;

public class SpinnerModel {

    private String languageName;
    private String languageCode;
    private String versionCode;

    public SpinnerModel() {
    }

    public SpinnerModel(SpinnerModel model) {
        languageName = model.getLanguageName();
        languageCode = model.getLanguageCode();
        versionCode = model.getVersionCode();
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SpinnerModel &&
                this.languageName != null && this.languageCode != null && this.versionCode != null &&
                this.languageName.equals(((SpinnerModel) obj).languageName) &&
                this.languageCode.equals(((SpinnerModel) obj).languageCode) &&
                this.versionCode.equals(((SpinnerModel) obj).versionCode);
    }
}
