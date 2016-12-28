package com.bridgeconn.autographago.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 20-12-2016.
 */

public class AllLanguagesModel {

    private List<LanguageModel> languageModelList = new ArrayList<>();

    public AllLanguagesModel(AllLanguagesModel model) {
        languageModelList = model.getLanguageModelList();
    }

    public AllLanguagesModel() {
    }

    public List<LanguageModel> getLanguageModelList() {
        return languageModelList;
    }

    public void setLanguageModelList(List<LanguageModel> languageModelList) {
        this.languageModelList = languageModelList;
    }
}
