package com.bridgeconn.autographago.models;

public class MetaDataResponseModel {

    private String languageName;
    private String languageCode;
    private String versionCode;
    private String versionName;
    private String source;
    private String license;
    private int year;
    private String available;

    public MetaDataResponseModel(String languageName, String languageCode, String versionCode, String versionName, String source, String license, int year, String available) {
        this.languageName = languageName;
        this.languageCode = languageCode;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.source = source;
        this.license = license;
        this.year = year;
        this.available = available;
    }

    public MetaDataResponseModel() {
    }

    public MetaDataResponseModel(MetaDataResponseModel model) {
        languageName = model.getLanguageName();
        languageCode = model.getLanguageCode();
        versionCode = model.getVersionCode();
        versionName = model.getVersionName();
        source = model.getSource();
        license = model.getLicense();
        year = model.getYear();
        available = model.getAvailable();
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

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
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

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }
}
