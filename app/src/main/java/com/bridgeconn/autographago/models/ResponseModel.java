package com.bridgeconn.autographago.models;

import java.util.List;

public class ResponseModel {

    private MetaDataResponseModel meta_data;
    private List<String> list_of_versions_available;
    private List<String> languages_available;

    public ResponseModel(MetaDataResponseModel metaData, List<String> listOfVersionsAvailable, List<String> languagesAvailable) {
        this.meta_data = metaData;
        this.list_of_versions_available = listOfVersionsAvailable;
        this.languages_available = languagesAvailable;
    }

    public ResponseModel() {
    }

    public ResponseModel(ResponseModel model) {
        meta_data = model.getMetaData();
        list_of_versions_available = model.getListOfVersionsAvailable();
        languages_available = model.getLanguagesAvailable();
    }

    public MetaDataResponseModel getMetaData() {
        return meta_data;
    }

    public void setMetaData(MetaDataResponseModel metaData) {
        this.meta_data = metaData;
    }

    public List<String> getListOfVersionsAvailable() {
        return list_of_versions_available;
    }

    public void setListOfVersionsAvailable(List<String> listOfVersionsAvailable) {
        this.list_of_versions_available = listOfVersionsAvailable;
    }

    public List<String> getLanguagesAvailable() {
        return languages_available;
    }

    public void setLanguagesAvailable(List<String> languagesAvailable) {
        this.languages_available = languagesAvailable;
    }
}
