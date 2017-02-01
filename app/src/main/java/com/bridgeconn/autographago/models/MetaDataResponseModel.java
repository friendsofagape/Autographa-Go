package com.bridgeconn.autographago.models;

public class MetaDataResponseModel {

    private String language;
    private String Version;
    private String Source;
    private String License;
    private int Year;

    public MetaDataResponseModel(String language, String version, String source, String license, int year) {
        this.language = language;
        this.Version = version;
        this.Source = source;
        this.License = license;
        this.Year = year;
    }

    public MetaDataResponseModel() {
    }

    public MetaDataResponseModel(MetaDataResponseModel model) {
        language = model.getLanguage();
        Version = model.getVersion();
        Source = model.getSource();
        License = model.getLicense();
        Year = model.getYear();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        this.Version = version;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        this.Source = source;
    }

    public String getLicense() {
        return License;
    }

    public void setLicense(String license) {
        this.License = license;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        this.Year = year;
    }
}
